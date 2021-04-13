package cn.allwayz.product.service.impl;

import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.Query;
import cn.allwayz.product.dao.CategoryDao;
import cn.allwayz.product.entity.CategoryEntity;
import cn.allwayz.product.service.CategoryBrandRelationService;
import cn.allwayz.product.service.CategoryService;
import cn.allwayz.product.vo.Catelog2VO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author allwayz
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Resource
    CategoryBrandRelationService categoryBrandRelationService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redisson;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、Find all categories
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        //2. Assemble the parent-child structure
        //2.1 Find all first-level classifications
        List<CategoryEntity> level1Menus = categoryEntities.stream().filter(categoryEntity->
                categoryEntity.getParentCid() == 0
        ).map(menu->{
            menu.setChildren(getChildren(menu,categoryEntities));
            return menu;
        }).sorted((menu1,menu2)->{
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);

        Collections.reverse(parentPath);

        return parentPath.toArray(new Long[parentPath.size()]);
    }

    @Transactional
    //@CacheEvict(value = "category", allEntries = true)
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    //@Cacheable(value = {"category"}, key = "#root.method.name", sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    //@Cacheable(value = {"category"}, key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2VO>> getCatalogJson() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        List<CategoryEntity> level1Categories = getParent_cid(selectList, 0L);

        return level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2VO> catelog2VOS = null;
            if (!CollectionUtils.isEmpty(categoryEntities)) {
                catelog2VOS = categoryEntities.stream().map(l2 -> {
                    Catelog2VO catelog2VO = new Catelog2VO(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    List<CategoryEntity> level3Catalog = getParent_cid(selectList, l2.getCatId());
                    if (!CollectionUtils.isEmpty(level3Catalog)) {
                        List<Catelog2VO.Catelog3VO> collect = level3Catalog.stream().map(l3 -> {
                            Catelog2VO.Catelog3VO catelog3VO = new Catelog2VO.Catelog3VO(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3VO;
                        }).collect(Collectors.toList());
                        catelog2VO.setCatalog3List(collect);
                    }
                    return catelog2VO;
                }).collect(Collectors.toList());
            }
            return catelog2VOS;
        }));
    }

    /*
    ==========================================================================================================================================================================
    ==========================================================================================================================================================================
    ==========================================================================================================================================================================
     */

    /**
     * Use Redis to cache product classification data
     *
     * @return
     */
    public Map<String, List<Catelog2VO>> getCatalogJson2() throws InterruptedException {
        // TODO： OutOfDirectMemoryError
        /*
         * 1. After SpringBoot2.0, lettuce is used as the default client to operate Redis, and lettuce uses Netty for network communication
         * 2. Lettuce bug causes out-of-heap memory overflow in Netty -Xmx300m Netty will use the parameters set by JVM if no external memory is specified
         * can use - Dio.net ty. MaxDirectMemory set outside the heap memory
         * solutions: can't just use - Dio.net. Ty maxDirectMemory to pile of external memory
         * 1. Upgrade the lettuce client 2. Switch to using Jedis
         *
         * Redistemplate encapsulates both lettuce and jedis, so see the RedisAutoConfiguration class for details
         *
         *
         * - Null result cache: Resolves Cache Penetration
         *
         * - Set expiration time (plus random value) : Solve Cache Avalanche
         *
         * - Locking: Resolves Hotspot Invalid
         */

        // To put a JSON string in the cache, the fetched JSON string also needs to be reversed to a usable object type

        // 1. Add cache logic. The data stored in the cache is a JSON string
        String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            // 2 Query the database if the cache is not hit
            // 4 Returns the data queried from the database
            return getCatalogJsonFromDBWithRedisLock();
        }

        return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2VO>>>() {});
    }

    public Map<String, List<Catelog2VO>> getCatalogJsonFromDBWithRedissonLock() {

        RLock lock = redisson.getLock("catalogJson-lock");

        lock.lock();
        Map<String, List<Catelog2VO>> dataFromDB;
        try {
            dataFromDB = getDataFromDB();
        } finally {
            lock.unlock();
        }
        return dataFromDB;
    }

    /**
     * Redis Implementing Distributed Locking
     *
     * @return
     */
    public Map<String, List<Catelog2VO>> getCatalogJsonFromDBWithRedisLock() throws InterruptedException {
        String uuid = UUID.randomUUID().toString();
        Boolean lockResult = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lockResult) {
            Map<String, List<Catelog2VO>> dataFromDB;
            try {
                dataFromDB = getDataFromDB();
            } finally {
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList("lock"), uuid);
            }
            return dataFromDB;
        } else {
            Thread.sleep(100);
            return getCatalogJsonFromDBWithRedisLock();
        }
    }


    private Map<String, List<Catelog2VO>> getDataFromDB() {
        String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2VO>>>() {
            });

        }

        List<CategoryEntity> selectList = baseMapper.selectList(null);
        List<CategoryEntity> level1Categories = getParent_cid(selectList, 0L);

        Map<String, List<Catelog2VO>> parentCid = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2VO> catelog2VOS = null;
            if (!CollectionUtils.isEmpty(categoryEntities)) {
                catelog2VOS = categoryEntities.stream().map(l2 -> {
                    Catelog2VO catelog2VO = new Catelog2VO(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    List<CategoryEntity> level3Catalog = getParent_cid(selectList, l2.getCatId());
                    if (!CollectionUtils.isEmpty(level3Catalog)) {
                        List<Catelog2VO.Catelog3VO> collect = level3Catalog.stream().map(l3 -> new Catelog2VO.Catelog3VO(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName())).collect(Collectors.toList());
                        catelog2VO.setCatalog3List(collect);
                    }
                    return catelog2VO;
                }).collect(Collectors.toList());
            }
            return catelog2VOS;
        }));
        String cache = JSON.toJSONString(parentCid);
        stringRedisTemplate.opsForValue().set("catalogJSON", cache, 1, TimeUnit.DAYS);
        return parentCid;
    }

    /**
     * Query and encapsulate product classification data from the database
     *
     * @return
     */
    public Map<String, List<Catelog2VO>> getCatalogJsonFromDBWithLocalLock() {
        /**
        * optimize
        * 1. Change multiple database queries into one query
        *
        * SpringBoot all components are singletons by default in the container and can be locked using synchronized (this)
        */
        synchronized (this) {
            /**
             * After you get the lock, you should check it again in the cache. If not, you need to continue the query
             *
             * If there are 100W concurrent requests, the first request to obtain the lock will start the query, and the other requests will queue for the lock
             * Wait until the lock is obtained before executing the query, but it is possible that the previous lock request has been successfully queried and the result has been added to the cache
             */
            return getDataFromDB();
        }
    }


    /**
     *
     * @param root
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root,List<CategoryEntity> all){
        List<CategoryEntity> childrenList = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            //Find the submenu
            categoryEntity.setChildren(getChildren(categoryEntity,all));
            return categoryEntity;
        }).sorted((menu1,menu2)->{
            //Sort menu
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return childrenList;
    }

    /**
     *
     *
     * @param selectList
     * @param parentCid
     * @return
     */
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parentCid) {
        return selectList.stream().filter(o -> o.getParentCid().equals(parentCid)).collect(Collectors.toList());
    }

    /**
     *
     * @param catelogId
     * @param paths
     * @return
     */
    private List<Long> findParentPath(Long catelogId,List<Long> paths){
        //1、Collect the current node ID
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if(byId.getParentCid()!=0){
            findParentPath(byId.getParentCid(),paths);
        }
        return paths;
    }


}
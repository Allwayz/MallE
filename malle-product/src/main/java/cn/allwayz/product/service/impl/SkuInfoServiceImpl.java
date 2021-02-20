package cn.allwayz.product.service.impl;

import cn.allwayz.common.exception.BizCodeEnum;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.Query;
import cn.allwayz.product.dao.SkuInfoDao;
import cn.allwayz.product.entity.SkuImagesEntity;
import cn.allwayz.product.entity.SkuInfoEntity;
import cn.allwayz.product.entity.SpuInfoDescEntity;
import cn.allwayz.product.feign.WareFeignService;
import cn.allwayz.product.service.*;
import cn.allwayz.product.vo.ItemAttrGroupWithAttrVO;
import cn.allwayz.product.vo.ItemDetailVO;
import cn.allwayz.product.vo.ItemSaleAttrVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private WareFeignService wareFeignService;
    @Resource
    private SkuImagesService skuImagesService;
    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Resource
    private SpuInfoDescService spuInfoDescService;
    @Resource
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        log.info(params.toString());
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        /**
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("sku_id",key).or().like("sku_name",key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            queryWrapper.eq("catalog_id",catelogId);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(catelogId)){
            queryWrapper.eq("brand_id",brandId);
        }

        String min = (String) params.get("min");
        if(!StringUtils.isEmpty(min)){
            queryWrapper.ge("price",min);
        }
        String max = (String) params.get("max");
        if(!StringUtils.isEmpty(max)  ){
            try{
                BigDecimal bigDecimal = new BigDecimal(max);

                if(bigDecimal.compareTo(new BigDecimal("0"))==1){
                    queryWrapper.le("price",max);
                }
            }catch (Exception e){
            }
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    @Override
    public ItemDetailVO detail(Long skuId) {
        ItemDetailVO itemDetailVO = new ItemDetailVO();
        if (itemDetailVO == null) {
            BizCodeEnum.PRODUCT_PARAM_INVAILD.getMsg();
        }
        // TODO 1.sku基本信息 接收返回值，后面的异步任务需要用到这个结果
        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfoEntity = this.getById(skuId);
            itemDetailVO.setSkuInfo(skuInfoEntity);
            return skuInfoEntity;
        }, executor);
        // TODO 6.商品是否有库存，与1无关，与1并列
        CompletableFuture<Void> skuStockFuture = CompletableFuture.runAsync(() -> {
//            R r = wareFeignService.getSkuStock(skuId);
//            if (r.getCode() == 0) {
//                Long stock = r.getData(Long.class);
//                itemDetailVO.setHasStock(stock > 0);
//            }
            itemDetailVO.setHasStock(true);
        }, executor);
        // TODO 2.此sku的图片集 不需要1的结果，也无返回值，与6并列
        CompletableFuture<Void> skuImageFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> skuImagesEntities = skuImagesService.listBySkuId(skuId);
            itemDetailVO.setSkuImages(skuImagesEntities);
        }, executor);
        // TODO 3.同商品所有销售销售属性及其对应值，需要1的结果 1.thenAcceptAsync
        CompletableFuture<Void> skuSaleAttrFuture = skuInfoFuture.thenAcceptAsync((skuInfoEntity) -> {
            List<ItemSaleAttrVO> saleAttrs = skuSaleAttrValueService.allAttrValueWithSkuBySpuId(skuInfoEntity.getSpuId());
            itemDetailVO.setSaleAttrs(saleAttrs);
        }, executor);
        // TODO 4.商品描述信息(spu)，需要1的结果，1.thenAcceptAsync，与3并列
        CompletableFuture<Void> spuDescFuture = skuInfoFuture.thenAcceptAsync((skuInfoEntity) -> {
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getBySpuId(skuInfoEntity.getSpuId());
            itemDetailVO.setSpuDesc(spuInfoDescEntity);
        }, executor);
        // TODO 5.商品规格参数(spu，属性分组与属性)，需要1的结果，1.thenAcceptAsync，与3,4并列
        CompletableFuture<Void> spuAttrGroupFuture = skuInfoFuture.thenAcceptAsync((skuInfoEntity) -> {
            List<ItemAttrGroupWithAttrVO> itemAttrGroupWithAttrVOS = productAttrValueService.getAttrsWithAttrGroupBySpuId(skuInfoEntity.getSpuId());
            itemDetailVO.setSpuAttrGroups(itemAttrGroupWithAttrVOS);
        }, executor);
        // TODO 7.当前商品参与的秒杀最近一场活动，不需要1的结果，也无返回值，与2,6并列
//        CompletableFuture<Void> skuSeckillFuture = skuInfoFuture.runAsync(() -> {
//            R r = seckillFeignService.getSkuSeckillInfo(skuId);
//            if (r.getCode() != 0) {
//                log.warn("gulimall-product调用gulimall-seckill获取秒杀信息失败");
//            } else {
//                SeckillSkuTO seckillSkuTO = r.getData(SeckillSkuTO.class);
//                itemDetailVO.setSeckillInfo(seckillSkuTO);
//            }
//        }, executor);
        // 等待所有异步任务执行完成,345在1之后执行，所以345完成1肯定完成，不用等1
        try {
            CompletableFuture.allOf(skuStockFuture, skuImageFuture, skuSaleAttrFuture, spuDescFuture, spuAttrGroupFuture).get();
        } catch (Exception e) {
            log.error("线程池异步编排任务失败：{}", e.getCause());
            BizCodeEnum.THREAD_POOL_TASK_FAILED.getMsg();
        }
        return itemDetailVO;
    }

    private boolean isValidId(String key) {
        return !StringUtils.isEmpty(key) && !"0".equalsIgnoreCase(key);
    }

}
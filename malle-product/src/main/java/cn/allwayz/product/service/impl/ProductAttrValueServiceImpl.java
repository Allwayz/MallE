package cn.allwayz.product.service.impl;

import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.Query;
import cn.allwayz.product.dao.ProductAttrValueDao;
import cn.allwayz.product.entity.ProductAttrValueEntity;
import cn.allwayz.product.entity.SpuInfoEntity;
import cn.allwayz.product.service.ProductAttrValueService;
import cn.allwayz.product.service.SpuInfoService;
import cn.allwayz.product.vo.ItemAttrGroupWithAttrVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Resource
    private SpuInfoService spuInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveProductAttr(List<ProductAttrValueEntity> collect) {
        this.saveBatch(collect);
    }

    @Override
    public List<ProductAttrValueEntity> baseAttrlistforspu(Long spuId) {
        List<ProductAttrValueEntity> entities = this.baseMapper.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return entities;
    }

    @Transactional
    @Override
    public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities) {
        //1、Remove all properties that correspond to this spuId
        this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));
        List<ProductAttrValueEntity> collect = entities.stream().map(item -> {
            item.setSpuId(spuId);
            return item;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

    @Override
    public List<ItemAttrGroupWithAttrVO> getAttrsWithAttrGroupBySpuId(Long spuId) {
        SpuInfoEntity spuInfoEntity = spuInfoService.getById(spuId);
        return this.baseMapper.getAttrsWithAttrGroup(spuId, spuInfoEntity.getCatalogId());

    }
}
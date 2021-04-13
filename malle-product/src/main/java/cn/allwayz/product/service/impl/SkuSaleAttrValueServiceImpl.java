package cn.allwayz.product.service.impl;

import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.Query;
import cn.allwayz.product.dao.SkuSaleAttrValueDao;
import cn.allwayz.product.entity.SkuSaleAttrValueEntity;
import cn.allwayz.product.service.SkuSaleAttrValueService;
import cn.allwayz.product.vo.ItemSaleAttrVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public List<SkuSaleAttrValueEntity> listBySkuId(Long skuId) {

        return this.list(new QueryWrapper<SkuSaleAttrValueEntity>().eq("sku_id", skuId));
    }

    /**
     * Specify all sales properties of the SPU and their values (multiple values, separated by commas, and collectively a String)
     *
     * @param spuId
     * @return
     */
    @Override
    public List<ItemSaleAttrVO> allAttrValueWithSkuBySpuId(Long spuId) {
        return this.baseMapper.allAttrValueWithSkuBySpuId(spuId);
    }

    @Override
    public List<String> stringListBySkuId(Long skuId) {
        return this.baseMapper.stringListBySkuId(skuId);
    }

}
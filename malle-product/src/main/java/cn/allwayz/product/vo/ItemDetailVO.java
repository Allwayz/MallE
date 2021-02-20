package cn.allwayz.product.vo;

import cn.allwayz.product.entity.SkuImagesEntity;
import cn.allwayz.product.entity.SkuInfoEntity;
import cn.allwayz.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author allwayz
 */
@Data
public class ItemDetailVO {

    // sku基本信息
    private SkuInfoEntity skuInfo;

    // 此sku是否有库存
    private boolean hasStock = false;

    // 此sku的图片集
    private List<SkuImagesEntity> skuImages;

    // 同款商品所有销售属性组合
    private List<ItemSaleAttrVO> saleAttrs;

    // 商品描述信息(spu)
    private SpuInfoDescEntity spuDesc;

    // 商品规格参数(spu，属性分组与属性)
    private List<ItemAttrGroupWithAttrVO> spuAttrGroups;

    // 商品参与的最近场次秒杀信息
    //private SeckillSkuTO seckillInfo;
}

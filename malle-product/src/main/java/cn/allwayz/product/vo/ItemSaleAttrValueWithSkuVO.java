package cn.allwayz.product.vo;

import lombok.Data;

/**
 * @author allwayz
 */
@Data
public class ItemSaleAttrValueWithSkuVO {

    String attrValue;
    String skuIds; // 逗号分隔的多个skuId
}

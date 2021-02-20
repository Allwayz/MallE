package cn.allwayz.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author allwayz
 */
@Data
public class ItemSaleAttrVO {

    /**
     * attr_id
     */
    private Long attrId;

    /**
     * 属性名
     */
    private String attrName;

    /**
     * 每种属性值和skuIds的对应关系
     */
    List<ItemSaleAttrValueWithSkuVO> attrValueWithSkus;

}

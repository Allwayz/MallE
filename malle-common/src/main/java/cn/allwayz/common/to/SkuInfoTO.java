package cn.allwayz.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author allwayz
 */
@Data
public class SkuInfoTO {

    private Long skuId;
    /**
     * spuId
     */
    private Long spuId;
    /**
     * skuName
     */
    private String skuName;
    /**
     * skuDesc
     */
    private String skuDesc;
    /**
     * catelogId
     */
    private Long catelogId;
    /**
     * brandId
     */
    private Long brandId;
    /**
     * skuDefaultImg
     */
    private String skuDefaultImg;
    /**
     * skuTitle
     */
    private String skuTitle;
    /**
     * skuSubtitle
     */
    private String skuSubtitle;
    /**
     * price
     */
    private BigDecimal price;
    /**
     * saleCount
     */
    private Long saleCount;
}


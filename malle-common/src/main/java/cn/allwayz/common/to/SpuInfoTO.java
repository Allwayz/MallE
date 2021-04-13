package cn.allwayz.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SpuInfoTO {

    private Long id;
    /**
     * 商品名称
     */
    private String spuName;
    /**
     * 商品描述
     */
    private String spuDescription;
    /**
     * 所属分类id
     */
    private Long catelogId;
    /**
     * catelogName
     */
    private String catelogName;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * brandName
     */
    private String brandName;
    /**
     * weight
     */
    private BigDecimal weight;
    /**
     * publishStatus
     */
    private Integer publishStatus;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * growBounds
     */
    private BigDecimal growBounds;

    /**
     * integration
     */
    private BigDecimal integration;
}


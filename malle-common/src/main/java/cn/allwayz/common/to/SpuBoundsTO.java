package cn.allwayz.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author allwayz
 */
@Data
public class SpuBoundsTO {
    /**
     * spuId
     */
    private Long spuId;
    /**
     * 成长积分
     */
    private BigDecimal growBounds;
    /**
     * 购物积分
     */
    private BigDecimal buyBounds;

}


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
     * growBounds
     */
    private BigDecimal growBounds;
    /**
     * buyBounds
     */
    private BigDecimal buyBounds;

}


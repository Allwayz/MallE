package cn.allwayz.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author allwayz
 */
@Data
public class CartItemTO {

    private Long skuId;

    private String skuImg;

    private String skuTitle;

    private List<String> attrs;

    private BigDecimal price;

    private Integer count;

    private BigDecimal totalPrice;
}

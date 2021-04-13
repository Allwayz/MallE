package cn.allwayz.cart.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author allwayz
 * 2021/1/16 12:08
 *
 * Each item in the shopping cart ------ is used to view the shopping cart list
 *
 * For the property that needs to be evaluated, override its get method to ensure that each time or this property value is recalculated to the latest value
 */
public class CartItemVO {

    @Getter
    @Setter
    private Long skuId;
    @Getter @Setter
    private Boolean checked;
    @Getter @Setter
    private String skuImg;
    @Getter @Setter
    private String skuTitle;
    @Getter @Setter
    private List<String> attrs;
    @Getter @Setter
    private BigDecimal price;
    @Getter @Setter
    private Integer count;
    @Setter
    private BigDecimal totalPrice;

    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(count.toString()));
    }
}

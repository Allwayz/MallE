package cn.allwayz.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author allwayz
 */
@Data
public class OrderSubmitVO {

    private Long addressId;

    // 支付方式，写死，在线支付(最终选用支付宝)
    private Integer payType = 1;

    private BigDecimal totalPrice;

    private String orderToken;

    // 发票/快递/优惠  不做

    // 订单项，再去查询购物车，获取最新选项
}

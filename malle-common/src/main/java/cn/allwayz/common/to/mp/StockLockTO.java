package cn.allwayz.common.to.mp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author allwayz
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StockLockTO {

    // 库存工作单详情id
    private Long taskDetailId;

    // 订单id
    private String orderSn;

    // 商品id
    private Long skuId;

    // 仓库id
    private Long wareId;

    // 数量
    private Integer count;
}


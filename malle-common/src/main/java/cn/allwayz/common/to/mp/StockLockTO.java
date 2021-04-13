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

    // Inventory work order detail ID
    private Long taskDetailId;

    // The order id
    private String orderSn;

    // product id
    private Long skuId;

    // warehouse id
    private Long wareId;

    // number
    private Integer count;
}


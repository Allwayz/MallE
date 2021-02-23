package cn.allwayz.common.to;

import lombok.Data;

import java.util.List;

@Data
public class OrderLockStockTO {

    private String orderSn;

    private List<SkuLockStock> locks;

    @Data
    public static class SkuLockStock {
        private Long skuId;

        private String skuName;

        private Integer count;
    }
}

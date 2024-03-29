package cn.allwayz.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderSkuVO {

    @Getter
    @Setter
    private Long skuId;
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
    @Getter @Setter
    private Long stock;
    @Setter
    private BigDecimal totalPrice;

    // 调用get方法自动计算总价
    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(count.toString()));
    }
}

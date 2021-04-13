package cn.allwayz.cart.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author allwayz
 * Shopping cart -- Used to view the total amount on the billing page
 *
 * Override the get method for the property to be evaluated to ensure that each time or value of the property is recalculated to the latest value
 *
 * This way we only need to focus on items, and the other properties are automatically evaluated every time they get a value
 */
public class CartVO {

    /**
     * Total shopping items
     */
    @Getter
    @Setter
    private List<CartItemVO> items;

    /**
     * A few items altogether
     */
    @Setter
    private Integer totalCount;

    /**
     * There are several types of goods
     */
    @Setter
    private Integer totalType;

    /**
     * The total price
     */
    @Setter
    private BigDecimal totalPrice;

    /**
     * The total amount of remission is written here first
     */
    @Getter @Setter
    private BigDecimal totalReduce = new BigDecimal("0");

    public Integer getTotalCount() {
        if (!CollectionUtils.isEmpty(items)) {
            return items.stream().mapToInt(CartItemVO::getCount).sum();
        }
        return 0;
    }

    /**
     * There are several types of goods
     * @return
     */
    public Integer getTotalType() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    /**
     * Current total price of [selected] shopping item
     * @return
     */
    public BigDecimal getTotalPrice() {
        BigDecimal total = new BigDecimal("0");
        if (!CollectionUtils.isEmpty(items)) {
            for (CartItemVO item : items) {
                if (item.getChecked()) {
                    total = total.add(item.getTotalPrice());
                }
            }
        }
        return total.subtract(totalReduce);
    }

}


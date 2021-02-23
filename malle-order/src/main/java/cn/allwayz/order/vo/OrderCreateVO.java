package cn.allwayz.order.vo;

import cn.allwayz.order.entity.OrderEntity;
import cn.allwayz.order.entity.OrderItemEntity;
import lombok.Data;

import java.util.List;

/**
 * @author allwayz
 */
@Data
public class OrderCreateVO {

    private OrderEntity order;

    private List<OrderItemEntity> items;
}

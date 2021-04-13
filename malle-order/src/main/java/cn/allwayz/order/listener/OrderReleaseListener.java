package cn.allwayz.order.listener;

import cn.allwayz.common.constant.OrderConstant;
import cn.allwayz.order.entity.OrderEntity;
import cn.allwayz.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author allwayz
 */
@Slf4j
@Component
@RabbitListener(queues = {OrderConstant.ORDER_RELEASE_ORDER_QUEUE})
public class OrderReleaseListener {

    @Resource
    OrderService orderService;

    @RabbitHandler
    public void releaseOrder(OrderEntity orderEntity, Message message, Channel channel) throws IOException {
        //System.out.println("Receive back order：" + orderEntity.getOrderSn());
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            orderService.closeOrder(orderEntity);
             // 消费成功，手动ack
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 消费失败，消息重新入队
            channel.basicReject(deliveryTag, true);
            //log.error("Message queue manual ACK failed：cn.allwayz.order.listener.OrderReleaseListener.releaseOrder, Error: {}", e.getMessage());
        }
    }
}
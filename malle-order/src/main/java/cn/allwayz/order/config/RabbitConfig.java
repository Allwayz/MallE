package cn.allwayz.order.config;

import cn.allwayz.common.constant.OrderConstant;
import cn.allwayz.common.constant.WareConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author allwayz
 *
 * Enable automatic RabbitMQ configuration and add custom configuration
 *
 * If you only need to create exchanges, queues, bindings, send messages, etc., just use AmqpAdmin.
 * If you want to use @RabbitListener to listen for messages (consumption), you must have @Enablerabbit enabled
 * registered its RabbitListenerAnnotationBeanPostProcessor this annotation is the water in the container
 *
 * Guarantee that messages are not lost:
 * 1, began to publisher confirmed mechanism (confirmCallback, returnCallback) and consumer to confirm (manual ack/nack)
 * 2. Record each message sent in the database, scan the database regularly and resend the failed message.
 */
@EnableRabbit
@Configuration
@Slf4j
public class RabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * If the message sent is an object, the serialization mechanism is used and processed by the MessageConverter converter,
     * default is WhiteListDeserializingMessageConverter, using JDK serialization, so these beans must implement the Serializable interface
     *
     * To use JSON serialization, we need to add a JSON formatted MessageConverter to the container and send a message marked with the full class name of the object
     *
     * listening to the queue method parameter, you can use the Object obj to receive the Message content, through the obj. GetClass () can see true type is org. Springframework. It.. The core Message
     * So you can also use Message Me directly to receive the Message. You can get the body of the Message through me.getBody()
     * If you know the intrinsic type of the message body, you can also use XXXEntity to receive it directly.
     * @return
     */
    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @PostConstruct
    public void setCallback() {
        /**
         * Messages are delivered by producers to Broker/Exchange callbacks
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("Message delivered to switch successfully：[correlationData={}]",correlationData);
            } else {
                log.error("Message delivered to switch failure：[correlationData={}，because：{}]", correlationData, cause);
            }
        });
        /**
         * The message is routed from Exchange to Queue failure callback
         */
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.error("Routing to queue failed,message content:{},switch:{},route piece:{},reply code:{},reply text:{}", message, exchange, routingKey, replyCode, replyText);
        });
    }

    @Bean
    public Exchange orderEventExchange() {
        // String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange(OrderConstant.ORDER_EVENT_EXCHANGE,
                true,
                false);
    }

    @Bean
    public Queue orderReleaseOrderQueue() {
        //String name, boolean durable, boolean exclusive, boolean autoDelete,
        // 			@Nullable Map<String, Object> arguments
        return new Queue(OrderConstant.ORDER_RELEASE_ORDER_QUEUE,
                true,
                false,
                false);
    }

    // Temarily unused, when the order is cancelled, to roll back the discount
    @Bean
    public Queue orderReleaseCouponQueue() {
        //String name, boolean durable, boolean exclusive, boolean autoDelete,
        // 			@Nullable Map<String, Object> arguments
        return new Queue(OrderConstant.ORDER_RELEASE_COUPON_QUEUE,
                true,
                false,
                false);
    }

    /**
     * Dead letter queue/delay queue
     * @return
     *
     * x-dead-letter-exchange="stock-event-exchange"
     * x-dead-letter-routing-key="stock.release"
     * x-message-ttl="60000"
     */
    @Bean
    public Queue orderDelayQueue() {
        //String name, boolean durable, boolean exclusive, boolean autoDelete,
        // 			@Nullable Map<String, Object> arguments
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", OrderConstant.DEAD_LETTER_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", OrderConstant.ORDER_DEAD_LETTER_ROUTING_KEY);
        arguments.put("x-message-ttl", OrderConstant.DEAD_LETTER_TTL);
        return new Queue(OrderConstant.ORDER_DELAY_ORDER_QUEUE,
                true,
                false,
                false,
                arguments);
    }

    /**
     * The binding relationship
     */
    @Bean
    public Binding orderCreateBinding() {
        // String destination, DestinationType destinationType, String exchange, String routingKey,
        // 			@Nullable Map<String, Object> arguments
        return new Binding(OrderConstant.ORDER_DELAY_ORDER_QUEUE,
                Binding.DestinationType.QUEUE,
                OrderConstant.ORDER_EVENT_EXCHANGE,
                OrderConstant.ORDER_CREATE_ROUTING_KEY,
                null);
    }

    @Bean
    public Binding orderReleaseOrderBind() {
        // String destination, DestinationType destinationType, String exchange, String routingKey,
        // 			@Nullable Map<String, Object> arguments
        return new Binding(OrderConstant.ORDER_RELEASE_ORDER_QUEUE,
                Binding.DestinationType.QUEUE,
                OrderConstant.ORDER_EVENT_EXCHANGE,
                OrderConstant.ORDER_RELEASE_ORDER_ROUTING_KEY,
                null);
    }

    /**
     * Bind to the unlock queue of the inventory
     * @return
     */
    @Bean
    public Binding orderReleaseStockBinding() {
        // String destination, DestinationType destinationType, String exchange, String routingKey,
        // 			@Nullable Map<String, Object> arguments
        return new Binding(WareConstant.STOCK_RELEASE_QUEUE,
                Binding.DestinationType.QUEUE,
                OrderConstant.ORDER_EVENT_EXCHANGE,
                OrderConstant.ORDER_RELEASE_STOCK_ROUTING_KEY,
                null);
    }

    /**
     * The binding relationship between seckill message queue and switch
     * @return
     */
    @Bean
    public Binding seckillOrderDealBinding() {
        // String destination, DestinationType destinationType, String exchange, String routingKey,
        // 			@Nullable Map<String, Object> arguments
        return new Binding(OrderConstant.ORDER_SECKILL_DEAL_QUEUE,
                Binding.DestinationType.QUEUE,
                OrderConstant.ORDER_EVENT_EXCHANGE,
                OrderConstant.ORDER_SECKILL_DEAL_QUEUE_ROUTING_KEY,
                null);
    }

}


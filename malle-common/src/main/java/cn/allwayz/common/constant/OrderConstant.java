package cn.allwayz.common.constant;

/**
 * @author allwayz
 */
public class OrderConstant {

    public static final String ORDER_TOKEN_PREFIX = "order:token:";

    // Message queues, switches, and queues
    public static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";
    public static final String ORDER_DELAY_ORDER_QUEUE = "order.delay.order.queue";
    public static final String ORDER_RELEASE_ORDER_QUEUE = "order.release.order.queue";
    public static final String ORDER_RELEASE_COUPON_QUEUE = "order.release.coupon.queue";
    public static final String ORDER_RELEASE_ORDER_ROUTING_KEY = "order.release.order.#";
    public static final String ORDER_RELEASE_COUPON_ROUTING_KEY = "order.release.coupon.#";
    public static final String ORDER_RELEASE_STOCK_ROUTING_KEY = "order.release.stock.#";
    public static final String ORDER_CREATE_ROUTING_KEY = "order.create.#";
    public static final String DEAD_LETTER_EXCHANGE = "order-event-exchange";
    public static final String ORDER_DEAD_LETTER_ROUTING_KEY = "order.release.order";
    public static final Integer DEAD_LETTER_TTL = 2 * 60 * 1000; // The unit is ms
    public static final String ORDER_SECKILL_DEAL_QUEUE = "order.seckill.deal.queue";
    public static final String ORDER_SECKILL_DEAL_QUEUE_ROUTING_KEY = "order.seckill.deal.#";

    public enum  OrderStatusEnum {
        CREATE_NEW(0,"Pending Payment"),
        PAYED(1,"Paied"),
        SENDED(2,"Delivered"),
        RECIEVED(3,"Completed"),
        CANCLED(4,"Cancelled"),
        SERVICING(5,"After-sale"),
        SERVICED(6,"After-sales completion");
        private Integer code;
        private String msg;

        OrderStatusEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum PayType {

        ALIPAY(1,"ALIPAY"),
        WECHAT(2,"WECHAT"),
        UNIONPAY(3,"UNIONPAY"),
        CASH_ON_DELIVERY(4,"CASH_ON_DELIVERY");

        private Integer code;
        private String msg;

        PayType(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

    }
}


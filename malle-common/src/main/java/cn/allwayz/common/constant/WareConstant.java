package cn.allwayz.common.constant;

public class WareConstant {

    // 消息队列，交换机和队列
    public static final String STOCK_EVENT_EXCHANGE = "stock-event-exchange";
    public static final String STOCK_DELAY_QUEUE = "stock.delay.queue";
    public static final String STOCK_RELEASE_QUEUE = "stock.release.queue";
    public static final String STOCK_RELEASE_ROUTING_KEY = "stock.release.#";
    public static final String STOCK_LOCKED_ROUTING_KEY = "stock.locked.#";
    public static final String DEAD_LETTER_EXCHANGE = "stock-event-exchange";
    public static final String DEAD_LETTER_ROUTING_KEY = "stock.release";
    public static final Integer DEAD_LETTER_TTL = 3 * 60 * 1000; // 单位是ms

    public enum  PurchaseStatusEnum{
        CREATED(0,"新建"),ASSIGNED(1,"已分配"),
        RECEIVE(2,"已领取"),FINISH(3,"已完成"),
        HASERROR(4,"有异常");
        private int code;
        private String msg;

        PurchaseStatusEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }


    public enum  PurchaseDetailStatusEnum{
        CREATED(0,"新建"),ASSIGNED(1,"已分配"),
        BUYING(2,"正在采购"),FINISH(3,"已完成"),
        HASERROR(4,"采购失败");
        private int code;
        private String msg;

        PurchaseDetailStatusEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    /**
     * 商品库存锁定状态
     */
    public enum StockLockStatus {
        LOCKED(1, "已锁定"),
        RELEASED(2, "已释放"),
        DEDUCTED(3,  "已扣减");

        private int value;
        private String desc;

        StockLockStatus(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public int getValue() {
            return value;
        }

        public String getDesc() { return desc; }
    }
}


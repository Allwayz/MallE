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
    public static final Integer DEAD_LETTER_TTL = 3 * 60 * 1000; //The unit is ms

    public enum  PurchaseStatusEnum{
        CREATED(0,"New"),ASSIGNED(1,"New"),
        RECEIVE(2,"Receive"),FINISH(3,"Completed"),
        HASERROR(4,"Error");
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
        CREATED(0,"New"),
        ASSIGNED(1,"allocated"),
        BUYING(2,"Buying"),
        FINISH(3,"Completed"),
        HASERROR(4,"Failure");
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
        LOCKED(1, "Locked"),
        RELEASED(2, "Released"),
        DEDUCTED(3,  "Deducted");

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


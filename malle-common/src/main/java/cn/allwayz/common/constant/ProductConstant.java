package cn.allwayz.common.constant;

import lombok.Getter;

/**
 * @author allwayz
 */
public class ProductConstant {
    public enum  AttrEnum{
        ATTR_TYPE_BASE(1,"BaseAttr"),ATTR_TYPE_SALE(0,"SaleAttr");
        private int code;
        private String msg;

        AttrEnum(int code,String msg){
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

    @Getter
    public enum StatusEnum {

        SPU_NEW(0, "New"),

        SPU_UP(1, "Release"),

        SPU_DOWN(2, "Undo");

        private int code;

        private String message;

        StatusEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}

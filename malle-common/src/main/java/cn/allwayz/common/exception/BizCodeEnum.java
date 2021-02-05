package cn.allwayz.common.exception;

import lombok.Getter;

/**
 * @author allwayz
 *
 * <h3>错误码和错误信息定义类</h3>
 * <ul>1. 错误码定义规则为5为数字</ul>
 * <ul>2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知异常</ul>
 * <ul>3. 维护错误码后需要维护错误描述，将他们定义为枚举形式</ul>
 * <h3>错误码列表：</h3>
 *  <ul>
 *  <li>10: 通用</li>
 *      <ul>
 *          <li>
 *          001：参数格式校验
 *          </li>
 *      </ul>
 *  <li>11: 商品</li>
 *  <li>12: 订单</li>
 *  <li>13: 购物车</li>
 *  <li>14: 物流</li>
 *  </ul>
 */
@Getter
public enum BizCodeEnum {
    /**
     *
     */
    UNKNOW_EXCEPTION(10000,"Unknown system exception"),
    VAILD_EXCEPTION(10001,"Parameter format verification failed"),
    SMS_CODE_EXCEPTION(10002,"短信频率太快"),

    TOO_MANY_REQUESTS(10003,"请求频率过高"),

    PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),

    USERNAME_EXIST_EXCEPTION(15001, "用户名已存在"),

    PHONE_EXIST_EXCEPTION(15002, "手机号已存在"),

    LOGIN_EXCEPTION(15003, "用户名或密码错误"),

    NO_STOCK_EXCEPTION(22000, "商品库存不足");

    private int code;
    private String msg;

    BizCodeEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
}


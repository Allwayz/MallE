package cn.allwayz.common.exception;

import lombok.Getter;

/**
 * @author allwayz
 *
 * <h3> error code and error message define class </h3>
 * <ul>1. The error code definition rule is 5 for the number </ul>
 * <ul>2. The first two represent the business scenario and the last three represent the error code. For example: 100001. 10: Universal 001: System unknown exception </ul>
 * <ul>3. The error descriptions need to be maintained after the error codes are maintained. Define them to the enumeration form </ul>
 * <h3> error code list: </h3>
 *  <ul>
 * <li>10: generic </li>
 *      <ul>
 *          <li>
 * 001: Parameter format check
 *          </li>
 *      </ul>
 * <li>11: goods </li>
 * <li>12: order </li>
 * <li>13: shopping cart </li>
 * <li>14: logistics </li>
 *  </ul>
 */
@Getter
public enum BizCodeEnum implements CommonError{

    //System Exception
    THREAD_POOL_TASK_FAILED(00001, "The thread pool failed to execute the task"),
    TOO_MANY_REQUEST(00002, "Request too frequent, please try again later"),
    UNKNOW_EXCEPTION(00003,"Unknown system exception"),
    VAILD_EXCEPTION(00004,"Parameter format verification failed"),
    PRODUCT_PARAM_INVAILD(00005,"Item parameter format validation failed"),
    SMS_CODE_EXCEPTION(00006,"Texting too fast"),
    TOO_MANY_REQUESTS(00007,"Request frequency is too high"),

    //Product Execption
    PRODUCT_UP_EXCEPTION(10001, "Product release error"),
    WARE_SKU_STOCK_NOT_ENOUGH(10002, "Short stock of goods"),

    //User Execption
    USERNAME_EXIST_EXCEPTION(20001, "The user name already exists"),
    PHONE_EXIST_EXCEPTION(20002, "The phone number already exists"),
    MEMBER_ALREADY_EXIST(20003, "User already exists"),
    MEMBER_NOT_EXIST(20004, "Account does not exist"),
    MEMBER_ACCOUNT_PASSWORD_NOT_MATCH(20005, "Incorrect user name or password"),
    INVALIDE_PASSWORD(20006,"Wrong Password"),
    AUTH_WEIBO_LOGIN_FAILED(20007, "Third party login failed, please try again"),
    AUTH_USER_NOT_LOGIN(20008, "User not logged in"),

    //Order Execption

    ORDER_HAS_EXPIRED(30001, "The order has been processed, please place a new order"),
    ORDER_CREATE_FAILED(30002, "Order creation failed"),
    ORDER_PAY_FEILED(30003, "Order payment failed"),


    //Warehouse Execption
    WARE_PURCHASE_MERGE_FAILED(40001, "Unable to merge purchase items into a received purchase order"),
    WARE_PURCHASE_ASSIGN_FAILED(40002, "Purchasers can only be assigned to newly created purchase orders"),

    //Remote Call Execption
    CALL_FEIGN_SERVICE_FAILED(50001, "The call to the remote service failed");


    private int code;
    private String msg;

    BizCodeEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
    @Override
    public int getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }
}


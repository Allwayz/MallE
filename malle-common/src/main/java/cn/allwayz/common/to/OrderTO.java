package cn.allwayz.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderTO {

    private Long id;
    /**
     * member_id
     */
    private Long memberId;
    /**
     * orderSn
     */
    private String orderSn;
    /**
     * couponId
     */
    private Long couponId;
    /**
     * create_time
     */
    private Date createTime;
    /**
     * memberUsername
     */
    private String memberUsername;
    /**
     * totalAmount
     */
    private BigDecimal totalAmount;
    /**
     * payAmount
     */
    private BigDecimal payAmount;
    /**
     * freightAmount
     */
    private BigDecimal freightAmount;
    /**
     * promotionAmount
     */
    private BigDecimal promotionAmount;
    /**
     * integrationAmount
     */
    private BigDecimal integrationAmount;
    /**
     * couponAmount
     */
    private BigDecimal couponAmount;
    /**
     * discountAmount
     */
    private BigDecimal discountAmount;
    /**
     * payType
     */
    private Integer payType;
    /**
     * sourceType
     */
    private Integer sourceType;
    /**
     * status
     */
    private Integer status;
    /**
     * deliveryCompany
     */
    private String deliveryCompany;
    /**
     * deliverySn
     */
    private String deliverySn;
    /**
     * autoConfirmDay
     */
    private Integer autoConfirmDay;
    /**
     * integration
     */
    private Integer integration;
    /**
     * growth
     */
    private Integer growth;
    /**
     * billType
     */
    private Integer billType;
    /**
     * billHeader
     */
    private String billHeader;
    /**
     * billContent
     */
    private String billContent;
    /**
     * billReceiverPhone
     */
    private String billReceiverPhone;
    /**
     * billReceiverEmail
     */
    private String billReceiverEmail;
    /**
     * receiverName
     */
    private String receiverName;
    /**
     * receiverPhone
     */
    private String receiverPhone;
    /**
     * receiverPostCode
     */
    private String receiverPostCode;
    /**
     * receiverProvince
     */
    private String receiverProvince;
    /**
     * receiverCity
     */
    private String receiverCity;
    /**
     * receiverRegion
     */
    private String receiverRegion;
    /**
     * receiverDetailAddress
     */
    private String receiverDetailAddress;
    /**
     * note
     */
    private String note;
    /**
     * confirmStatus
     */
    private Integer confirmStatus;
    /**
     * deleteStatus
     */
    private Integer deleteStatus;
    /**
     * useIntegration
     */
    private Integer useIntegration;
    /**
     * paymentTime
     */
    private Date paymentTime;
    /**
     * deliveryTime
     */
    private Date deliveryTime;
    /**
     * receiveTime
     */
    private Date receiveTime;
    /**
     * commentTime
     */
    private Date commentTime;
    /**
     * modifyTime
     */
    private Date modifyTime;
}

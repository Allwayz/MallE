package cn.allwayz.common.to;

import lombok.Data;

@Data
public class MemberAddressTO {

    /**
     * id
     */
    private Long id;
    /**
     * member_id
     */
    private Long memberId;
    /**
     * Name of consignee
     */
    private String name;
    /**
     * phone
     */
    private String phone;
    /**
     * postCode
     */
    private String postCode;
    /**
     * province
     */
    private String province;
    /**
     * city
     */
    private String city;
    /**
     * region
     */
    private String region;
    /**
     * detailAddress
     */
    private String detailAddress;
    /**
     * areacode
     */
    private String areacode;
    /**
     * defaultStatus
     */
    private Integer defaultStatus;
}


package cn.allwayz.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author allwayz
 */
@Data
public class MemberInfoVO implements Serializable {

    /**
     * id
     */
    private Long id;
    /**
     * level
     */
    private String level;
    /**
     * username
     */
    private String username;

    /**
     * nickname
     */
    private String nickname;
    /**
     * mobile
     */
    private String mobile;
    /**
     * email
     */
    private String email;
    /**
     * header
     */
    private String header;
    /**
     * gender
     */
    private Integer gender;
    /**
     * birth
     */
    private Date birth;
    /**
     * city
     */
    private String city;
    /**
     * job
     */
    private String job;
    /**
     * sign
     */
    private String sign;
    /**
     * sourceType
     */
    private Integer sourceType;
    /**
     * integration
     */
    private Integer integration;
    /**
     * growth
     */
    private Integer growth;
    /**
     * status
     */
    private Integer status;
    /**
     * createTime
     */
    private Date createTime;
}

package cn.allwayz.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author allwayz
 */
@Data
public class MemberPrice {

    private Long id;
    private String name;
    private BigDecimal price;

}
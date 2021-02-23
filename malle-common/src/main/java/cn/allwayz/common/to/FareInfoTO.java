package cn.allwayz.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareInfoTO {

    private MemberAddressTO address;

    private BigDecimal fare;
}

package cn.allwayz.order.service.impl;

import cn.allwayz.common.exception.BizCodeEnum;
import cn.allwayz.common.exception.BizException;
import cn.allwayz.common.to.FareInfoTO;
import cn.allwayz.common.to.MemberAddressTO;
import cn.allwayz.common.utils.R;
import cn.allwayz.order.feign.MemberFeignService;
import cn.allwayz.order.service.FareService;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author allwayz
 */
@Slf4j
@Service
public class FareServiceImpl implements FareService {

    @Resource
    MemberFeignService memberFeignService;

    @Override
    public FareInfoTO getFare(Long addressId) {
        R r = memberFeignService.getAddress(addressId);
        if (r.getCode() != 0) {
            log.error("Failed to call the malle-member service to query address information");
            throw new BizException(BizCodeEnum.CALL_FEIGN_SERVICE_FAILED);
        }
        MemberAddressTO address = r.getData("memberReceiveAddress", new TypeReference<MemberAddressTO>(){});
        FareInfoTO fareInfoTO = new FareInfoTO();
        fareInfoTO.setAddress(address);
        fareInfoTO.setFare(getSimpleFare(address));
        return fareInfoTO;
    }

    @Override
    public FareInfoTO getMemberDefaultAddressFare(Long memberId) {
        R r = memberFeignService.getMemberDefaultAddress(memberId);
        if (r.getCode() != 0) {
            log.error("Invoke the malle-member service to query the user's default address");
            throw new BizException(BizCodeEnum.CALL_FEIGN_SERVICE_FAILED);
        }
        MemberAddressTO address = r.getData(MemberAddressTO.class);
        FareInfoTO fareInfoTO = new FareInfoTO();
        fareInfoTO.setAddress(address);
        fareInfoTO.setFare(getSimpleFare(address));
        return fareInfoTO;
    }

    /**
     * Simple freight calculation
     * @param address
     * @return
     */
    private BigDecimal getSimpleFare(MemberAddressTO address) {
        // Simplified Freight Calculation
        if (address != null) {
            String phone = address.getPhone();
            if (!StringUtils.isEmpty(phone)) {
                // Simplify the process of calculating freight charges
                return new BigDecimal(phone.substring(phone.length() - 1));
            }
        }
        return new BigDecimal("0");
    }
}


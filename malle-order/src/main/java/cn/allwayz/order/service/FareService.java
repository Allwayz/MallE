package cn.allwayz.order.service;

import cn.allwayz.common.to.FareInfoTO;

public interface FareService {
    FareInfoTO getFare(Long addressId);

    /**
     * 获取指定用户默认地址以及运费信息
     * @param memberId
     * @return
     */
    FareInfoTO getMemberDefaultAddressFare(Long memberId);
}

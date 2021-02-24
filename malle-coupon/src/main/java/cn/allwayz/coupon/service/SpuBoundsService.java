package cn.allwayz.coupon.service;

import cn.allwayz.common.to.SpuBoundsTO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 21:14:42
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);

    SpuBoundsTO getBySpuId(Long spuId);

    boolean saveSpuBoundS(SpuBoundsEntity spuBounds);
}


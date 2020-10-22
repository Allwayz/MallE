package cn.allwayz.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.order.entity.RefundInfoEntity;

import java.util.Map;

/**
 * 退款信息
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 20:03:11
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


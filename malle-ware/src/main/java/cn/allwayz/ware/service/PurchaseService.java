package cn.allwayz.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.ware.entity.PurchaseEntity;

import java.util.Map;

/**
 * 采购信息
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 20:13:03
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


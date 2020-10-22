package cn.allwayz.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.product.entity.SpuCommentEntity;

import java.util.Map;

/**
 * 商品评价
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 19:24:14
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


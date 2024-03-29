package cn.allwayz.product.service;

import cn.allwayz.common.to.BrandTO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 19:24:13
 */
public interface BrandService extends IService<BrandEntity> {

    /**
     * Paging query
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * Update Detail
     * @param brand
     */
    void updateDetail(BrandEntity brand);

    List<BrandTO> getBatch(List<Long> ids);
}


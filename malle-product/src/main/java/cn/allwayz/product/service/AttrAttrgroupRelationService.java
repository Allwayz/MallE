package cn.allwayz.product.service;

import cn.allwayz.product.vo.AttrGroupRelationVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.product.entity.AttrAttrgroupRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 19:24:14
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    /**
     * ???
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * ???
     * @param vos
     */
    void saveBatch(List<AttrGroupRelationVo> vos);
}


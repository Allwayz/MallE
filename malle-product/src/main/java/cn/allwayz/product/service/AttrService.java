package cn.allwayz.product.service;

import cn.allwayz.product.vo.AttrGroupRelationVo;
import cn.allwayz.product.vo.AttrRespVo;
import cn.allwayz.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 19:24:14
 */
public interface AttrService extends IService<AttrEntity> {

    /**
     * ?
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * ?
     * @param attr
     */
    void saveAttr(AttrVo attr);

    /**
     * ?
     * @param params
     * @param catelogId
     * @param type
     * @return
     */
    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    /**
     * ?
     * @param attrId
     * @return
     */
    AttrRespVo getAttrInfo(Long attrId);

    /**
     * ?
     * @param attr
     */
    void updateAttr(AttrVo attr);

    /**
     * ?
     * @param attrgroupId
     * @return
     */
    List<AttrEntity> getRelationAttr(Long attrgroupId);

    /**
     * ?
     * @param vos
     */
    void deleteRelation(AttrGroupRelationVo[] vos);

    /**
     * ?
     * @param params
     * @param attrgroupId
     * @return
     */
    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);
}


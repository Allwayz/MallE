package cn.allwayz.product.service;

import cn.allwayz.common.to.SpuInfoTO;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.product.entity.SpuInfoEntity;
import cn.allwayz.product.vo.SpuSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * spu信息
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 19:24:14
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {
    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity infoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void up(Long spuId);

    SpuInfoTO getBySkuId(Long skuId);
}


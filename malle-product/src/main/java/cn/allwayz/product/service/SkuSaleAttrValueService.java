package cn.allwayz.product.service;

import cn.allwayz.product.vo.ItemSaleAttrVO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.product.entity.SkuSaleAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 19:24:14
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuSaleAttrValueEntity> listBySkuId(Long skuId);

    /**
     * 指定spu的所有销售属性及其值(值为多个值，逗号分隔，总体为一个string)
     * @param spuId
     * @return
     */
    List<ItemSaleAttrVO> allAttrValueWithSkuBySpuId(Long spuId);


    /**
     * 指定sku的所有销售属性及其值(属性名:值)
     * @param skuId
     * @return
     */
    List<String> stringListBySkuId(Long skuId);
}


package cn.allwayz.product.dao;

import cn.allwayz.product.entity.SkuSaleAttrValueEntity;
import cn.allwayz.product.vo.ItemSaleAttrVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 19:24:14
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<ItemSaleAttrVO> allAttrValueWithSkuBySpuId(@Param("spuId") Long spuId);

    List<String> stringListBySkuId(@Param("skuId") Long skuId);
}

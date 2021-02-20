package cn.allwayz.product.dao;

import cn.allwayz.product.entity.ProductAttrValueEntity;
import cn.allwayz.product.vo.ItemAttrGroupWithAttrVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu属性值
 * 
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 19:24:14
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {
    List<ItemAttrGroupWithAttrVO> getAttrsWithAttrGroup(@Param("spuId") Long spuId, @Param("catelogId") Long catelogId);
}

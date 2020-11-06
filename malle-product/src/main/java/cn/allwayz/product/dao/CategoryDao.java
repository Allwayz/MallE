package cn.allwayz.product.dao;

import cn.allwayz.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 19:24:13
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}

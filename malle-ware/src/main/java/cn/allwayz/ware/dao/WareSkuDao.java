package cn.allwayz.ware.dao;

import cn.allwayz.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 20:13:03
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}

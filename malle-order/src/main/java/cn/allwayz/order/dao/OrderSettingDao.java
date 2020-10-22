package cn.allwayz.order.dao;

import cn.allwayz.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 20:03:11
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}

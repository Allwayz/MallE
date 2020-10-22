package cn.allwayz.order.dao;

import cn.allwayz.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 20:03:11
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}

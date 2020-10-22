package cn.allwayz.ware.dao;

import cn.allwayz.ware.entity.WareInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息
 * 
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 20:13:03
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {
	
}

package cn.allwayz.product.dao;

import cn.allwayz.common.to.BrandTO;
import cn.allwayz.product.entity.BrandEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 品牌
 * 
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 19:24:13
 */
@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {

    List<BrandTO> getBatch(@Param("ids") List<Long> ids);
}

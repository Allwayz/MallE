package cn.allwayz.ware.dao;

import cn.allwayz.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品库存
 * 
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 20:13:03
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    Long getSkuStock(@Param("skuId") Long skuId);

    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId);

    @Update("UPDATE wms_ware_sku SET stock_locked = stock_locked + #{count} WHERE sku_id = #{skuId} AND ware_id = #{wareId} AND stock - stock_locked >= #{count}")
    int lockSkuStock(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("count") Integer count);

    //UPDATE wms_ware_sku SET stock = stock - stock_locked, stock_locked = stock_locked - 3 WHERE sku_id = 71 AND ware_id = 1
    @Update("UPDATE wms_ware_sku SET stock = stock - stock_locked, stock_locked = stock_locked -  #{count} WHERE sku_id = #{skuId} AND ware_id = #{wareId}")
    boolean unlockStock(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("count") Integer count);

    // 查询此商品在哪些仓库有库存
    @Select("SELECT ware_id FROM wms_ware_sku WHERE sku_id = #{skuId} AND stock - stock_locked > 0")
    List<Long> listWaresBySkuId(@Param("skuId") Long skuId);
}

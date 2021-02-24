package cn.allwayz.ware.service;

import cn.allwayz.common.to.OrderLockStockTO;
import cn.allwayz.common.to.OrderTO;
import cn.allwayz.common.to.SkuHasStockVO;
import cn.allwayz.common.to.SkuStockTO;
import cn.allwayz.common.to.mp.StockLockTO;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 20:13:03
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVO> getSkuHasStock(List<Long> skuIds);

    List<SkuStockTO> getSkusStock(List<Long> skuIds);

    /**
     * 查出某个sku的库存信息
     * @param skuId
     * @return
     */
    Long getSkuStock(Long skuId);

    /**
     * 为订单锁定库存
     * @param lockStockTO
     * @return
     */
    boolean lockOrderStock(OrderLockStockTO lockStockTO);

    /**
     * 收到库存锁定过期消息，释放库存
     * @param stockLockTO
     * @return
     */
    void unlockStock(StockLockTO stockLockTO);

    /**
     * 收到订单关闭消息，释放库存
     */
    void unlockStock(OrderTO orderTO);

}


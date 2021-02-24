package cn.allwayz.ware.service.impl;

import cn.allwayz.common.constant.OrderConstant;
import cn.allwayz.common.constant.WareConstant;
import cn.allwayz.common.exception.BizCodeEnum;
import cn.allwayz.common.exception.BizException;
import cn.allwayz.common.to.OrderLockStockTO;
import cn.allwayz.common.to.OrderTO;
import cn.allwayz.common.to.SkuHasStockVO;
import cn.allwayz.common.to.SkuStockTO;
import cn.allwayz.common.to.mp.StockLockTO;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.Query;
import cn.allwayz.common.utils.R;
import cn.allwayz.ware.dao.WareSkuDao;
import cn.allwayz.ware.entity.WareOrderTaskDetailEntity;
import cn.allwayz.ware.entity.WareOrderTaskEntity;
import cn.allwayz.ware.entity.WareSkuEntity;
import cn.allwayz.ware.feign.OrderFeignService;
import cn.allwayz.ware.feign.ProductFeignService;
import cn.allwayz.ware.service.WareOrderTaskDetailService;
import cn.allwayz.ware.service.WareOrderTaskService;
import cn.allwayz.ware.service.WareSkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    WareOrderTaskService taskService;

    @Autowired
    WareOrderTaskDetailService taskDetailService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderFeignService orderFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId: 1
         * wareId: 2
         */
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            queryWrapper.eq("sku_id",skuId);
        }

        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }


        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1、判断如果还没有这个库存记录新增
        List<WareSkuEntity> entities = this.baseMapper.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(entities == null || entities.size() == 0){
            WareSkuEntity skuEntity = new WareSkuEntity();
            skuEntity.setSkuId(skuId);
            skuEntity.setStock(skuNum);
            skuEntity.setWareId(wareId);
            skuEntity.setStockLocked(0);
            //TODO 远程查询sku的名字，如果失败，整个事务无需回滚
            //1、自己catch异常
            //TODO 还可以用什么办法让异常出现以后不回滚？高级
            try {
                R info = productFeignService.info(skuId);
                Map<String,Object> data = (Map<String, Object>) info.get("skuInfo");

                if(info.getCode() == 0){
                    skuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){

            }


            this.baseMapper.insert(skuEntity);
        }else{
            this.baseMapper.addStock(skuId,wareId,skuNum);
        }
    }

    @Override
    public List<SkuHasStockVO> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVO> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVO vo = new SkuHasStockVO();
            // 查询当前 sku 的库存
            Long count = baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count != null && count > 0);
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<SkuStockTO> getSkusStock(List<Long> skuIds) {
        List<SkuStockTO> collect = skuIds.stream().map(skuId -> {
            SkuStockTO skuStockTO = new SkuStockTO();
            Long stock = baseMapper.getSkuStock(skuId);
            skuStockTO.setSkuId(skuId);
            skuStockTO.setStock(stock == null ? 0 : stock);
            return skuStockTO;
        }).collect(Collectors.toList());

        return collect;
    }

    @Override
    public Long getSkuStock(Long skuId) {
        return baseMapper.getSkuStock(skuId);
    }

    @Transactional
    @Override
    public boolean lockOrderStock(OrderLockStockTO lockStockTO) {
        // 通常情况下，根据订单收货地址选择最近仓库锁定库存
        // 这里简化为，遍历存有此商品的所有库存，挨个尝试锁定，有一个仓库锁定成功就算成功
        // 1. 创建库存工作单
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(lockStockTO.getOrderSn());
        taskService.save(taskEntity);
        // 2. 尝试锁定所有订单项库存
        List<OrderLockStockTO.SkuLockStock> locks = lockStockTO.getLocks();
        for (OrderLockStockTO.SkuLockStock skuLockStock : locks) {
            Long skuId = skuLockStock.getSkuId();
            // 先查询此商品在哪些仓库有货
            List<Long> wareIds = this.baseMapper.listWaresBySkuId(skuId);
            if (CollectionUtils.isEmpty(wareIds)) {
                // 当前商品库存不足，锁定失败
                throw new BizException(BizCodeEnum.WARE_SKU_STOCK_NOT_ENOUGH, "库存不足，商品：" + skuLockStock);
            }
            // 挨个仓库尝试锁定
            boolean currSkuLockRes = false;
            for (Long wareId : wareIds) {
                long rows = this.baseMapper.lockSkuStock(wareId, skuId, skuLockStock.getCount());
                if (rows == 1) {
                    // 当前商品库存锁定成功
                    // 创键任务详情
                    WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity(
                            null, skuId, skuLockStock.getSkuName(),
                            skuLockStock.getCount(), taskEntity.getId(),
                            wareId, WareConstant.StockLockStatus.LOCKED.getValue());

                    taskDetailService.save(taskDetailEntity);
                    // 向mq发送当前锁定消息
                    StockLockTO stockLockTO = new StockLockTO(taskDetailEntity.getId(), lockStockTO.getOrderSn(), skuId, wareId, skuLockStock.getCount());
                    rabbitTemplate.convertAndSend(WareConstant.STOCK_EVENT_EXCHANGE, WareConstant.STOCK_LOCKED_ROUTING_KEY, stockLockTO);
                    currSkuLockRes = true;
                    break;
                }
            }
            // 全部仓库都锁定失败
            if (currSkuLockRes == false) {
                throw new BizException(BizCodeEnum.WARE_SKU_STOCK_NOT_ENOUGH, "库存不足，商品：" + skuLockStock);
            }
        }
        return true;
    }

    /**
     * 收到库存锁定过期消息，释放库存
     * @param stockLockTO
     * @return
     */
    @Transactional
    @Override
    public void unlockStock(StockLockTO stockLockTO) {
        Long taskDetailId = stockLockTO.getTaskDetailId();
        WareOrderTaskDetailEntity taskDetail = taskDetailService.getById(taskDetailId);
        // 库存扣减时的任务单已不存在，说明扣减时失败已被回滚，无需处理
        // 库存单虽存在，但是状态已经是已解锁状态或已扣除状态(非锁定状态)，也无需处理
        if (taskDetail == null || taskDetail.getLockStatus() != WareConstant.StockLockStatus.LOCKED.getValue()) {
            return;
        }
        // 库存扣减时的任务单仍然存在，且仍然是已锁定状态
        // 远程调用，判断当时这个订单目前的状态
        R r = orderFeignService.getOrderDetail(stockLockTO.getOrderSn());
        if (r.getCode() != 0) {
            log.error("gulimall-ware调用gulimall-order查询订单失败");
            // 消费失败，重新入队
            throw new BizException(BizCodeEnum.CALL_FEIGN_SERVICE_FAILED);
        } else {
            OrderTO orderTO = r.getData(OrderTO.class);
            // 订单已不存在或已取消 就需要解锁库存
            if (orderTO == null || orderTO.getStatus() == OrderConstant.OrderStatusEnum.CANCLED.getCode()) {
                // 回滚，解锁库存
                System.out.println("<<<<<<<<<<<<<<<解锁库存<<<<<<<<<<<<<<");
                unlockStock(taskDetailId, stockLockTO.getWareId(), stockLockTO.getSkuId(), stockLockTO.getCount());
            }
            // 消费成功，手动ack
        }
    }

    /**
     * 收到订单关闭消息，释放库存
     */
    @Transactional
    @Override
    public void unlockStock(OrderTO orderTO) {
        // 1.获取库存工作单
        WareOrderTaskEntity taskEntity = taskService.getTaskByOrderSn(orderTO.getOrderSn());
        if (taskEntity != null) {
            // 2.获取此条工作单上的全部工作项
            List<WareOrderTaskDetailEntity> detailEntities = taskDetailService.listByTtaskId(taskEntity.getId());
            if (!CollectionUtils.isEmpty(detailEntities)) {
                // 3.找出其中状态为锁定状态的工作项，执行解锁库存方法
                detailEntities.stream()
                        .filter(detailEntity -> detailEntity.getLockStatus() == WareConstant.StockLockStatus.LOCKED.getValue())
                        .forEach(detailEntity -> unlockStock(detailEntity.getId(), detailEntity.getWareId(), detailEntity.getSkuId(), detailEntity.getSkuNum()));
            }
        }
    }

    /**
     * 解锁库存，并修改对应库存工作单状态
     * @param wareId
     * @param skuId
     * @param count
     * @return
     */
    private boolean unlockStock(Long taskDetailId, Long wareId, Long skuId, Integer count) {
        // 解锁库存
        this.baseMapper.unlockStock(wareId, skuId, count);
        // 修改库存工作单状态为已释放
        WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity();
        detailEntity.setId(taskDetailId);
        detailEntity.setLockStatus(WareConstant.StockLockStatus.RELEASED.getValue());
        return taskDetailService.updateById(detailEntity);
    }

    private boolean isValidId(String key) {
        return !StringUtils.isEmpty(key) && !"0".equalsIgnoreCase(key);
    }
}
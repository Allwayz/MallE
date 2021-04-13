package cn.allwayz.order.service;

import cn.allwayz.common.to.OrderTO;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.vo.UserCheckVO;
import cn.allwayz.order.entity.OrderEntity;
import cn.allwayz.order.vo.AlipayNotifyVO;
import cn.allwayz.order.vo.OrderConfirmVO;
import cn.allwayz.order.vo.OrderCreateVO;
import cn.allwayz.order.vo.OrderSubmitVO;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 订单
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 20:03:11
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * Returns the data required for the order confirmation page
     * @return
     */
    OrderConfirmVO confirmOrder();

    /**
     * 创建订单
     * @param submitVO
     * @return
     */
    OrderCreateVO submit(OrderSubmitVO submitVO);

    /**
     * Get the order and the order item
     * @param orderSn
     * @return
     */
    OrderCreateVO getOrderDetail(String orderSn);

    OrderTO getOrderTOByOrderSn(String orderSn);

    OrderEntity getOrderByOrderSn(String orderSn);

    /**
     * Order not paid over time, cancel order
     * @param orderEntity
     */
    void closeOrder(OrderEntity orderEntity);


    /**
     * Payment order
     * @param orderSn
     * @return
     */
    String payOrder(String orderSn);


    /**
     * Paging the order list of the current logged-in user
     * @param params
     * @return
     */
    PageUtils getCurrentUserOrderList(Map<String, Object> params);

    /**
     * Handle Ali payment asynchronous notification messages
     * @param notifyVO
     */
    String handleAlipayNotify(AlipayNotifyVO notifyVO, HttpServletRequest request);

    String updateOrder(UserCheckVO userCheckVO);

}


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
     * 返回订单确认页需要的数据
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
     * 获取订单及订单项
     * @param orderSn
     * @return
     */
    OrderCreateVO getOrderDetail(String orderSn);

    OrderTO getOrderTOByOrderSn(String orderSn);

    OrderEntity getOrderByOrderSn(String orderSn);

    /**
     * 订单超时未支付，取消订单
     * @param orderEntity
     */
    void closeOrder(OrderEntity orderEntity);


    /**
     * 支付订单
     * @param orderSn
     * @return
     */
    String payOrder(String orderSn);


    /**
     * 分页查询当前登录用户的订单列表
     * @param params
     * @return
     */
    PageUtils getCurrentUserOrderList(Map<String, Object> params);

    /**
     * 处理阿里支付异步通知消息
     * @param notifyVO
     */
    String handleAlipayNotify(AlipayNotifyVO notifyVO, HttpServletRequest request);

    String updateOrder(UserCheckVO userCheckVO);

}


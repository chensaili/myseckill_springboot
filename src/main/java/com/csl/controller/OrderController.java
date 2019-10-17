package com.csl.controller;

import com.csl.error.BusinessException;
import com.csl.error.EmBusinessError;
import com.csl.response.CommonReturnType;
import com.csl.service.OrderService;
import com.csl.service.model.OrderModel;
import com.csl.service.model.UserModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(value = "订单模块",tags = "订单模块")
@RestController
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class OrderController extends BaseController {
    private static final Logger logger= LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @ApiOperation(value = "创建订单")
    @RequestMapping(value = "/createorder",method = RequestMethod.POST,consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId")Integer itemId,
                                        @RequestParam(name = "amount")Integer amount,
                                        @RequestParam(name = "promoId",required = false)Integer promoId) throws BusinessException {
        //判断用户是否登录
        Boolean isLogin=(Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        //System.out.println(isLogin);
        //注意，下面的判断应该是isLogin==null，不能为!isLogin，当用户未登录时，isLogin是null，不是false
        if(isLogin==null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
        //如果用户已经登录，就取出用户的信息
        UserModel userModel=(UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        logger.info("下单用户id  "+userModel.getId());
        logger.info("下单的商品itemid  "+itemId);
        logger.info("是否为秒杀商品"+promoId);
        OrderModel orderModel=orderService.createOrder(userModel.getId(),itemId,promoId,amount);
        System.out.println(orderModel.getId());

        return CommonReturnType.create(orderModel);
    }
}

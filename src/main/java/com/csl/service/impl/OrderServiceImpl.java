package com.csl.service.impl;

import com.csl.controller.OrderController;
import com.csl.dao.ItemDOMapper;
import com.csl.dao.ItemStockDOMapper;
import com.csl.dao.OrderDOMapper;
import com.csl.dao.SequenceDOMapper;
import com.csl.dataobject.OrderDO;
import com.csl.dataobject.SequenceDO;
import com.csl.error.BusinessException;
import com.csl.error.EmBusinessError;
import com.csl.service.OrderService;
import com.csl.service.model.ItemModel;
import com.csl.service.model.OrderModel;
import com.csl.service.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class OrderServiceImpl  implements OrderService {
    private static final Logger logger= LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private OrderDOMapper orderDOMapper;
    @Autowired
    private SequenceDOMapper sequenceDOMapper;
    @Autowired
    private ItemServiceImpl itemService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private JedisPool jedisPool;
    //创建订单
    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId,Integer promoId, Integer amount) throws BusinessException {

        if(promoId==null){
            logger.info("进入普通下单部分");
            return createCommonOrder(userId,itemId,amount);
        }else {
            logger.info("进入秒杀下单部分");
            return createPromoOrder(userId,itemId,promoId,amount);
        }
    }


    public OrderModel createCommonOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException {
        ItemModel itemModel=itemService.getItemById(itemId);
        logger.info("根据itemid获取商品");
        if(itemModel==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品不存在");
        }
        //（2)检查用户是否存在
        UserModel userModel=userService.getUserById(userId);
        logger.info("根据id获取用户");
        if(userModel==null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST,"用户不存在");
        }
        //(3)购买数量
        if(amount<0||amount>99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"购买数量不合法");
        }

        //2.落单减库存
        boolean result=itemService.decreaseStock(itemId,amount);
        logger.info("落单减库存 "+result);
        if(!result){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
        //3.订单入库
        OrderModel orderModel=new OrderModel();
        logger.info("userId  "+userId);
        orderModel.setUserId(userId);
        logger.info("itemId  "+itemId);
        orderModel.setItemId(itemId);
        logger.info("amount  "+amount);
        orderModel.setAmount(amount);
        orderModel.setPromoId(0);
        logger.info("itemModel.getPrice() "+itemModel.getPrice());
        orderModel.setItemPrice(itemModel.getPrice());
        logger.info("orderModel.getItemPrice().multiply(new BigDecimal(amount))  "+orderModel.getItemPrice().multiply(new BigDecimal(amount)));
        orderModel.setOrderPirce(orderModel.getItemPrice().multiply(new BigDecimal(amount)));
        //需要将orderModel转换为OrderDO
        OrderDO orderDO=convertFromModel(orderModel);
        logger.info("orderModel转换为OrderDO");
        //生成订单号
        orderDO.setId(generateOrderNo());
        logger.info("订单号生成");
        //操作数据库
        orderDOMapper.insertSelective(orderDO);
        logger.info("操作数据库，往order表插入数据");
        //加上商品的销量
        itemService.increaseSales(itemId,amount);
        logger.info("增加商品销量");

        //下面三行是为了模拟秒杀时，减redis缓存的
        Jedis jedis=jedisPool.getResource();
        long res=jedis.decrBy("1",1);//res减1操作后的值
        logger.info(" res   "+res);

        return orderModel;
    }

    public OrderModel createPromoOrder(Integer userId, Integer itemId,Integer promoId, Integer amount) throws BusinessException {
        ItemModel itemModel=itemService.getItemById(itemId);
        if(itemModel==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品不存在");
        }
        //（2)检查用户是否存在
        UserModel userModel=userService.getUserById(userId);
        if(userModel==null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST,"用户不存在");
        }
        //(3)购买数量
        if(amount<0||amount>99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"购买数量不合法");
        }

        //2.落单减库存
        Jedis jedis=jedisPool.getResource();
        System.out.println("秒杀商品的id为"+promoId.toString());
        long result=jedis.decrBy(promoId.toString(),1);

       /* if(!result){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }*/
        //3.订单入库
        OrderModel orderModel=new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        orderModel.setPromoId(promoId);
        orderModel.setOrderPirce(orderModel.getItemPrice().multiply(new BigDecimal(amount)));
        //需要将orderModel转换为OrderDO
        OrderDO orderDO=convertFromModel(orderModel);
        //生成订单号
        orderDO.setId(generateOrderNo());
        //操作数据库
        orderDOMapper.insertSelective(orderDO);
        //加上商品的销量
        itemService.increaseSales(itemId,amount);

        return orderModel;
    }



    //订单号的产生
    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOrderNo(){
        Random random=new Random();
        int num=random.nextInt();
        /*StringBuilder stringBuilder=new StringBuilder();
        //1.前八位为时间信息
        LocalDateTime now=LocalDateTime.now();//获取当前时间
        String nowDate=now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);

        //2.中间六位为自增序列，这里通过创建数据表来获取sequence_info
        int sequence=0;
        //获取当前sequence
        SequenceDO sequenceDO=sequenceDOMapper.getSequenceByName("order_info");
        sequence=sequenceDO.getCurrentValue();
        //拿到一次数据后，需要将currentValue按照步长增加
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue()+sequenceDO.getStep());

        //更新数据库
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);

        //拼接
        String sequenceStr=String.valueOf(sequence);
        for(int i=0;i<6-sequenceStr.length();i++){
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);

        //3.最后两位为分库分表位,暂时写死为00
        stringBuilder.append("00");*/

        return String.valueOf(num);
    }
    private OrderDO convertFromModel(OrderModel orderModel){
        if(orderModel==null){
            return null;
        }
        OrderDO orderDO=new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPirce().doubleValue());
        return orderDO;
    }
}

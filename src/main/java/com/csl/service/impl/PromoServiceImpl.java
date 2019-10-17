package com.csl.service.impl;


import com.csl.controller.ItemController;
import com.csl.dao.PromoDOMapper;
import com.csl.dataobject.PromoDO;
import com.csl.service.ItemService;
import com.csl.service.PromoService;
import com.csl.service.model.PromoModel;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class PromoServiceImpl implements PromoService {
    private static final Logger logger= LoggerFactory.getLogger(ItemController.class);
    @Autowired
    private PromoDOMapper promoDOMapper;
    @Autowired
    private ItemService itemService;
    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        //获取对应商品的秒杀活动信息
        PromoDO promoDO=promoDOMapper.selectByItemId(itemId);
        if(promoDO==null){
            //表示该商品没有秒杀活动
            return null;
        }
        PromoModel promoModel=convertFromDataObject(promoDO);
        //判断秒杀是否正在进行
        if(promoModel.getStartTime().isAfterNow()){
            promoModel.setStatus(1);
        }else if(promoModel.getEndTime().isBeforeNow()){
            promoModel.setStatus(3);
        }else {
            promoModel.setStatus(2);
        }

        return promoModel;
    }

    @Override
    public void addPromoItem(Integer id) {
        PromoDO promoDO=new PromoDO();
        promoDO.setItemId(id);
        logger.info("id为"+id);
        promoDO.setPromoName(itemService.getNameById(id));
        logger.info("秒杀商品为"+itemService.getNameById(id));
        promoDO.setPromoItemPrice(itemService.getPriceById(id)-itemService.getPriceById(id)*0.2);
        logger.info("秒杀价格为"+(itemService.getPriceById(id)-itemService.getPriceById(id)*0.2));
        promoDO.setStartTime(new Timestamp(new Date().getTime()+24*60*60*1000));
        logger.info("秒杀开始时间为"+new Date());
        promoDO.setEndTime(new Timestamp(new Date().getTime()+24*60*60*1000*2));
        promoDOMapper.insertSelective(promoDO);
    }

    @Override
    public List<PromoDO> getPromoToCache(Date date) {
        logger.info("时间段1："+new Timestamp(new Date().getTime()+2*60*60*1000).toString());
        logger.info("时间段2："+new Timestamp(new Date().getTime()+4*60*60*1000).toString());
        return promoDOMapper.getPromoToCache(new Timestamp(new Date().getTime()+2*60*60*1000),
                new Timestamp(new Date().getTime()+4*60*60*1000));
    }

    private PromoModel convertFromDataObject(PromoDO promoDO){
        if(promoDO==null){
            return null;
        }
        PromoModel promoModel=new PromoModel();
        BeanUtils.copyProperties(promoDO,promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoModel.setStartTime(new DateTime(promoDO.getStartTime()));
        promoModel.setEndTime(new DateTime(promoDO.getEndTime()));
        return promoModel;
    }
}

package com.csl.common;

import com.csl.dataobject.PromoDO;
import com.csl.service.ItemService;
import com.csl.service.PromoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.List;

@Component
public class TimerConfig {
    private static final Logger logger= LoggerFactory.getLogger(TimerConfig.class);
    @Autowired
    private PromoService promoService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private JedisPool jedisPool;

    @Scheduled(fixedRate = 1*60*60*1000)
    public void addItemToCache(){
        logger.info("秒杀商品写入redis缓存");
        List<PromoDO>list=promoService.getPromoToCache(new Date());
        logger.info("缓存的商品数量为"+list.size());
        Jedis jedis=jedisPool.getResource();
        for(int i=0;i<list.size();i++){
            logger.info("秒杀商品的id为 "+list.get(i).getId()
                    +"，库存为 "+itemService.getStockById(list.get(i).getItemId()));
            jedis.set(String.valueOf(list.get(i).getId()),
                    String.valueOf(itemService.getStockById(list.get(i).getItemId())));
        }
    }
}

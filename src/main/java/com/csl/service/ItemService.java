package com.csl.service;

import com.csl.error.BusinessException;
import com.csl.service.model.ItemModel;


import java.util.List;

public interface ItemService {
    //创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;
    //查看商品列表
    List<ItemModel> listItem();
    //商品详情浏览
    ItemModel getItemById(Integer id);
    //落单减库存
    boolean decreaseStock(Integer itemId, Integer amount);
    //商品销量增加
    void increaseSales(Integer itemId, Integer amount) throws BusinessException;

    String getNameById(Integer id);
    Double getPriceById(Integer id);
    Integer getStockById(Integer id);
}

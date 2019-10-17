package com.csl.service.impl;

import com.csl.dao.ItemDOMapper;
import com.csl.dao.ItemStockDOMapper;
import com.csl.dataobject.ItemDO;
import com.csl.dataobject.ItemStockDO;
import com.csl.error.BusinessException;
import com.csl.error.EmBusinessError;
import com.csl.service.ItemService;
import com.csl.service.PromoService;
import com.csl.service.model.ItemModel;
import com.csl.service.model.PromoModel;
import com.csl.validator.ValidatorImpl;
import com.csl.validator.ValidatorResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;
    @Autowired
    private ItemDOMapper itemDOMapper;
    @Autowired
    private ItemStockDOMapper itemStockDOMapper;
    @Autowired
    private PromoService promoService;

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        ValidatorResult result=validator.validate(itemModel);
        if(result.isHasError()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }
        //将itemModel转为itemDO
        ItemDO itemDO=convertFromItemModel(itemModel);
        //写入数据库
        itemDOMapper.insertSelective(itemDO);

        itemModel.setId(itemDO.getId());

        ItemStockDO itemStockDO=convertStockFromModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);

        //返回创建完成的对象
        return getItemById(itemModel.getId());
    }
    private ItemDO convertFromItemModel(ItemModel itemModel){
        if(itemModel==null){
            return null;
        }
        ItemDO itemDO=new ItemDO();
        BeanUtils.copyProperties(itemModel,itemDO);
        //只有在属性和字段完全相同的情况下才可以复制，所以这对价格还需要手动复制
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }
    //因为ItemDO中没有库存stock字段，所以要从itemModel中取出stock放入ItemStockDO中
    private ItemStockDO convertStockFromModel(ItemModel itemModel){
        if(itemModel==null){
            return null;
        }
        ItemStockDO itemStockDO=new ItemStockDO();
        itemStockDO.setStock(itemModel.getStock());
        itemStockDO.setItemId(itemModel.getId());
        return itemStockDO;
    }

    @Override
    //这个地方很陌生
    public List<ItemModel> listItem() {
        List<ItemDO>itemDOList=itemDOMapper.listItem();
        //因为要展示给前端，所以需要将itemDO map成itemModel
        List<ItemModel>itemModelList=itemDOList.stream().map(itemDO -> {
            ItemStockDO itemStockDO=itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel=this.convertModelFromDataobject(itemDO,itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO=itemDOMapper.selectByPrimaryKey(id);
        if(itemDO==null){
            return null;
        }
        //因为itemDO中没有库存字段，所以要进行操作获得库存数量
        ItemStockDO itemStockDO=itemStockDOMapper.selectByItemId(itemDO.getId());
        //将dataobject转为model
        ItemModel itemModel=convertModelFromDataobject(itemDO,itemStockDO);

        //获取活动商品信息
        PromoModel promoModel=promoService.getPromoByItemId(itemModel.getId());
        if(promoModel!=null&&promoModel.getStatus().intValue()!=3){
            //表示该商品是秒杀活动商品，且活动还未结束
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;
    }

    //下单减库存
    @Override
    public boolean decreaseStock(Integer itemId, Integer amount) {
        int affectRow=itemStockDOMapper.decreaseStock(itemId,amount);
        if(affectRow>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
        itemDOMapper.increaseSales(itemId,amount);
    }

    @Override
    public String getNameById(Integer id) {

        return itemDOMapper.getNameById(id);
    }

    @Override
    public Double getPriceById(Integer id) {
        return itemDOMapper.getPriceById(id);
    }

    @Override
    public Integer getStockById(Integer id) {

        return itemStockDOMapper.getStockById(id);
    }

    private ItemModel convertModelFromDataobject(ItemDO itemDO, ItemStockDO itemStockDO){
        ItemModel itemModel=new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }
}

package com.csl.service;

import com.csl.dataobject.PromoDO;
import com.csl.service.model.PromoModel;

import java.util.Date;
import java.util.List;

public interface PromoService {
     PromoModel getPromoByItemId(Integer itemId);
     void addPromoItem(Integer id);
     List<PromoDO> getPromoToCache(Date date);
}

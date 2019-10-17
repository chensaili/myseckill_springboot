package com.csl.service;

import com.csl.error.BusinessException;
import com.csl.service.model.OrderModel;

public interface OrderService {
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId,Integer amount) throws BusinessException;
    }

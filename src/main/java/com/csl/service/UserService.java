package com.csl.service;

import com.csl.error.BusinessException;
import com.csl.service.model.UserModel;

public interface UserService {
    //根据id查找用户
    UserModel getUserById(Integer id);
    //根据id查找role
    int getRoleById(Integer id);
    //用户注册
    void register(UserModel userModel) throws BusinessException;
    //检验用户登录是否合法
    UserModel validateLogin(String telphone,String encryptPassword) throws BusinessException;
}

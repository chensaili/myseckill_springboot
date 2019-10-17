package com.csl.validator;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class ValidatorImpl implements InitializingBean {
    //javax定义的一套接口校验工具
    private Validator validator;

    //实现校验方法并返回校验结果
    public ValidatorResult validate(Object bean){
        ValidatorResult result=new ValidatorResult();
        //若bean里面参数规则有违背了validation定义的annotation的话，set里面就会有值
        Set<ConstraintViolation<Object>>constraintViolationSet= validator.validate(bean);
        if(constraintViolationSet.size()>0){
            //有错误
            result.setHasError(true);
            //遍历错误,将错误放入map集合中。
            // jdk8特有的lamda表达式
            constraintViolationSet.forEach(constraintViolation->{
                String errMsg=constraintViolation.getMessage();
                String propertyName=constraintViolation.getPropertyPath().toString();
                result.getErrorMsgMap().put(propertyName,errMsg);
            });
            /*for(ConstraintViolation<Object>constraintViolation:constraintViolationSet){
                String errMsg=constraintViolation.getMessage();
                String propertyName=constraintViolation.getPropertyPath().toString();
                result.getErrorMsgMap().put(propertyName,errMsg);
            }*/
        }
        return result;
    }

    /**
     * 当spring bean初始化完成后，会执行此方法
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //将hibernate validator通过工厂的初始化方式使其实例化
        this.validator= Validation.buildDefaultValidatorFactory().getValidator();
    }
}

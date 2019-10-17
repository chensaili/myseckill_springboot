package com.csl.error;

public enum  EmBusinessError implements CommonError{
    //通用错误类型10001
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    UNKNOW_ERROR(10002,"未知错误"),

    //20000开头为用户相关错误
    USER_NOT_EXIST(20001,"用户不存在"),
    USER_LOGIN_FAIL(20002,"手机或密码错误"),
    USER_NOT_LOGIN(20003,"用户还未登陆"),
    USER_NOT_ADMIN(20004,"非管理员无法创建商品"),
    USER_NOT_ADMIN_ADD_PROMO(20004,"非管理员无法添加秒杀商品"),
    //30000开头为交易型错误
    STOCK_NOT_ENOUGH(30001,"库存不足"),
    //40000开头为验证码型错误
    OTP_ERROR(40001,"验证码每隔30秒才能发送一次");

    private int errCode;
    private String errMsg;
    EmBusinessError(int errCode,String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
    @Override
    public int getErrCode() {
        return errCode;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg=errMsg;
        return this;
    }
}

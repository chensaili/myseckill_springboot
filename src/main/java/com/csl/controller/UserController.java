package com.csl.controller;

import com.alibaba.druid.util.StringUtils;
import com.csl.controller.viewobject.UserVo;
import com.csl.error.BusinessException;
import com.csl.error.EmBusinessError;
import com.csl.response.CommonReturnType;
import com.csl.service.impl.UserServiceImpl;
import com.csl.service.model.UserModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

@Api(value = "用户模块",tags = "用户模块")
@RestController
@RequestMapping(value = "/user")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class UserController extends BaseController{
    private static final Logger logger= LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private HttpServletRequest request;

    @ApiOperation(value = "获取用户信息")
    @RequestMapping(value = "/get",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        UserModel userModel=userService.getUserById(id);
        if(userModel==null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        return CommonReturnType.create(convertVoFromModel(userModel));
    }
    private UserVo convertVoFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserVo userVo=new UserVo();
        BeanUtils.copyProperties(userModel,userVo);
        return userVo;
    }

    @ApiOperation(value = "获取验证码")
    @RequestMapping(value = "/getotp", method = {RequestMethod.POST}, consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telphone")String telphone) throws BusinessException {
        long lastTime=System.currentTimeMillis();
        if(request.getSession().getAttribute(telphone)==null){
            //表示该用户是第一次获取验证码
            request.getSession().setAttribute(telphone+"otp",lastTime);
        }else if(lastTime-Long.valueOf(request.getSession().getAttribute(telphone+"otp").toString())<30000){
            throw new BusinessException(EmBusinessError.OTP_ERROR);
        }
        //System.out.println(lastTime);
       // System.out.println(request.getSession().getAttribute(telphone+"otp"));
       // System.out.println(lastTime-Long.valueOf(request.getSession().getAttribute(telphone+"otp").toString()));
        //按规则生成验证码
        Random random=new Random();
        int randomInt=random.nextInt(9999);
        randomInt+=10000;
        String otpcode=String.valueOf(randomInt);
        //将电话与验证码存入session域
        request.getSession().setAttribute(telphone,otpcode);
        //将OtP验证码通过短信通道发送给用户（省略）
        System.out.println("telphone = " + telphone + "  &otpcode = " + otpcode);
        return CommonReturnType.create(null);
    }

    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
                                     @RequestParam(name = "otpcode") String otpcode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") Integer gender,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name="password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和对应otpcode相符
        String inSessionOtpCode=(String)request.getSession().getAttribute(telphone);
        logger.info("验证码为"+inSessionOtpCode);
        if(!StringUtils.equals(otpcode,inSessionOtpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证不符合");
        }
        //验证用户是否已经注册
        //getUserByTelPhone(String telphone)
        //如果用户已经注册，那么跳转到用户登录页面
        //短信验证符合的情况下,并且数据中不存在该用户，那么开始用户注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.enCodeByMD5(password));

        logger.info("注册获取用户信息正常");

        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    @ApiOperation(value = "用户登录")
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone")String telphone,
                                  @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if(StringUtils.isEmpty(telphone)||StringUtils.isEmpty(password)){
           throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //检验用户登录是否合法，就是检验用户输入的密码是否正确
        UserModel userModel=userService.validateLogin(telphone,enCodeByMD5(password));
        //将登录成功的凭证加入session中
        this.request.getSession().setAttribute("IS_LOGIN",true);
        //将登录成功的UserModel也放入session里
        this.request.getSession().setAttribute("LOGIN_USER",userModel);
        return CommonReturnType.create(null);
    }

    //展示注册过的用户，可以将用户设为管理员
    @ApiOperation(value = "用户管理")
    @RequestMapping(value = "/userManage", method = {RequestMethod.POST}, consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public CommonReturnType manage(@RequestParam(name = "telphone")String telphone,
                                  @RequestParam(name = "password")String password) throws BusinessException {
        Boolean isLogin=(Boolean) request.getSession().getAttribute("IS_LOGIN");
        if(isLogin==null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
        UserModel userModel=(UserModel) request.getSession().getAttribute("LOGIN_USER");
        logger.info("登录用户的id为"+userModel.getId());
        int role=userService.getRoleById(userModel.getId());
        logger.info("登录用户的role为"+role);

        //还未实现
        //验证角色后，超级管理员可以通过一个按钮将某个用户设置为管理员
        //管理员可以通过这个页面查看所有已经注册过的用户，但是无法将普通用户设置为管理员
        return CommonReturnType.create(null);
    }



    public String enCodeByMD5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // 确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        // 加密字符串
        String newStr = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return newStr;
    }
}

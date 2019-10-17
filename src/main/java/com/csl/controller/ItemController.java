package com.csl.controller;

import com.csl.controller.viewobject.ItemVo;
import com.csl.error.BusinessException;
import com.csl.error.EmBusinessError;
import com.csl.response.CommonReturnType;
import com.csl.service.ItemService;
import com.csl.service.PromoService;
import com.csl.service.UserService;
import com.csl.service.model.ItemModel;
import com.csl.service.model.UserModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Api(value = "商品模块",tags = "商品模块")
@RestController
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class ItemController extends BaseController{
    private static final Logger logger= LoggerFactory.getLogger(ItemController.class);
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private PromoService promoService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @ApiOperation(value = "创建商品")
    //创建商品
    @RequestMapping(value = "/create",method = RequestMethod.POST,consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title")String title,
                                       @RequestParam(name = "description")String description,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock")Integer stock,
                                       @RequestParam(name = "imgUrl")String imgUrl) throws BusinessException {
         Boolean isLogin=(Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
         if(isLogin==null){
             throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
         }
         UserModel userModel=(UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
         logger.info("登录用户的id为"+userModel.getId());
         int role=userService.getRoleById(userModel.getId());
         logger.info("登录用户的role为"+role);
         if(role==1){
             //封装service请求用来创建商品
             ItemModel itemModel=new ItemModel();
             itemModel.setTitle(title);
             itemModel.setDescription(description);
             itemModel.setPrice(price);
             itemModel.setStock(stock);
             itemModel.setImgUrl(imgUrl);
             ItemModel itemModel1FroReturn=itemService.createItem(itemModel);
             ItemVo itemVo=convertItemVoFromModel(itemModel1FroReturn);
             return CommonReturnType.create(itemVo);
         }else {
             throw new BusinessException(EmBusinessError.USER_NOT_ADMIN);
         }
    }

    @ApiOperation(value = "商品列表查询")
    //商品列表查询
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType listItem(){
        List<ItemModel>itemModelList=itemService.listItem();
        //使用stream api将itemModel转化为itemVo
        List<ItemVo>itemVoList=itemModelList.stream().map(itemModel -> {
            ItemVo itemVo=convertItemVoFromModel(itemModel);
            return itemVo;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVoList);
    }

    @ApiOperation(value = "商品详情查询")
    //商品详情页浏览
    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id")Integer id) throws BusinessException {
        ItemModel itemModel=itemService.getItemById(id);
        ItemVo itemVo=convertItemVoFromModel(itemModel);
        return CommonReturnType.create(itemVo);
    }

    @ApiOperation(value = "管理员添加秒杀商品")
    //添加秒杀商品，商品的id由前端传过来
    @RequestMapping(value = "/addPromo",method = RequestMethod.POST,consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public CommonReturnType addPromoItem(@RequestParam(value = "id") Integer id) throws BusinessException {
        logger.info("添加秒杀商品");
        //我是用postman进行测试的，所以把角色验证这部分代码注释了
        /*Boolean isLogin=(Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(isLogin==null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
        UserModel userModel=(UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        logger.info("登录用户的id为"+userModel.getId());
        int role=userService.getRoleById(userModel.getId());
        logger.info("登录用户的role为"+role);
        if(role==1){*/
            promoService.addPromoItem(id);
            return CommonReturnType.create(null);
       /* }
        throw new BusinessException(EmBusinessError.USER_NOT_ADMIN_ADD_PROMO);*/
    }
    private ItemVo convertItemVoFromModel(ItemModel itemModel){
        if(itemModel==null){
            return null;
        }
        ItemVo itemVo=new ItemVo();
        BeanUtils.copyProperties(itemModel,itemVo);
        if(itemModel.getPromoModel()!=null){
            //有正在进行或者即将进行的秒杀活动
            itemVo.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVo.setPromoId(itemModel.getPromoModel().getId());
            itemVo.setStartTime(itemModel.getPromoModel().getStartTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVo.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else {
            itemVo.setPromoStatus(0);
        }
        return itemVo;
    }

    /*@RequestMapping(value = "/getid",method = RequestMethod.GET)
    public ModelAndView test(@RequestParam("ID") Integer id,String name) throws BusinessException {
        ModelAndView mv=new ModelAndView();
        System.out.println(id+name);
        return mv;
    }*/
}


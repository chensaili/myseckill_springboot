package com.csl.dao;

import com.csl.dataobject.PromoDO;
import com.csl.service.model.PromoModel;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface PromoDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbggenerated Mon May 13 17:41:23 CST 2019
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbggenerated Mon May 13 17:41:23 CST 2019
     */
    int insert(PromoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbggenerated Mon May 13 17:41:23 CST 2019
     */
    int insertSelective(PromoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbggenerated Mon May 13 17:41:23 CST 2019
     */
    PromoDO selectByPrimaryKey(Integer id);
    PromoDO selectByItemId(Integer itemId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbggenerated Mon May 13 17:41:23 CST 2019
     */
    int updateByPrimaryKeySelective(PromoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbggenerated Mon May 13 17:41:23 CST 2019
     */
    int updateByPrimaryKey(PromoDO record);

    List<PromoDO> getPromoToCache(@Param("date1") Date date1,@Param("date2")  Date date2);
}
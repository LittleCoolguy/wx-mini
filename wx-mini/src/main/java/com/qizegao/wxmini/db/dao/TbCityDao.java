package com.qizegao.wxmini.db.dao;

import com.qizegao.wxmini.db.pojo.TbCity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbCityDao {

    //根据城市名查询城市编号
    public String searchCode(String city);
}
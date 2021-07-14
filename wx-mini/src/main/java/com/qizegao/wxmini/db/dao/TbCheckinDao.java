package com.qizegao.wxmini.db.dao;

import com.qizegao.wxmini.db.pojo.TbCheckin;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface TbCheckinDao {

    //判断当天是否已经签到
    public Integer haveCheckin(int userId);

    //保存签到结果
    public void insert(TbCheckin checkin);

    //查询当天的签到结果及用户的基本信息
    public HashMap searchTodayCheckin(int userId);

    //统计用户总的签到天数
    public long searchCheckinDays(int userId);
}
package com.qizegao.wxmini.service;

import java.util.HashMap;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/29 17:11
 */
public interface CheckinService {

    //判断当天是否可以签到
    public String validCanCheckIn(int userId);

    //将签到结果保存在checkin表
    public void checkin(HashMap param);

    //查询当天的签到结果及用户的基本信息
    public HashMap searchTodayCheckin(int userId);

    //统计用户总的签到天数
    public long searchCheckinDays(int userId);
}

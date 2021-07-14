package com.qizegao.wxmini.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.qizegao.wxmini.db.dao.TbCheckinDao;
import com.qizegao.wxmini.db.dao.TbCityDao;
import com.qizegao.wxmini.db.dao.TbUserDao;
import com.qizegao.wxmini.db.pojo.TbCheckin;
import com.qizegao.wxmini.exception.EmosException;
import com.qizegao.wxmini.service.CheckinService;
import com.qizegao.wxmini.task.EmailTask;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/29 17:12
 */

@Service
@Scope("prototype")
@Slf4j
public class CheckinServiceImpl implements CheckinService {

    @Autowired
    private TbCheckinDao checkinDao;

    @Autowired
    private TbCityDao cityDao;

    @Autowired
    private TbUserDao userDao;

    @Value("${emos.email.hr}")
    private String hrEmail;

    @Autowired
    private EmailTask emailTask;

    @Override
    public String validCanCheckIn(int userId) {
        boolean bool = checkinDao.haveCheckin(userId) != null;
        return bool ? "今日已经考勤，不用重复考勤" : "可以考勤";
    }

    @Override
    public void checkin(HashMap param) {

        //查询疫情风险等级
        int risk = 1;
        int userId = (Integer) param.get("userId");
        String city = (String) param.get("city");
        String district = (String) param.get("district");
        String address = (String) param.get("address");
        String country = (String) param.get("country");
        String province = (String) param.get("province");

        if (!StrUtil.isBlank(city) && !StrUtil.isBlank(district)) {

            //得到城市编码
            String code = cityDao.searchCode(city);

            //利用jsoup得到疫情等级
            try {

                //发送请求
                String url = "http://m." + code + ".bendibao.com/news/yqdengji/?qu=" + district;
                Document document = Jsoup.connect(url).get();
                Elements elements = document.getElementsByClass("list-content");
                if (elements.size() > 0) {
                    Element element = elements.get(0);
                    String result = element.select("p:last-child").text();

                    if ("高风险".equals(result)) {
                        risk = 3;

                        //发送告警邮件

                        HashMap<String, String> map = userDao.searchNameAndDept(userId);
                        String name = map.get("name");
                        String deptName = map.get("dept_name");
                        deptName = deptName != null ? deptName : "";
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setTo(hrEmail);
                        message.setSubject("员工" + name + "身处高风险疫情地区警告");
                        message.setText(deptName + "员工" + name + "，" + DateUtil.format(new Date(), "yyyy年MM月dd日") + "处于" + address + "，属于新冠疫情高风险地区，请及时与该员工联系，核实情况！");

                        //调用异步方法发送邮件
                        emailTask.sendAsync(message);

                    } else if ("中风险".equals(result)) {
                        risk = 2;
                    }
                }
            } catch (Exception e) {
                log.error("执行异常", e);
                throw new EmosException("获取风险等级失败");
            }
        }

        //保存签到记录
        TbCheckin entity = new TbCheckin();
        entity.setUserId(userId);
        entity.setAddress(address);
        entity.setCountry(country);
        entity.setProvince(province);
        entity.setCity(city);
        entity.setDistrict(district);
        entity.setRisk(risk);
        entity.setDate(DateUtil.today());

        //获取当前日期
        Date d1 = DateUtil.date();
        entity.setCreateTime(d1);

        //保存记录
        checkinDao.insert(entity);
    }

    @Override
    public HashMap searchTodayCheckin(int userId) {
        HashMap map = checkinDao.searchTodayCheckin(userId);
        return map;
    }

    @Override
    public long searchCheckinDays(int userId) {
        long days = checkinDao.searchCheckinDays(userId);
        return days;
    }
}

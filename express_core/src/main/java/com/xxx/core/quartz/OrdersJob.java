package com.xxx.core.quartz;

/**
 * @Description:
 * @Author: disvenk.dai
 * @Date: 下午 1:01 2018/1/27 0027
 */


import com.xxx.core.dao.ExpReceiverOrderDao;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;


public class OrdersJob implements Job {

    //无法注入的问题是说，当前这个类并没有没spring管理，它要注入被spring管理的类是无法成功的
    @Autowired
    private ExpReceiverOrderDao expReceiverOrderDao;

    public void execute(JobExecutionContext context)
            throws JobExecutionException {

       expReceiverOrderDao.updateOrderStatusToBack();
    System.out.println("成功的执行了定时批量更新订单为‘已退回’");
    }

}

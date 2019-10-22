package com.xxx.core.query;


import com.alibaba.fastjson.JSON;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangh on 2015/11/1.
 */
public class PageQuery {
    public PageQuery() {
        this.pageSize = 10;
    }

    public PageQuery(Integer start, Integer limit) {
        this();
        this.start = start;
        this.limit = limit;
    }

    public PageQuery(Integer pageNum) {
        this();
        this.page = pageNum;
        this.start = (pageNum - 1) * pageSize;
        this.limit = pageSize;
    }

    public Integer pageSize;  //从1开始
    public Integer page; //当前页（从1开始）,用于 lucene 分页查询
    public Integer totalPage; //总页数，设置 limit 时会自动填充
    public Long total = 0l; //分页查询时，实际的记录总数。此字段在查询后被填充
    public Integer moduleId;  //模块ID，模块表中的过滤及排序是权重最高的。前端可以使用"?moduleId=${moduleId}"将模块ID放入url参数中


    public Map params = new HashMap();

    public boolean hibernateCacheable = false;  //仅支持hibernate查询模式

    //################################# 一、bootstrap-table ############################
    //bootstrap-table 插件分页请求格式：?search=aaa&sort=id&order=asc&limit=1&offset=0
    //bootstrap-table 插件分页默认接受的返回json格式：{rows:[{},{}],total:2}，使用"dataField"可改变这个"rows"。
    public String sort;
    public String order;
    public Integer offset;     //和extjs中的start一个意思
    public Integer limit;
    public String search;      //搜索
    //###################################################################################


    //################################# 二、extjs grid #################################
    //sort：[{"property":"id","direction":"ASC"},{"property":"name","direction":"DESC"}]
    public Integer start;
    public String filter;  //可接收来自extjs过滤插件上的过滤格式，但过滤格式有所扩展

    //正在整理.....
    //#################################################################################


    //################################# 三、easyui treegrid #############################
    //page=2&rows=2&id=0
    //注意：如果获取list，需要返回格式：{rows:[{"id":1,"name":"张三","parentId":"0","status":"closed"},{"id":11,"name":"李四","parentId":"1","status":"open"},{"id":12,"name":"李四","parentId":"1","status":"open"}],total:1}
    //      如果status=closed，则会有个合起来的箭头的。默认情况下，点击它，会发送一个ajax请求动态获取子选项数据。其中参数id不再是0，而是点击的那条记录。对于子选项数据的返回格式需要注意，不再是个对象，而仅仅是一个数组了。
    public Integer rows;   //和 limit 属性一样。
    public Integer id;     //参数表示树节点的值
    //###################################################################################

    public String searchAssociationFields; //搜索要的关联的字段，多个用逗号隔开。
    public String hibernateFetchFields;    //设置fetch字段，多个用逗号隔开。 hibernate默认使用延迟加载策略，但系统一概使用这种策略有时反而不好，此字段就是用于设置。

    public List<Order> hibernateOrders = new ArrayList<>();  //调用"List getList(Class c, PageQuery pq)"函数后，分页请求中携带的排序信息都会存放在此集合中。如果需要，使用此对象，您可以在controller层加些额外排序条件。
    public Criterion hibernateCriterion;  //注意：这里使用"user.departmentId"这种是不支持的，因为没有解析它我。调用"List getList(Class c, PageQuery pq)"函数后，filter 过滤表达式信息都会存放在此对象中。如果需要，使用此对象，您可以在controller层加些额外过滤条件。

    public boolean logicDeleted = false;    //系统内置查询
    public String companyid;                //系统内置查询

    @Override
    public String toString() {
        String hibernateCriterionStr = "";
        //提示：Criterion 转字符串时，必须手动转，List<Order> 不需要！
        if (hibernateCriterion != null)
            hibernateCriterionStr = hibernateCriterion.toString();
        return JSON.toJSONString(this) + "[" + hibernateCriterionStr + "]";
    }
}

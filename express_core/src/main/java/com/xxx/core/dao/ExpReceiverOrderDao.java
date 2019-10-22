package com.xxx.core.dao;


import org.springframework.stereotype.Repository;

@Repository
public class ExpReceiverOrderDao extends CommonDao{

    public int updateOrderStatusToBack(){
        return mybatisRepository.getCurrentSession().update("mybatis.mappers.ExpReceiverOrderMapper.updateOrderStatusToBack");
    }


}

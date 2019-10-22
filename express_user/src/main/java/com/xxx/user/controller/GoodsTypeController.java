package com.xxx.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpGoodsType;
import com.xxx.user.service.GoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 物品类别
 * @Author: disvenk.dai
 * @Date: 2018/1/8
 */
@RestController
@RequestMapping("/goods")
public class GoodsTypeController {

    @Autowired
    private GoodsTypeService goodsTypeService;

   /**
    * @Description: 物品类别列表接口
    * @Author: disvenk.dai
    * @Date: 2018/1/8
    */
    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    public ResponseEntity list() throws Exception {
        List<ExpGoodsType> list = goodsTypeService.findExpGoodsTypeList();
        JSONArray data = new JSONArray();
        for (ExpGoodsType expGoodsType : list) {
            JSONObject json = new JSONObject();
            json.put("id", expGoodsType.getId());
            json.put("name", expGoodsType.getName());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
    }

}

package com.xxx.clients.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.clients.form.AddressForm;
import com.xxx.clients.form.IdForm;
import com.xxx.clients.service.ReceiveAddressService;
import com.xxx.clients.service.SendAddressService;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpReceiveAddress;
import com.xxx.model.business.ExpSendAddress;
import com.xxx.user.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 寄件人地址
 */
@RestController
@RequestMapping("/sendAddress")
public class SendAddressController {

    @Autowired
    private SendAddressService sendAddressService;

    /**
     * @Description: 获取寄件地址列表
     * @Author: Chen.zm
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    public ResponseEntity list() throws Exception {
        List<ExpSendAddress> list = sendAddressService.findList(CurrentUser.get().userId);
        JSONArray data = new JSONArray();
        for (ExpSendAddress address : list) {
            JSONObject json = new JSONObject();
            json.put("id", address.getId());
            json.put("name", address.getName());
            json.put("tel", address.getTel());
            json.put("province", address.getProvince());
            json.put("city", address.getCity());
            json.put("zone", address.getZone());
            json.put("address", address.getAddress());
            json.put("isDefault", address.getDefault());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }


    /**
     * @Description: 寄件地址详情
     * @Author: Chen.zm
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/detail", method = {RequestMethod.POST})
    public ResponseEntity detail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        ExpSendAddress address = sendAddressService.getAddress(form.id, CurrentUser.get().userId);
        if (address == null)
            return new ResponseEntity(new RestResponseEntity(120, "收件地址不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("id", address.getId());
        json.put("name", address.getName());
        json.put("tel", address.getTel());
        json.put("province", address.getProvince());
        json.put("city", address.getCity());
        json.put("zone", address.getZone());
        json.put("address", address.getAddress());
        json.put("isDefault", address.getDefault());
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description: 寄件人地址删除
     * @Author: Chen.zm
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/remove", method = {RequestMethod.POST})
    public ResponseEntity remove(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        sendAddressService.removeAddress(form.id, CurrentUser.get().userId);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 寄件人地址设为默认
     * @Author: Chen.zm
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/saveDefault", method = {RequestMethod.POST})
    public ResponseEntity saveDefault(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        sendAddressService.saveDefault(form.id, CurrentUser.get().userId);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 寄件人地址保存
     * @Author: Chen.zm
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    public ResponseEntity save(@RequestBody AddressForm form) throws Exception {
        ExpSendAddress address = sendAddressService.save(form, CurrentUser.get().userId);
        JSONObject json = new JSONObject();
        json.put("id", address.getId());
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }


}

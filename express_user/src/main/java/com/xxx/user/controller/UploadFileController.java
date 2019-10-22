package com.xxx.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.user.form.Base64Form;
import com.xxx.user.form.IdForm;
import com.xxx.user.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/uploadFile")
public class UploadFileController {

	@Autowired
	private UploadFileService uploadFileService;


	/**
	 * @Description: 上传单张图片
	 * @Author: Chen.zm
	 * @Date: 2017/11/28 0028
	 */
	@RequestMapping(value = "/saveUploadFile", method = {RequestMethod.POST})
	public ResponseEntity saveUploadFile(@RequestBody Base64Form form) throws Exception {
 		Integer key = uploadFileService.saveOssUploadFileByBase64(form.base64);
		JSONObject json = new JSONObject();
		json.put("key", key);
		return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
	}

	/**
	 * @Description: 上传视频
	 * @Author: Chen.zm
	 * @Date: 2017/11/28 0028
	 */
	@RequestMapping(value = "/saveUploadFileVideo", method = {RequestMethod.POST})
	public ResponseEntity saveUploadFileVideo(@RequestBody Base64Form form) throws Exception {
		Integer key = uploadFileService.saveOssUploadFileByBase64_video(form.base64);
		JSONObject json = new JSONObject();
		json.put("key", key);
		return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
	}

	/**
	 * @Description: 删除文件
	 * @Author: Chen.zm
	 * @Date: 2017/11/28 0028
	 */
	@RequestMapping(value = "/removeUploadFile", method = {RequestMethod.POST})
	public ResponseEntity removeUploadFile(@RequestBody IdForm form) throws Exception {
		uploadFileService.deleteUploadFile(form.id);
		return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
	}

}

package com.xxx.core.response;

import org.springframework.http.HttpStatus;

/**
 * Created by wanghua on 17/1/19.
 */
public class RestResponseEntity extends BaseResponseEntity {
    public Object data;

    public RestResponseEntity(RestResponseEntity 页码不能为空, HttpStatus ok) {
    }

    public RestResponseEntity(int responseStatus, String message, Object data) {
        super(responseStatus, message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

package com.xxx.core.cache;

import com.alibaba.fastjson.JSON;
import com.xxx.core.query.PageQuery;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by wanghua on 16/11/2.
 */
public class CustomKeyGenerator extends SimpleKeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(target.getClass().getName()).append(".");
        buffer.append(method.getName()).append(".");
        if (params.length > 0) {
            for (Object each : params) {
                if (each != null) {
                    if (each instanceof Boolean || each instanceof Character || each instanceof Void
                            || each instanceof Short || each instanceof Byte || each instanceof Double
                            || each instanceof Float || each instanceof Integer || each instanceof Long) {
                        buffer.append(each);
                    } else if (each instanceof Object[]) {
                        //Integer数组使用的就是Object类的hashCode是个内存地址，每次执行都变，要改用Arrays.hashCode(array)才不会变
                        //buffer.append(Arrays.hashCode((Object[]) each));
                        buffer.append(Arrays.deepHashCode((Object[]) each));  //如果是数组，那么需要为每个元素当做单独的域来处理。如果你使用的是1.5及以上版本的JDK，那么没必要自己去重新遍历一遍数组，java.util.Arrays.hashCode方法包含了8种基本类型数组和引用数组的hashCode计算。
                    } else if (each instanceof HttpServletRequest || each instanceof HttpServletResponse) {
                        continue;
                    } else if (each instanceof Map) {
                        buffer.append(JSON.toJSONString(exclude(each)).hashCode());
                    } else if (each instanceof PageQuery) {
                        PageQuery pq = (PageQuery) each;
                        buffer.append(pq.toString().hashCode());
                    } else if (each instanceof Object) {
                        //buffer.append(each.hashCode());  //可能会得到不同的hashCode，不能这样用.
                        buffer.append(JSON.toJSONString(each).hashCode());
                    } else {
                        buffer.append(each.hashCode());
                    }
                } else {
                    //buffer.append(NO_PARAM_KEY);
                }
            }
        } else {
            //buffer.append(NO_PARAM_KEY);
        }
        return buffer.toString().hashCode();
    }


    private Object exclude(Object obj) {
//        try {
//            if (obj instanceof Map) {
//                Map map = (Map) obj;
//                for (Object key : map.keySet()) {
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return obj;
    }
}
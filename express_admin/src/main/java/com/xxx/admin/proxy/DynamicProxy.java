package com.xxx.admin.proxy;

import sun.management.Agent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxy implements InvocationHandler {
    //创建目标对象
    private Object object;

    public DynamicProxy(Object object) {
        this.object = object;
    }

    public void before(){
        System.out.println("before");
    }

    public void after(){
        System.out.println("after");
    }

    //创建代理对象
    public Object createProxy() {
        ClassLoader loader = object.getClass().getClassLoader();
        Class[] interfaces = object.getClass().getInterfaces();
        return Proxy.newProxyInstance(loader,interfaces,this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before();
        if(args[0].equals("SB")){
            System.out.println("他是个傻逼，不理他");
            return null;
        }
        for(Object obj : args){
            System.out.println(obj);
        }
        Object result = method.invoke(object,args);
        after();
        return result;
    }


}

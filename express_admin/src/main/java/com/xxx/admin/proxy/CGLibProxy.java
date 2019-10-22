package com.xxx.admin.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CGLibProxy implements MethodInterceptor {

    //声明目标对象
    private Object object;

    public CGLibProxy (Object obj){
        this.object = obj;
    }

    public void before(){
        System.out.println("before");
    }

    public void after(){
        System.out.println("after");
    }

    //创建代理对象
    public Object createProxy(){
        //创建Enhancer
        Enhancer enhancer = new Enhancer();

        //传递目标对象的class
        enhancer.setSuperclass(object.getClass());

        //设置回调操作
        enhancer.setCallback(this);

        return enhancer.create();
    }
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        before();
        if(!args[0].equals("NB")){
            System.out.println("他不牛逼，我们不和他玩");
            return null;
        }
       // Object result = method.invoke(object,args);
        Object result = methodProxy.invokeSuper(object,args);
        after();
        return result;
    }

    public static void main(String[] args){
        CGLibProxy cgLibProxy = new CGLibProxy(new HelloImpl());
        HelloImpl proxy = (HelloImpl) cgLibProxy.createProxy();
        proxy.say("NB");
    }
}

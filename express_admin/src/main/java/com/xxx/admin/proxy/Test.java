package com.xxx.admin.proxy;

import javax.sound.midi.Instrument;

public class Test {

    public static void main(String[] args){

        //创建目标对象
        Hello hello = new HelloImpl();
        //创建代理对象
        DynamicProxy proxy = new DynamicProxy(hello);
        //得到一个Hello的代理对象
        Hello helloProxy = (Hello) proxy.createProxy();
        helloProxy.say("SB");
    }
}

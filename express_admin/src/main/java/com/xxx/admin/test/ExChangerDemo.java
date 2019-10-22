package com.xxx.admin.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
*Exchanger 线程之间数据交换
 */

public class ExChangerDemo {

   public static void main(String[] args){
       //定义交换器，交换String类型数据，当然可以为任意数据类型
       Exchanger<String> exchanger = new Exchanger<String>();
       //定义线程池
       ExecutorService threadPool = Executors.newCachedThreadPool();

        //绑架者A
       threadPool.execute(new Runnable() {
           @Override
           public void run() {
               try {
                   //准备人质B
                   String renzhi = "B";
                   String money = exchanger.exchange(renzhi);
                   System.out.println("绑架者用B交换回："+money);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       });

       //家属C
       threadPool.execute(new Runnable() {
           @Override
           public void run() {
               //交换
               try {
                   //准备1000万
                   String money = "1000万";
                   String renzhi = exchanger.exchange(money);
                   System.out.println("C用1000万换回："+renzhi);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       });

       threadPool.shutdown();
   }
}

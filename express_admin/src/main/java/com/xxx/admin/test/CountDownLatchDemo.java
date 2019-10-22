package com.xxx.admin.test;

import java.util.concurrent.CountDownLatch;

/**
 * CountDownLatchDemo 倒计时器
 */

public class CountDownLatchDemo {
    //倒计时器


   public static void main(String[] args) throws InterruptedException {
       final CountDownLatch count = new CountDownLatch(2);

       //模拟第一个子任务
       new Thread(){
           @Override
           public void run() {
               //模拟任务执行时间
               try {
                   Thread.sleep((long)(Math.random()*10000));
                   System.out.println("子线程B"+Thread.currentThread().getName()+"正在执行");
                   Thread.sleep((long)(Math.random()*10000));
                   System.out.println("子任务B"+Thread.currentThread().getName()+"正在完毕");
                   //倒计时减1
                    count.countDown();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       }.start();

       //模拟第二个子任务
       new Thread(){
           @Override
           public void run() {
               //模拟任务执行时间
               try {
                   Thread.sleep((long)(Math.random()*10000));
                   System.out.println("子线程C"+Thread.currentThread().getName()+"正在执行");
                   Thread.sleep((long)(Math.random()*10000));
                   System.out.println("子任务C"+Thread.currentThread().getName()+"正在完毕");
                   //倒计时减1
                   count.countDown();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       }.start();

       //模拟第三个子任务
       new Thread(){
           @Override
           public void run() {
               //模拟任务执行时间
               try {
                   Thread.sleep((long)(Math.random()*10000));
                   System.out.println("子线程D"+Thread.currentThread().getName()+"正在执行");
                   Thread.sleep((long)(Math.random()*10000));
                   System.out.println("子任务D"+Thread.currentThread().getName()+"正在完毕");
                   //倒计时减1
                   count.countDown();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       }.start();

       //主任务
       System.out.println("等待三个子任务执行完毕，"+Thread.currentThread().getName()+"主任务才开始执行");
       //等待子任务执行完毕，此时阻塞
           count.await();
       System.out.println("说明此时BCD三个子任务已经执行完毕");
       //继续执行主任务
       System.out.println("继续执行主任务：");
   }
}

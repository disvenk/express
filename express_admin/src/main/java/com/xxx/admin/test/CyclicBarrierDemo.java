package com.xxx.admin.test;


import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *同步屏障 CyclicBarrier
 */

public class CyclicBarrierDemo {

    public static void main(String[] args){
        //cyclicBarrier不止有这么点功能，还有一个构造，可以在吃饭前拍个照
        final CyclicBarrier cb = new CyclicBarrier(3, new Runnable() {
            //如果我们复用了这个方式，那么洗脚的人员全部到齐之后，一样也会执行一遍这个方法
            @Override
            public void run() {
                System.out.println("人员都到齐了，各自拍照留恋。。。");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        //线程池
        ExecutorService threadPool = Executors.newCachedThreadPool();
        //模拟3个用户
        for(int i=0;i<3;i++){
            final int user = i+1;
           Runnable r = new Runnable(){
                @Override
                public void run() {
                    //模拟每个人来的时间各不一样
                    try {
                        Thread.sleep((long)(Math.random()*10000));
                        System.out.println(user+"到达聚餐地点，当前已有"+(cb.getNumberWaiting()+1)+"人到达");
                        //只有当线程都到达之后才能往下面走
                            cb.await();
                            if(user==1){
                                System.out.println("人员全部到齐，准备吃饭。。。");
                            }
                            Thread.sleep((long)(Math.random()*10000));
                            System.out.println(user+"吃完饭，准备去洗脚。。");

                        /*本来吃完饭可以直接回家，下面我们可以接着写上面一模一样的格式，可以复用这个方式，再去洗个脚*/
                        Thread.sleep((long)(Math.random()*10000));
                        System.out.println(user+"到达足浴地点，当前已有"+(cb.getNumberWaiting()+1)+"人到达");
                        //只有当线程都到达之后才能往下面走
                        cb.await();
                        if(user==1){
                            System.out.println("人员全部到齐，准备洗脚。。。");
                        }
                        Thread.sleep((long)(Math.random()*10000));
                        System.out.println(user+"洗完脚，准备回家。。");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

           threadPool.execute(r);
        }
        threadPool.shutdown();
    }
}

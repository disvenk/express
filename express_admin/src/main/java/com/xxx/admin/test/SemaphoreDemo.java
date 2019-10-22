package com.xxx.admin.test;

import com.xxx.utils.HttpClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 *信号量：semaphore
 */

public class SemaphoreDemo {

    class MyTask implements Runnable{
        private Semaphore semaphore;//信号量

        private int user;//第几个用户

        public MyTask(Semaphore semaphore, int user) {
            this.semaphore = semaphore;
            this.user = user;
        }

        @Override
        public void run() {

            try {
                //获取到信号量许可，才能占用窗口
                semaphore.acquire();
                //运行到了这里说明获取到了许可，可以去买票了
                System.out.println("用户"+user+"进入窗口，准备买票。。。");
                Thread.sleep((long)(Math.random()*10000));
                System.out.println("用户"+user+"买票完成准备离开。。。");
                Thread.sleep((long)(Math.random()*10000));
                System.out.println("用户"+user+"离开售票窗口。。。");
                //释放信号量许可证
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void execute(){
        //定义窗口个数
        final Semaphore s = new Semaphore(1);

        //线程池
        ExecutorService threadPool = Executors.newCachedThreadPool();

        //模拟20个用户
        for(int i=0;i<20;i++){
            threadPool.execute(new MyTask(s,(i+1)));
        }

        //关闭线程池
        threadPool.shutdown();
    }

    public static void main(String[] args){
        SemaphoreDemo semaphoreDemo =new SemaphoreDemo();
        semaphoreDemo.execute();

    }
}

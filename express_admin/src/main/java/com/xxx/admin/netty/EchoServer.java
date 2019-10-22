package com.xxx.admin.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;

import java.net.Inet4Address;
import java.net.InetSocketAddress;

public class EchoServer {
    private static final Logger LOGGER = Logger.getLogger(EchoServer.class.getName());
    private int port;

    public EchoServer(int port){
        this.port = port;
    }

    //引导服务方法启动
    public void start() throws InterruptedException {
        //引入处理逻辑实例
        final EchoServerHandler echoServerHandler = new EchoServerHandler();
        //线程池组
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            //serverBootstrap:辅助引导服务启动的类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //构建模式
            serverBootstrap.group(group)
                    //指定所使用的NIO传输NioServerSocketChannel
                    .channel(NioServerSocketChannel.class)
                    //使用指定的端口设置套接字地址
                    .localAddress(new InetSocketAddress(port))
                    //添加一个EchoServerHandler到子Channel的ChannelPipeLine
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //这里EchoServerHandler被@sharable标注，代表线程安全，所以可以用同一个实例
                            //开发大家自己处理的handler
                            socketChannel.pipeline().addLast(echoServerHandler);
                        }
                    });

            //sync()就是一个不断循环监听的口子，敏感EVENT时间类型进行监听

            ChannelFuture channelFuture = serverBootstrap.bind().sync();

            LOGGER.info(EchoServer.class.getName()+"started and listening for connections on"
                    +channelFuture.channel().localAddress());
            //获取Channel的closeFuture并且阻塞当前线程直到它完成
            channelFuture.channel().closeFuture().sync();
        }finally {
            //关闭EventLoopGroup释放所有资源
                group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args){
        int port = 8080;
        try {
            new EchoServer(port).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

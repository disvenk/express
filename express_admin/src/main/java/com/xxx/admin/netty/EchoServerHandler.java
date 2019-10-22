package com.xxx.admin.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;
import sun.java2d.pipe.AAShapePipe;

import java.text.SimpleDateFormat;
import java.util.Date;

@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = Logger.getLogger(EchoServerHandler.class.getName());


    /**
     * 通过Event驱动去把控，一有资源立马调用下面的方法
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //传送的消息
        String inMsg = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);
        //将消息记录到控制台
        LOGGER.info("服务器收到的客户端"+ctx.channel()+"的消息是:"+inMsg);

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = null;
        if("QUERY TIME ORDER".equals(inMsg)){
            currentTime = sf.format(new Date(System.currentTimeMillis())).toString();
        }else {
            currentTime = "BAD REQUEST";
        }

        //通过Unpooled 这样一个byteBuf管理池 高效构建byteBuf,静态构造
        ByteBuf responseBuf = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(responseBuf);

    }

    /**
     * Event对最后一次进行把控，如果是最后一次读写，将会通知到下面方法进行严密监
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //清空消息，并且加入监听，传入监听器进行过程结束并且释放资源
       ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("主机"+ctx.channel().remoteAddress()+"出现异常"+cause.getMessage());
        //打印异常
        cause.printStackTrace();
        //关闭channel通道
        ctx.channel();
    }
}

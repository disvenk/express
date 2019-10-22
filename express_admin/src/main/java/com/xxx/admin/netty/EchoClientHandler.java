package com.xxx.admin.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger LOGGER = Logger.getLogger(EchoClientHandler.class.getName());

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        ctx.writeAndFlush(Unpooled.copiedBuffer("QUERY TIME ORDER", CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        String inMsg = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);
        LOGGER.info("客户端收到的服务器"+ctx.channel()+"的消息是:"+inMsg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

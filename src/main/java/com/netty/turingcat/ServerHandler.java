package com.netty.turingcat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * Created by arvin on 2016/12/22.
 */
public class ServerHandler extends SimpleChannelInboundHandler<Head> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Head o) throws Exception {
//        ByteBuf in = (ByteBuf) o;
//        byte[] req = new byte[in.readableBytes()];
//        in.readBytes(req);
//        String body = new String(req,"utf-8");
        System.out.println("收到客户端消息:" + o.str);
        String send = "hello, it is Server test header ping";
        ByteBuf buf = Unpooled.copiedBuffer(send.getBytes());

        for (ChannelHandlerContext c : ServiceMain.list) {
            ChannelFuture f = c.writeAndFlush(o);
            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    System.out.println("发送成功");
                }
            });
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ServiceMain.list.add(ctx);
        System.out.println("list " + ServiceMain.list.size());
        super.channelActive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}

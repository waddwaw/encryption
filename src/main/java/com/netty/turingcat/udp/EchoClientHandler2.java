package com.netty.turingcat.udp;

import java.net.InetSocketAddress;
import java.util.Vector;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import static com.netty.turingcat.udp.EchoClient2.address;

public class EchoClientHandler2 extends SimpleChannelInboundHandler<DatagramPacket>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet)
            throws Exception {
        //服务器推送对方IP和PORT
        ByteBuf buf = (ByteBuf) packet.copy().content();
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String str = new String(req, "UTF-8");
        String[] list = str.split(" ");
        //如果是A 则发送
        if(list[0].equals("A")){
            String ip = list[1];
            String port = list[2];
            System.out.println("----" + ip);
            address = new InetSocketAddress(ip, Integer.parseInt(port));
            ctx.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("打洞信息".getBytes()), address));
            Thread.sleep(1000);
            ctx.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("P2P info..".getBytes()), address));
        }
        System.out.println("接收到的信息:" + str);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端向服务器发送自己的IP和PORT");
        ctx.writeAndFlush(new DatagramPacket(
                Unpooled.copiedBuffer("R".getBytes()),
                new InetSocketAddress("120.27.33.15", 10090)));
        EchoClient2.channel = ctx.channel();
        super.channelActive(ctx);
    }
}

package com.netty.turingcat.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class EchoServer {

    public static Channel channel;
    public static InetSocketAddress addr1 = null;
    public static InetSocketAddress addr2 = null;
    public static void main(String[] args) {
        Bootstrap b = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new EchoServerHandler());

            b.bind(11090).sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
//            group.shutdownGracefully();
        }

        System.out.println("---------------");
        while (true) {
            Scanner scan1 = new Scanner(System.in);
            int i = 0;
            switch (scan1.nextInt()) {
                case 0: {
                    String remot = "A " + addr2.getAddress().toString().replace("/", "")
                            + " " + addr2.getPort();
                    channel.writeAndFlush(new DatagramPacket(
                            Unpooled.copiedBuffer(remot.getBytes()), addr1));
                    //addr2 -> addr1
                    remot = "A " + addr1.getAddress().toString().replace("/", "")
                            + " " + addr1.getPort();
                    channel.writeAndFlush(new DatagramPacket(
                            Unpooled.copiedBuffer(remot.getBytes()), addr2));
                    System.out.println("M 命令");

                    break;
                }

            }
        }
    }
}
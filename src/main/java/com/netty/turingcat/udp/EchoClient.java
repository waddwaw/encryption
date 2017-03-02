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

/**
 * 模拟P2P客户端
 * @author
 *
 */
public class EchoClient{

    public static Channel channel;
    public static InetSocketAddress address;
    public static void main(String[] args) {
        int port = 10092;
        if(args.length != 0){
            port = Integer.parseInt(args[0]);
        }
        Bootstrap b = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new EchoClientHandler());

            b.bind(port).sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
//            group.shutdownGracefully();
        }

        System.out.println("---------------");
        while (true) {
            Scanner scan1 = new Scanner(System.in);
            switch (scan1.nextInt()) {
                case 0: {
                    Scanner scan = new Scanner(System.in);
                    channel.writeAndFlush(new DatagramPacket(
                            Unpooled.copiedBuffer(scan.next().getBytes()),address));
                    break;
                }
            }
        }
    }
}

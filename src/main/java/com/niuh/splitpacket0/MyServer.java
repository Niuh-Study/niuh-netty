package com.niuh.splitpacket0;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;

public class MyServer {

    public static void main(String[] args) throws InterruptedException {
        //创建两个线程组bossGroup和workerGroup, 含有的子线程NioEventLoop的个数默认为cpu核数的两倍
        // bossGroup只是处理连接请求 ,真正的和客户端业务处理，会交给workerGroup完成
        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务器端的启动对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            //使用链式编程来配置参数
            bootstrap.group(boosGroup, workerGroup) //设置两个线程组
                    .channel(NioServerSocketChannel.class) //使用NioServerSocketChannel作为服务器的通道实现
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //解码器需要设置数据的最大长度，我这里设置成1024
                            //ch.pipeline().addLast(new LineBasedFrameDecoder(1024));

                            //数据包最大长度是1024
                            //长度域的起始索引是0
                            //长度域的数据长度是4
                            //矫正值为0，因为长度域只有 有效数据的长度的值
                            //丢弃数据起始值是4，因为长度域长度为4，我要把长度域丢弃，才能得到有效数据
                            //ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));


                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
                            //添加Protobuf解码器，构造器需要指定解码具体的对象实例
                            pipeline.addLast(new ProtobufDecoder(MessagePojo.Message.getDefaultInstance()));

                            pipeline.addLast(new MyServerHandler());
                        }
                    });
            System.out.println("netty server start。。");
            //绑定一个端口并且同步, 生成了一个ChannelFuture异步对象，通过isDone()等方法可以判断异步事件的执行情况
            //启动服务器(并绑定端口)，bind是异步操作，sync方法是等待异步操作执行完毕
            ChannelFuture cf = bootstrap.bind(9000).sync();
            //对通道关闭进行监听，closeFuture是异步操作，监听通道关闭
            // 通过sync方法同步等待通道关闭处理完毕，这里会阻塞等待通道关闭完成
            cf.channel().closeFuture().sync();
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

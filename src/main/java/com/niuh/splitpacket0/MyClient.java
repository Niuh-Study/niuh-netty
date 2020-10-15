package com.niuh.splitpacket0;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.codec.string.LineSeparator;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

public class MyClient {
    public static void main(String[] args) throws InterruptedException {
        //客户端需要一个事件循环组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建客户端启动对象
            //注意客户端使用的不是ServerBootstrap而是Bootstrap
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group) //设置线程组
                    .channel(NioSocketChannel.class) // 使用NioSocketChannel作为客户端的通道实现
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //加入处理器
                            ChannelPipeline pipeline = ch.pipeline();
                            //添加编码器，使用默认的符号\n，字符集是UTF-8
                            //pipeline.addLast(new LineEncoder(LineSeparator.DEFAULT, CharsetUtil.UTF_8));

                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                            //在发送端添加Protobuf编码器
                            pipeline.addLast(new ProtobufEncoder());

                            pipeline.addLast(new MyClientHandler());
                        }
                    });
            System.out.println("netty client start。。");
            //启动客户端去连接服务器端
            ChannelFuture cf = bootstrap.connect("127.0.0.1", 9000).sync();
            //对通道关闭进行监听
            cf.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }
}

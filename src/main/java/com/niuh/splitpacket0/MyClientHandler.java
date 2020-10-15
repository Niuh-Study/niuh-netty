package com.niuh.splitpacket0;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;

import java.nio.charset.Charset;

public class MyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当客户端连接服务器完成就会触发该方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        /*for (int i = 1; i <= 5; i++) {
            String str = "msg No" + i;
            ByteBuf byteBuf = Unpooled.buffer(1024);
            byte[] bytes = str.getBytes(Charset.forName("utf-8"));
            //设置长度域的值，为有效数据的长度
            byteBuf.writeInt(bytes.length);
            //设置有效数据
            byteBuf.writeBytes(bytes);
            ctx.writeAndFlush(byteBuf);
        }*/
        for (int i = 1; i <= 100; i++) {
            //使用的是构建者模式进行创建对象
            MessagePojo.Message message = MessagePojo
                    .Message
                    .newBuilder()
                    .setId(i)
                    .setContent(i + "一角钱，起飞～")
                    .build();
            ctx.writeAndFlush(message);
        }
    }

    //当通道有读取事件时会触发，即服务端发送数据给客户端
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("收到服务端的消息:" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("服务端的地址： " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

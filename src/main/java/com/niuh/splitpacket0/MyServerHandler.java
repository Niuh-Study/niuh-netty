package com.niuh.splitpacket0;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

public class MyServerHandler  extends ChannelInboundHandlerAdapter {

    // count 变量，用于计数
    private int count;

    /**
     * 读取客户端发送的数据
     *
     * @param ctx 上下文对象, 含有通道channel，管道pipeline
     * @param msg 就是客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /*System.out.println("服务器读取线程 " + Thread.currentThread().getName());

        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        // 把ByteBuf的数据读到bytes数组中
        buf.readBytes(bytes);
        String message = new String(bytes, Charset.forName("utf-8"));
        System.out.println("服务器接收到数据：" + message);
        // 打印接收的次数
        System.out.println("接收到的数据量是：" + (++this.count));*/

        MessagePojo.Message messagePojo = (MessagePojo.Message) msg;
        System.out.println("id:" + messagePojo.getId());
        System.out.println("content:" + messagePojo.getContent());
    }

    /**
     * 数据读取完毕处理方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ByteBuf buf = Unpooled.copiedBuffer("HelloClient".getBytes(CharsetUtil.UTF_8));
        ctx.writeAndFlush(buf);
    }

    /**
     * 处理异常, 一般是需要关闭通道
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}

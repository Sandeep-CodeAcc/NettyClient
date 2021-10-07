package com.netty.client.handler;

import java.net.SocketAddress;

import javax.net.ssl.SSLEngine;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.handler.codec.http.DefaultHttpRequest;

public class SSLOutboundHandler extends SslHandler {

    public ChannelHandlerContext context;

    public SSLOutboundHandler(SSLEngine engine) {
        super(engine);
    }
    
    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.bind(ctx, localAddress, promise);
        this.context = ctx;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    @Override
    public void write(final ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        super.read(ctx);
        System.out.println(ctx);
        // String host = "https://v250005-iflmap.avtsbhf.us3.hana.ondemand.com";
        // HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, host);
        // ctx.writeAndFlush(request).addListener(new ChannelFutureListener(){
        //     @Override
        //     public void operationComplete(ChannelFuture future) {
        //         System.out.println("Operation Completed");
        //         if (future.isSuccess()) {
        //             System.out.println("Call Executed Successfully");
        //         }
        //         if (!future.isSuccess()) future.cause().printStackTrace();
        //     }});

    }
}

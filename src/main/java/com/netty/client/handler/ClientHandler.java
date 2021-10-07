package com.netty.client.handler;

import java.nio.BufferOverflowException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class ClientHandler extends SimpleChannelInboundHandler<String> { 

    private ChannelHandlerContext ctx;
    private BlockingQueue<Promise<String>> messageList = new ArrayBlockingQueue<>(16);
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("Exception Caught");
        System.err.println(cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
        System.out.println("InboundHandler: channelActive");
        if (ctx == null) {
            System.out.println("Context is null");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Reading channel");
        System.out.println(msg);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg);
    }
    
    public Future<String> sendMessage(Object message) {
        if(ctx == null) throw new IllegalStateException();
        return sendMessage(message, ctx.executor().newPromise());
    }

    public Future<String> sendMessage(Object message, Promise<String> prom) {
        synchronized(this){
            if(messageList == null) {
                // Connection closed
                prom.setFailure(new IllegalStateException());
            } else if(messageList.offer(prom)) { 
                // Connection open and message accepted
                ctx.writeAndFlush(message).addListener(new ChannelFutureListener(){
                    @Override
                    public void operationComplete(ChannelFuture future) {
                        System.out.println("Operation Completed");
                        if (future.isSuccess()) {
                            System.out.println("Call Executed Successfully");
                        }
                        if (!future.isSuccess()) future.cause().printStackTrace();
                    }});
            } else { 
                // Connection open and message rejected
                prom.setFailure(new BufferOverflowException());
            }
            return prom;
        }
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof SslHandshakeCompletionEvent) {
            SslHandshakeCompletionEvent event = (SslHandshakeCompletionEvent) evt;
            if (!((SslHandshakeCompletionEvent) evt).isSuccess()) {
                System.out.println("Handshake Failed");
                System.out.println(event.cause().fillInStackTrace());
            }
        }
    }
}

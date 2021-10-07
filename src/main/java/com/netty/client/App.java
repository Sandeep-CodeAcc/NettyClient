package com.netty.client;

import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import com.netty.client.data.RequestData;
import com.netty.client.handler.ClientHandler;
import com.netty.client.handler.SSLOutboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * Hello world!
 */
public final class App {

    private static ClientHandler handler; 
    private static SSLOutboundHandler outboundHandler;
    private static Logger log = LoggerFactory.getLogger(App.class);
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        log.info("Starting App");
        runNettyRequest();
    }

    private static void runNettyRequest() {
        String host = "https://v250005-iflmap.avtsbhf.us3.hana.ondemand.com";
        int port = 443;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
 
                @Override
                public void initChannel(SocketChannel ch) 
                  throws Exception {
                    // SSLContext context = SSLContext.getInstance("TLSv1.1");
                    // context.init(null, null, null);
                    // context.init(KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).getKeyManagers(), 
                    //         TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).getTrustManagers(), 
                    //         new SecureRandom());
                    // KeyManagerFactory.init();
                    // SSLEngine engine = context.createSSLEngine();
                    // engine.setUseClientMode(true);
                    // SslHandler sslHandler = new SslHandler(engine);
                    // sslHandler.handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>(){

                    //     @Override
                    //     public void operationComplete(Future<? super Channel> future) throws Exception {
                    //         System.out.println("Handshake Future completed");
                    //     }
                        
                    // });
                    outboundHandler = new SSLOutboundHandler(ssl().newEngine(ByteBufAllocator.DEFAULT));
                    // ChannelFuture handshakeFuture = outboundHandler.handshakeFuture();
                    // handshakeFuture.addListener(new GenericFutureListener<Future<? super Void>>(){
                        
                    // });
                    // if (outboundHandler.handshakeFuture().awaitUninterruptibly(50000)) {
                    //     System.out.println("Handshake Successful");
                    // } else {
                    //     System.out.println("Handshake Failure");
                    // }

                    handler = new ClientHandler();
                    // ch.pipeline().addFirst(outboundHandler);
                    ch.pipeline().addLast(new HttpRequestEncoder(),
                        new HttpResponseDecoder(),outboundHandler,handler);
                    
                }
            });
            // Future<Channel> channelFuture = outboundHandler.handshakeFuture();
            // channelFuture.addListener(new ChannelFutureListener(){
            //    @Override
            //    public void operationComplete(ChannelFuture future) {

            //    } 
            // });
            Channel f = b.connect(host, port).awaitUninterruptibly().channel();

            // if (f.pipeline().get("ssl") instanceof SslHandler) {
            //     SslHandler sslHandlerFromChannel = f.pipeline().get(SslHandler.class);
            // }
            Future<Channel> handshakeFuture = f.pipeline().get(SslHandler.class).handshakeFuture();
            handshakeFuture.addListener(new GenericFutureListener<Future<? super Channel>>(){
                @Override
                public void operationComplete(Future<? super Channel> future) throws Exception {
                    System.out.println("Handshake Complete");
                    // HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, host);
                    // handler.sendMessage(request);
                }
            });
            System.out.println("Channel Writtable: " + f.isWritable());
            // ChannelFuture future = f.write(request);
            // ChannelFuture future = f.write(request);
            // future.addListener(new ChannelFutureListener(){
            //     @Override
            //     public void operationComplete(ChannelFuture future) throws Exception {
            //         System.out.println("Operation Complete in Outbound Channel");
            //         future.channel().writeAndFlush(request);
            //     }
            // });
            // future.await();
            f.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private static SslContext ssl() throws SSLException {
        SslContextBuilder ctxBuilder = SslContextBuilder.forClient().sslProvider(SslProvider.JDK).startTls(true).protocols(new String[]{"TLSv1.1"});
        return ctxBuilder.build();
    }
}

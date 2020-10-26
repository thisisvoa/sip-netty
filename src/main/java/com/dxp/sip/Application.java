package com.dxp.sip;

import com.dxp.sip.bus.fun.DispatchHandlerContext;
import com.dxp.sip.bus.handler.GbLoggingHandler;
import com.dxp.sip.bus.handler.SipRequestHandler;
import com.dxp.sip.bus.handler.SipResponseHandler;
import com.dxp.sip.codec.sip.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;

/**
 * 启动类
 *
 * @author carzy
 * @date 2020/8/10
 */
public class Application {
    private static final int port = 5060;
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(Application.class);
    private static final GbLoggingHandler LOGGING_HANDLER = new GbLoggingHandler(LogLevel.DEBUG);
    private static final EventLoopGroup BOSS_GROUP = new NioEventLoopGroup(2);
    private static final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup(2);
    private static final EventLoopGroup UDP_GROUP = new NioEventLoopGroup(2);

    private void startUdp() {
        Bootstrap b = new Bootstrap();

        b.group(UDP_GROUP)
                .channel(NioDatagramChannel.class) // 关闭广播
                .option(ChannelOption.SO_BROADCAST, false)
                .handler(new ChannelInitializer<NioDatagramChannel>() {

                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new AbstractSipResponseEncoder())
                                .addLast(new AbstractSipRequestEncoder())
                                .addLast(new SipObjectUdpDecoder())
                                .addLast(new SipObjectAggregator(8192))
                                .addLast(LOGGING_HANDLER)
                                .addLast(new SipRequestHandler())
                                .addLast(new SipResponseHandler());
                    }
                });
        try {
            ChannelFuture future = b.bind(port).sync();
            LOGGER.info("udp port {}   is running.", port);
            future.channel().closeFuture().addListener(future1 -> {

                if (future1.isSuccess()) {
                    LOGGER.info("udp exit suc on port  {}", port);
                } else {
                    LOGGER.error("udp exit err on port {}", port, future1.cause());
                }
            });
        } catch (Exception e) {
            LOGGER.error("udp run port $port err", e);
            UDP_GROUP.shutdownGracefully();
        }

        try {
            DispatchHandlerContext.getInstance().init();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void startTcp() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(BOSS_GROUP, WORKER_GROUP)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 512)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline()
                                .addLast(new AbstractSipResponseEncoder())
                                .addLast(new AbstractSipRequestEncoder())
                                .addLast(new SipObjectTcpDecoder())
                                .addLast(new SipObjectAggregator(8192))
                                .addLast(LOGGING_HANDLER)
                                .addLast(new SipRequestHandler())
                                .addLast(new SipResponseHandler());
                    }
                });
        try {
            ChannelFuture future = b.bind(port).sync();
            LOGGER.info("tcp port $port is running.");
            future.channel().closeFuture().addListener(future1 -> {
                if (future1.isSuccess()) {
                    LOGGER.info("tcp exit suc on port $port");
                } else {
                    LOGGER.error("tcp exit err on port $port", future1.cause());
                }
            });
        } catch (Exception e) {
            LOGGER.error("tcp run port $port err", e);
            BOSS_GROUP.shutdownGracefully();
            WORKER_GROUP.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        Application application = new Application();
        application.startUdp();
    }

}
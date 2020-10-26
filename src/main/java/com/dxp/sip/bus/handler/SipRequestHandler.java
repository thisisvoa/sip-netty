package com.dxp.sip.bus.handler;

import com.dxp.sip.bus.fun.DispatchHandler;
import com.dxp.sip.codec.sip.AbstractSipHeaders;
import com.dxp.sip.codec.sip.FullSipRequest;
import com.dxp.sip.codec.sip.SipMessageUtil;
import com.dxp.sip.codec.sip.SipMethod;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * 处理sip请求.
 *
 *
 * 该对象必须单例使用.
 *
 * @author carzy
 * @date 2020/8/11
 */
public final class SipRequestHandler extends SimpleChannelInboundHandler <FullSipRequest>  {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(SipRequestHandler.class);

protected void channelRead0(  ChannelHandlerContext ctx,   FullSipRequest msg) throws Exception {
    AbstractSipHeaders headers = msg.headers();
    Channel channel = ctx.channel();

        // 启动的时候已经声明了. TCP为NioSocketChannel, UDP为NioDatagramChannel
        if (channel instanceof NioDatagramChannel) {
            LOGGER.info("[{}{}] rec udp request msg", channel.id().asShortText(), msg.recipient().toString());
        } else {
            LOGGER.info("[{}{}] rec tcp request msg", channel.id().asShortText(), msg.recipient().toString());
        }
        if (SipMethod.BAD == msg.method()) {
            LOGGER.error("收到一个错误的SIP消息");
            StringBuilder builder = new StringBuilder();
            LOGGER.error(SipMessageUtil.appendFullRequest(builder, msg).toString());
        }

        // 异步执行
        DispatchHandler.instance().handler(msg, ctx.channel());
    }


}
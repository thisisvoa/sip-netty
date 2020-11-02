package com.dxp.sip.bus.handler;

import com.dxp.sip.codec.sip.*;
import com.dxp.sip.conference.DefaultSessionRegister;
import com.dxp.sip.conference.SipSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理sip请求
 *
 * @author carzy
 * @date 2020/8/11
 */
public final class SipResponseHandler extends SimpleChannelInboundHandler<FullSipResponse> {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(SipResponseHandler.class);



    protected void channelRead0(ChannelHandlerContext ctx, FullSipResponse msg) throws Exception {
        Channel channel = ctx.channel();

        // 启动的时候已经声明了. TCP为NioSocketChannel, UDP为NioDatagramChannel
        if (channel instanceof NioDatagramChannel) {
            LOGGER.info("[{}-{}] rec udp response msg", channel.id().asShortText(), msg.recipient().toString());

            DefaultSessionRegister defaultSessionRegister = DefaultSessionRegister.getInstance();
            SipObjectAggregator.AbstractAggregatedFullSipResponse response= (SipObjectAggregator.AbstractAggregatedFullSipResponse) msg;
            AbstractSipHeaders headers = response.headers();


            String str = headers.get(SipHeaderNames.FROM);
            String toURI = replaceStr(str.split(";")[0]);
            SipSession sipSession = defaultSessionRegister.getSession(toURI);
         //   Channel channel1 = sipSession.getCtx();
           // channel1.writeAndFlush(msg);

        } else {
            LOGGER.info("[{}-{}] rec tcp response msg", channel.id().asShortText(), msg.recipient().toString());
        }
    }

    private String replaceStr(String str){
        Pattern pattern = Pattern.compile("[<>]+");
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("");
    }

}
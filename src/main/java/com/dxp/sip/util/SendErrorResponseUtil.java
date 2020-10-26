package com.dxp.sip.util;

import com.dxp.sip.bus.fun.DispatchHandlerContext;
import com.dxp.sip.codec.sip.*;
import io.netty.channel.Channel;
import kotlin.jvm.JvmStatic;

/**
 * @author carzy
 * @date 2020/8/17
 */
public final class SendErrorResponseUtil  {
    public static final void err400(FullSipRequest request, Channel channel, String reason) {
        error(request, SipResponseStatus.BAD_REQUEST, channel, reason);
    }

    public static final void err500( FullSipRequest request,  Channel channel,  String reason) {
        error(request, SipResponseStatus.INTERNAL_SERVER_ERROR, channel, reason);
    }

    public static final void err405( FullSipRequest request,  Channel channel) {
        error(request, SipResponseStatus.METHOD_NOT_ALLOWED, channel, request.method().asciiName().toString() + " not allowed");
    }
    public static final void err404( FullSipRequest request,  Channel channel,String reason) {
        error(request, SipResponseStatus.NOT_FOUND, channel, request.method().asciiName().toString() + " not found");
    }
    /**
     * @param request 请求
     * @param channel 通道
     * @param reason  错误信息.
     */
    private  static final void error(FullSipRequest request, SipResponseStatus status, Channel channel, String reason) {
        AbstractSipHeaders headers = request.headers();
        DefaultFullSipResponse response = new DefaultFullSipResponse(status);
        response.setRecipient(request.recipient());
        AbstractSipHeaders h = response.headers();
        h.set(SipHeaderNames.FROM, headers.get(SipHeaderNames.FROM))
                .set(SipHeaderNames.TO, headers.get(SipHeaderNames.TO))
                .set(SipHeaderNames.ALLOW, DispatchHandlerContext.allowMethod())
                .set(SipHeaderNames.CONTENT_LENGTH, headers.get(SipHeaderNames.CONTENT_LENGTH))
                .set(SipHeaderNames.CSEQ, headers.get(SipHeaderNames.CSEQ))
                .set(SipHeaderNames.CALL_ID, headers.get(SipHeaderNames.CALL_ID))
                .set(SipHeaderNames.REASON, reason)
        .set(SipHeaderNames.USER_AGENT,SipHeaderValues.USER_AGENT);

        channel.writeAndFlush(response);
    }
}
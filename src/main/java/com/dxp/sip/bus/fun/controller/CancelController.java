package com.dxp.sip.bus.fun.controller;

import com.dxp.sip.bus.fun.AbstractMsgProcessor;
import com.dxp.sip.bus.fun.HandlerController;
import com.dxp.sip.codec.sip.*;
import com.dxp.sip.util.CharsetUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.dom4j.DocumentException;

/**
 * @author zhangjin
 * When the user who is calling, ends the call then a CANCEL request is sent to the server.
 * The server forwards the CANCEL request to the receiver.
 * Receiver sends an 200 OK and a 487 REQUEST TERMINATED response.
 * The session is then cancelled successfully.
 * @date 2020/8/14
 */
public class CancelController extends AbstractMsgProcessor {
        private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(CancelController.class);
        public SipMethod method() {
        return SipMethod.CANCEL;
    }

        /**
         * 处理具体的 sip 请求
         *
         * @param request 请求携带的信息.
         * @param channel 通道
         * @throws DocumentException 解析XML失败.
         */
        @Override
        public void handler(FullSipRequest request, ChannelHandlerContext channel) throws DocumentException {
            AbstractSipHeaders headers = request.headers();
            String type = headers.get(SipHeaderNames.CONTENT_TYPE);

                LOGGER.info("cancel: {}", request.content().toString(CharsetUtils.US_ASCII));
                DefaultFullSipResponse response =new  DefaultFullSipResponse(SipResponseStatus.OK);
                response.setRecipient(request.recipient());
                AbstractSipHeaders h = response.headers();
                h.set(SipHeaderNames.VIA,headers.get(SipHeaderNames.VIA));
                h.set(SipHeaderNames.FROM, headers.get(SipHeaderNames.FROM))
                        .set(SipHeaderNames.TO, headers.get(SipHeaderNames.TO) + ";tag=" + System.currentTimeMillis())
                        .set(SipHeaderNames.CSEQ, headers.get(SipHeaderNames.CSEQ))
                        .set(SipHeaderNames.CALL_ID, headers.get(SipHeaderNames.CALL_ID))
                        .set(SipHeaderNames.CONTACT, "")
                        .set(SipHeaderNames.CONTENT_LENGTH, SipHeaderValues.EMPTY_CONTENT_LENGTH);

                channel.channel().writeAndFlush(response);



        }




}
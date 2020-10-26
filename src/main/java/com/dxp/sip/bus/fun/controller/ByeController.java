package com.dxp.sip.bus.fun.controller;

import com.dxp.sip.bus.fun.AbstractMsgProcessor;
import com.dxp.sip.bus.fun.HandlerController;
import com.dxp.sip.codec.sip.*;
import com.dxp.sip.util.CharsetUtils;
import com.dxp.sip.util.SendErrorResponseUtil;
import io.netty.channel.Channel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.dom4j.DocumentException;

/**
 * @author zhangjin
 * @date 2020/8/14
 */
public class ByeController extends AbstractMsgProcessor {
        private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ByeController.class);
        public SipMethod method() {
        return SipMethod.BYE;
    }

        /**
         * 处理具体的 sip 请求
         *
         * @param request 请求携带的信息.
         * @param channel 通道
         * @throws DocumentException 解析XML失败.
         */
        @Override
        public void handler(FullSipRequest request, Channel channel) throws DocumentException {
            AbstractSipHeaders headers = request.headers();
            String type = headers.get(SipHeaderNames.CONTENT_TYPE);

                LOGGER.info("bye: {}", request.content().toString(CharsetUtils.US_ASCII));
                DefaultFullSipResponse response =new  DefaultFullSipResponse(SipResponseStatus.OK);
                response.setRecipient(request.recipient());
                AbstractSipHeaders h = response.headers();
                h.set(SipHeaderNames.VIA,headers.get(SipHeaderNames.VIA));
                h.set(SipHeaderNames.FROM, headers.get(SipHeaderNames.FROM))
                        .set(SipHeaderNames.TO, headers.get(SipHeaderNames.TO) + ";tag=" + System.currentTimeMillis())
                        .set(SipHeaderNames.CSEQ, headers.get(SipHeaderNames.CSEQ))
                        .set(SipHeaderNames.CALL_ID, headers.get(SipHeaderNames.CALL_ID))
                        .set(SipHeaderNames.MAX_FORWARDS, SipHeaderValues.USER_AGENT)
                        .set(SipHeaderNames.USER_AGENT, SipHeaderValues.USER_AGENT)
                        .set(SipHeaderNames.CONTENT_LENGTH, SipHeaderValues.EMPTY_CONTENT_LENGTH);

                channel.writeAndFlush(response);




        }




}
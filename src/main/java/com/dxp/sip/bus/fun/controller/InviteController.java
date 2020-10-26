package com.dxp.sip.bus.fun.controller;

import com.dxp.sip.bus.fun.AbstractMsgProcessor;
import com.dxp.sip.bus.fun.DispatchHandlerContext;
import com.dxp.sip.codec.sip.*;
import com.dxp.sip.conference.SipContactAOR;
import com.dxp.sip.util.SendErrorResponseUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.dom4j.DocumentException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author carzy
 * @date 2020/8/14
 */
public class InviteController extends AbstractMsgProcessor {

        private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(InviteController.class);



    public SipMethod method() {
        return SipMethod.INVITE;
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

            String toURI = replaceStr(headers.get(SipHeaderNames.TO));

            LOGGER.info("invite toURI : {}", toURI);

            SipContactAOR contactAOR = DispatchHandlerContext.getInstance().getContactMap().get(toURI);
            /* no such user, reply not-found */
            if (null == contactAOR) {
                SendErrorResponseUtil.err404(request, channel.channel(), "not found!");
                return;
            }
            /*send try to the invitor */
            else {
                DefaultFullSipResponse responseTrying = new DefaultFullSipResponse(SipResponseStatus.TRYING);
                responseTrying.setRecipient(request.recipient());
                AbstractSipHeaders h_try = responseTrying.headers();
                h_try.set(SipHeaderNames.VIA, headers.get(SipHeaderNames.VIA));
                String s = "<" + contactAOR + ">";
                h_try.set(SipHeaderNames.FROM, headers.get(SipHeaderNames.FROM))
                        .set(SipHeaderNames.TO, s + ";tag=" + System.currentTimeMillis())
                        .set(SipHeaderNames.CSEQ, headers.get(SipHeaderNames.CSEQ))
                        .set(SipHeaderNames.CALL_ID, headers.get(SipHeaderNames.CALL_ID))
                        .set(SipHeaderNames.USER_AGENT, SipHeaderValues.USER_AGENT)
                        .set(SipHeaderNames.CONTENT_LENGTH, SipHeaderValues.EMPTY_CONTENT_LENGTH);

                channel.writeAndFlush(responseTrying);



            }

            /* forward it to the invitee */
            request.setUri(contactAOR.toString());
            channel.write(request);
        }

    private String replaceStr(String str){
        Pattern pattern = Pattern.compile("[<>]+");
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("");
    }




}
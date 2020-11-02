package com.dxp.sip.bus.fun.controller;

import com.dxp.sip.bus.fun.AbstractMsgProcessor;
import com.dxp.sip.bus.fun.DispatchHandlerContext;
import com.dxp.sip.codec.sip.*;
import com.dxp.sip.conference.DefaultSessionRegister;
import com.dxp.sip.conference.SipContactAOR;
import com.dxp.sip.conference.SipSession;
import com.dxp.sip.util.SendErrorResponseUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.DocumentException;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
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

            String to_str = headers.get(SipHeaderNames.TO);
            String toURI = replaceStr(to_str);

            LOGGER.info("invite toURI : {}", toURI);

            DefaultSessionRegister defaultSessionRegister = DefaultSessionRegister.getInstance();
            String username_str = getStr(toURI);
            SipSession sipSession = defaultSessionRegister.getSession(username_str);
            /* no such user, reply not-found */
            if (null == sipSession) {
                SendErrorResponseUtil.err404(request, channel.channel(), "not found!");
                return;
            }
            /*send try to the invitor  */
            else {
                DefaultFullSipResponse responseTrying = new DefaultFullSipResponse(SipResponseStatus.TRYING);
                responseTrying.setRecipient(request.recipient());
                AbstractSipHeaders h_try = responseTrying.headers();
                h_try.set(SipHeaderNames.VIA, headers.get(SipHeaderNames.VIA));
                String s = "<" + toURI + ">";
                h_try.set(SipHeaderNames.FROM, headers.get(SipHeaderNames.FROM))
                        .set(SipHeaderNames.TO, s + ";tag=" + System.currentTimeMillis())
                        .set(SipHeaderNames.CSEQ, headers.get(SipHeaderNames.CSEQ))
                        .set(SipHeaderNames.CALL_ID, headers.get(SipHeaderNames.CALL_ID))
                        .set(SipHeaderNames.USER_AGENT, SipHeaderValues.USER_AGENT)
                        .set(SipHeaderNames.CONTENT_LENGTH, SipHeaderValues.EMPTY_CONTENT_LENGTH);

                channel.writeAndFlush(responseTrying);
            }
            /* forward it to the invitee */

            SipSession _sipSession = defaultSessionRegister.getSession(username_str);

           // request.setUri(contactAOR.toString());


            SipRequest inviteRequest = new DefaultFullSipRequest(SipVersion.SIP_2_0, SipMethod.INVITE, "");

            try {
                BeanUtils.copyProperties(inviteRequest,request);
                request.setRecipient(sipSession.getDeviceAddress());
                inviteRequest.setRecipient(sipSession.getDeviceAddress());
                sipSession.getCtx().writeAndFlush(request);
             //   sipSession.getCtx().writeAndFlush(inviteRequest);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }



            //判断是B返回的请求
            Boolean isRetFromBClient = Boolean.FALSE;



        }

    private String replaceStr(String str){
        Pattern pattern = Pattern.compile("[<>]+");
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("");
    }

    private String getStr(String str){
        Pattern pattern = Pattern.compile(":(.*?)@");
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()){
            return matcher.group(1);
        }
        return "";
    }


}
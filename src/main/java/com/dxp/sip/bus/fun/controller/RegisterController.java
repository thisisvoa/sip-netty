package com.dxp.sip.bus.fun.controller;

import com.dxp.sip.bus.fun.AbstractMsgProcessor;
import com.dxp.sip.bus.fun.DispatchHandlerContext;
import com.dxp.sip.codec.sip.*;
import com.dxp.sip.conference.*;
import com.dxp.sip.util.CharsetUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.dom4j.DocumentException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author carzy
 * @date 2020/8/14
 */
public final class RegisterController extends AbstractMsgProcessor {


    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(RegisterController.class);



    @Override
    public SipMethod method() {
        return SipMethod.REGISTER;
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
        DefaultFullSipResponse response =new  DefaultFullSipResponse(SipResponseStatus.UNAUTHORIZED);
        response.setRecipient(request.recipient());
        AbstractSipHeaders h = response.headers();
       /* if (!headers.contains(SipHeaderNames.AUTHORIZATION)) {
            String wwwAuth = "Digest realm=\"31011000002001234567\", nonce=\"" +
                    "b700dc7cb094478503a21148184a3731\", opaque=\"5b279c2efd18d123d1f4a2182527a281\", algorithm=MD5";
            h.set(SipHeaderNames.VIA,headers.get(SipHeaderNames.VIA));
            h.set(SipHeaderNames.FROM, headers.get(SipHeaderNames.FROM))
                    .set(SipHeaderNames.TO, headers.get(SipHeaderNames.TO))
                    .set(SipHeaderNames.CSEQ, headers.get(SipHeaderNames.CSEQ))
                    .set(SipHeaderNames.CALL_ID, headers.get(SipHeaderNames.CALL_ID))
                    .set(SipHeaderNames.USER_AGENT, SipHeaderValues.USER_AGENT)
                    .set(SipHeaderNames.WWW_AUTHENTICATE, wwwAuth)
                    .set(SipHeaderNames.CONTENT_LENGTH, SipHeaderValues.EMPTY_CONTENT_LENGTH);
        } else {

*/

            //1. 先查询expires是否过期，过期删除联系人
            SipMessage sipMessage = request;
            String toURI = replaceStr(headers.get(SipHeaderNames.TO));
            ConcurrentHashMap<String, SipContactAOR> contactMap = DispatchHandlerContext.getInstance().getContactMap();
            String expires = headers.get(SipHeaderNames.EXPIRES);
            int _expires=-1;
            if(null != expires){
                _expires= Integer.valueOf(expires);


            }
            if(_expires<=0){
                String key = sipMessage.recipient().toString();
                contactMap.remove(key);
            }
            String contactURI =replaceStr(sipMessage.headers().get(SipHeaderNames.CONTACT)).split(";")[0];
            SipContactAOR contactAOR = new SipContactAOR(contactURI);
            contactAOR.attachTo(new SipAOR(toURI));

            SipSession.DefaultSessionBuilder defaultSessionBuilder=new SipSession.DefaultSessionBuilder();
            defaultSessionBuilder.build();
            defaultSessionBuilder.sipContactAOR = contactAOR;
            defaultSessionBuilder.status(Session.Status.CONNECTED);
            defaultSessionBuilder.ctx(channel);
            SipSession sipSession =new SipSession(defaultSessionBuilder);
            DefaultSessionRegister defaultSessionRegister=new DefaultSessionRegister();
            defaultSessionRegister.put(toURI, sipSession);
            //2.根据sipaor查询用户名密码去数据库查询
            /* if the user name exists, reply ambiguous */



            //3.设置to到map中
            contactMap.put(toURI,contactAOR);

        ok_method(headers, response, h);
        LOGGER.info("Response Sent: {}", response.content().toString(CharsetUtils.US_ASCII));

        channel.writeAndFlush(response);
    }

    private void ok_method(AbstractSipHeaders headers, DefaultFullSipResponse response, AbstractSipHeaders h) {
        h.set(SipHeaderNames.VIA,headers.get(SipHeaderNames.VIA));
        h.set(SipHeaderNames.FROM, headers.get(SipHeaderNames.FROM))
                .set(SipHeaderNames.TO, headers.get(SipHeaderNames.TO) + ";tag=" + System.currentTimeMillis())
                .set(SipHeaderNames.CSEQ, headers.get(SipHeaderNames.CSEQ))
                .set(SipHeaderNames.CALL_ID, headers.get(SipHeaderNames.CALL_ID))
                .set(SipHeaderNames.USER_AGENT, SipHeaderValues.USER_AGENT)
                .set(SipHeaderNames.CONTENT_LENGTH, SipHeaderValues.EMPTY_CONTENT_LENGTH);
        response.setStatus(SipResponseStatus.OK);
    }


    private String replaceStr(String str){
        Pattern pattern = Pattern.compile("[<>]+");
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("");
    }

    public static void main(String[] args) {
        RegisterController registerController=new RegisterController();
        String  str=registerController.replaceStr("<sip:232@192.168.30.222:5060>");
        System.out.println(str);
    }
}
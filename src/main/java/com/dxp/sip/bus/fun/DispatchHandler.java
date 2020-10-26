package com.dxp.sip.bus.fun;

import com.dxp.sip.codec.sip.FullSipRequest;
import com.dxp.sip.codec.sip.SipMethod;
import com.dxp.sip.util.SendErrorResponseUtil;
import io.netty.channel.Channel;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.dom4j.DocumentException;

/**
 * @author carzy
 * @date 2020/8/14
 */
public final class DispatchHandler {
    // 指向自己实例的私有静态引用，主动创建
    private static DispatchHandler dispatchHandler = new DispatchHandler();

    /**
     * 异步执行处理函数， 不阻塞work线程。
     */
    private EventLoopGroup   loopGroup = new DefaultEventLoopGroup(new DefaultThreadFactory("dis-han"));

    public final void handler(FullSipRequest request, Channel channel) {
        request.retain();
        loopGroup.submit(new Runnable() {
            @Override
            public void run() {
                handler0(request, channel);
            }
        }) ;
    }

    private final void handler0(FullSipRequest request, Channel channel) {
        try {
            SipMethod method = request.method();

            HandlerController controller = DispatchHandlerContext.getInstance().method(method);
            if (controller == null) {
                SendErrorResponseUtil.err405(request, channel);
            } else {
                controller.handler(request, channel);
            }
        } catch (  DocumentException e) {
            SendErrorResponseUtil.err400(request, channel, "xml err");
        } catch ( Exception e) {
            SendErrorResponseUtil.err500(request, channel, e.getMessage());
        } finally {
            request.release();
        }
    }
    // 以自己实例为返回值的静态的公有方法，静态工厂方法
    public static DispatchHandler instance(){
        return dispatchHandler;
    }



}
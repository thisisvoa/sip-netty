package com.dxp.sip.conference;

import com.dxp.sip.codec.sip.FullSipRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public interface Session {

    /**
     * session status types
     */
    enum Status
    {
        NOT_CONNECTED, CONNECTING, CONNECTED, CLOSED
    }

    Object getId();

    void setId(Object id);

    void setAttribute(String key, Object value);

    Object getAttribute(String key);

    void removeAttribute(String key);

    void onEvent(FullSipRequest event);

    void close();

    boolean isConnected();

    void setCtx(Channel ctx);

    Channel getCtx();

}

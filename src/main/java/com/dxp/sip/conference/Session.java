package com.dxp.sip.conference;

import com.dxp.sip.codec.sip.FullSipRequest;

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

}

package com.dxp.sip.bus.transaction;

import com.dxp.sip.codec.sip.SipMessage;

public interface Transaction {

    String getTransport();

    String getHost();

    int getPort();

    SipMessage getSipMessage();

    String getBranch();

    long getCSeq();

    String getViaHost();

    int getViaPort();

}

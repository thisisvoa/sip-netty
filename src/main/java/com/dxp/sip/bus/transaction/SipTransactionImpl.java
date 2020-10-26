package com.dxp.sip.bus.transaction;

import com.dxp.sip.codec.sip.SipMessage;

public class SipTransactionImpl implements Transaction {



    @Override
    public String getTransport() {
        return null;
    }

    @Override
    public String getHost() {
        return null;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public SipMessage getSipMessage() {
        return null;
    }

    @Override
    public String getBranch() {
        return null;
    }

    @Override
    public long getCSeq() {
        return 0;
    }

    @Override
    public String getViaHost() {
        return null;
    }

    @Override
    public int getViaPort() {
        return 0;
    }
}

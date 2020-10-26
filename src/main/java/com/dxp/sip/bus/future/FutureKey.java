package com.dxp.sip.bus.future;

import java.util.Objects;

public class FutureKey {
    private  String toURI;  //sip:096@192.168.30.183

    private long cSeq; //编号


    public FutureKey(String toURI, long cSeq) {
        this.toURI = toURI;
        this.cSeq = cSeq;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FutureKey futureKey = (FutureKey) o;
        return cSeq == futureKey.cSeq &&
                Objects.equals(toURI, futureKey.toURI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toURI, cSeq);
    }

    public String getToURI() {
        return toURI;
    }

    public void setToURI(String toURI) {
        this.toURI = toURI;
    }

    public long getcSeq() {
        return cSeq;
    }

    public void setcSeq(long cSeq) {
        this.cSeq = cSeq;
    }
}

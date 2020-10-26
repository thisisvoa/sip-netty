package com.dxp.sip.conference;

public interface ISessionRegisterListener<T> {

    void regNotify(T t);

    void unRegNotify(T t);
}

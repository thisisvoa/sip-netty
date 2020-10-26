package com.dxp.sip.callback;

public interface IDeviceCallback {

    void deviceRegistered(String uri, Integer dynamicId);

    void deviceUnRegistered(String uri, Integer dynamicId);

}

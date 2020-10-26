package com.dxp.sip.util;

public interface ConstantPool {
    int RESEND_TIME = 1500;
    interface Ret {
        byte SUCCESS = 0x00;                                //成功
        byte FAILED = 0x01;                                 //失败
                         //消息格式错误
        byte TIMEOUT = 0x1d;                                //超时

    }
}

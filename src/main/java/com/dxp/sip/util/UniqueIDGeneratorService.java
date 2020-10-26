package com.dxp.sip.util;

public interface UniqueIDGeneratorService {

    public Object generate();

    public Object generateFor(@SuppressWarnings("rawtypes") Class klass);

}

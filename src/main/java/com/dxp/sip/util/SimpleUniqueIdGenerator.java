package com.dxp.sip.util;

import java.util.concurrent.atomic.AtomicLong;

public class SimpleUniqueIdGenerator implements UniqueIDGeneratorService {

    public static final AtomicLong ID = new AtomicLong(0l);

    @Override
    public Object generate()
    {
        String nodeName = System.getProperty("SIP-SERVER");
        if (null == nodeName || "".equals(nodeName))
        {
            return ID.incrementAndGet();
        }
        else
        {
            return nodeName + ID.incrementAndGet();
        }
    }

    @Override
    public Object generateFor(@SuppressWarnings("rawtypes") Class klass)
    {
        return klass.getSimpleName() + ID.incrementAndGet();
    }

    public static void main(String[] args) {
        SimpleUniqueIdGenerator  simpleUniqueIdGenerator=new SimpleUniqueIdGenerator();

       for(int i=0 ;i<1000;i++){

           Long str = (Long) simpleUniqueIdGenerator.generate();

           System.out.println(str);
       }
    }

}

package com.dxp.sip.conference;

import com.dxp.sip.bus.fun.DispatchHandlerContext;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DefaultSessionRegister {

    private static DefaultSessionRegister instance;
    private final int clean_delay_time = 30 * 1000;

    private ScheduledExecutorService scheduledExecutorService;

    private final Map<String, SipSession> defaultSessionMap;

    private final Map<Integer, String> dynamic_id_map;

    private final List<ISessionRegisterListener<String>> listeners;

    public DefaultSessionRegister(){
        defaultSessionMap = new ConcurrentHashMap<>(1024);
        dynamic_id_map = new ConcurrentHashMap<>(1024);
        listeners = new ArrayList<>();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new CleanJob(), clean_delay_time, clean_delay_time, TimeUnit.MILLISECONDS);
    }


    public SipSession getSession(String mac)
    {
        if (StringUtils.isEmpty(mac))
            return null;

        return defaultSessionMap.get(mac);
    }
    public Integer put(String uri, SipSession session)
    {
        defaultSessionMap.put(uri,session);
        if (  listeners.size() > 0)
        {
            for (ISessionRegisterListener listener : listeners)
                listener.regNotify(uri);
        }
        return Integer.valueOf(session.getId().toString());
    }
    /**
     * 根据ID获取URI sip:006@192.168.30.3
     * @param dynamicId
     * @return
     */
    public String getURI(Integer dynamicId)
    {
        if (dynamicId == null)
            return null;

        return dynamic_id_map.get(dynamicId);
    }
    public void remove(String uri){

        if ("" == uri)
            return;

        SipSession session = defaultSessionMap.remove(uri);
        if (session == null)
            return;
        if (listeners.size() > 0)
        {
            for (ISessionRegisterListener listener : listeners)
                listener.unRegNotify(uri);
        }

    }
    class CleanJob implements Runnable
    {
        @Override
        public void run()
        {
            long currentTime = System.currentTimeMillis();
            SipSession session;
            for (Map.Entry<String, SipSession> entry : defaultSessionMap.entrySet())
            {
                session = entry.getValue();
                if (session.getExpireTime() < currentTime)
                    remove("mac");
            }


        }
    }

    public static DefaultSessionRegister getInstance() {

//先检查实例是否存在，如果不存在才进入下面的同步块

        if (instance == null) {

//同步块，线程安全的创建实例

            synchronized (DispatchHandlerContext.class) {

//再次检查实例是否存在，如果不存在才真的创建实例

                if (instance == null) {

                    instance = new DefaultSessionRegister();

                }

            }

        }

        return instance;
    }
}

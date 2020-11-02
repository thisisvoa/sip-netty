package com.dxp.sip.bus.fun;

import com.dxp.sip.codec.sip.SipMethod;
import com.dxp.sip.conference.SipContactAOR;
import com.dxp.sip.util.ClassScanner;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * gb-sip 请求分发
 *
 * @author carzy
 * @date 2020/8/14
 */
public class DispatchHandlerContext {

    private volatile static DispatchHandlerContext instance = null;

    private static final ConcurrentHashMap<SipMethod, HandlerController> CONTROLLER_MAP = new ConcurrentHashMap(256);
    private static String ALLOW_METHOD = "";


    private DispatchHandlerContext() {


    }

    public final static HandlerController method(SipMethod method) {
        return CONTROLLER_MAP.get(method);
    }

    public static String allowMethod() {
        return ALLOW_METHOD;
    }

    {
    }


    public static final void init() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Set<Class> classes = ClassScanner.doScanAllClasses("com.dxp.sip.bus.fun.controller");
        for (Class aClass : classes) {
            if (HandlerController.class.isAssignableFrom(aClass)) {
                addHandlerController((HandlerController) newClass(aClass));
            }
        }
        ALLOW_METHOD = CONTROLLER_MAP.keySet().stream().map(SipMethod::name).collect(Collectors.joining(","));
    }

    private static final void addHandlerController(HandlerController o) {
        Boolean bb = !CONTROLLER_MAP.containsKey(o.method());
        if (!bb) {
            throw new IllegalArgumentException("handlerController has be created.");
        } else {
            CONTROLLER_MAP.put(o.method(), o);
        }

    }

    private static final Object newClass(Class tClass) throws IllegalAccessException, InstantiationException {


        return tClass.newInstance();
    }


    public static DispatchHandlerContext getInstance() {

//先检查实例是否存在，如果不存在才进入下面的同步块

        if (instance == null) {

//同步块，线程安全的创建实例

            synchronized (DispatchHandlerContext.class) {

//再次检查实例是否存在，如果不存在才真的创建实例

                if (instance == null) {

                    instance = new DispatchHandlerContext();

                }

            }

        }

        return instance;
    }

   }
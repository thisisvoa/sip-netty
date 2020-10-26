package com.dxp.sip.conference;

import com.dxp.sip.bus.fun.DispatchHandler;
import com.dxp.sip.codec.sip.FullSipRequest;
import com.dxp.sip.util.SimpleUniqueIdGenerator;
import com.dxp.sip.util.UniqueIDGeneratorService;

import java.util.HashMap;
import java.util.Map;

public class DefaultSession implements Session {

    public SipContactAOR sipContactAOR;

    protected final Object id;

    protected final long creationTime;

    private volatile long expireTime;

    protected Status status;

    protected Map<String, Object> sessionAttributes = null;

    protected  DispatchHandler eventDispatcher;

    public DefaultSession(DefaultSessionBuilder sessionBuilder) {
        sessionBuilder.validateAndSetValues();
        this.id= sessionBuilder.id;
        this.sipContactAOR = sessionBuilder.sipContactAOR;
        this.creationTime = sessionBuilder.creationTime;
        this.status = sessionBuilder.status;
        this.sessionAttributes = sessionBuilder.sessionAttributes;
    }



    public  static  class DefaultSessionBuilder {

        protected static final UniqueIDGeneratorService ID_GENERATOR_SERVICE = new SimpleUniqueIdGenerator();
        protected Object id = null;
        protected DispatchHandler dispatchHandler =null;

        protected Map<String, Object> sessionAttributes = null;

        public SipContactAOR sipContactAOR;

        protected long creationTime = 0L;

        protected Status status;

        protected  DispatchHandler eventDispatcher;


        public DefaultSession build()
        {
            return new DefaultSession(this);
        }


        protected void validateAndSetValues() {
            if (null == id)
            {
                id = String.valueOf(ID_GENERATOR_SERVICE.generate());
            }
            if (null == eventDispatcher)
            {
                eventDispatcher = DispatchHandler.instance();
            }
            if(null == sessionAttributes)
            {
                sessionAttributes = new HashMap<String, Object>();
            }
            creationTime = System.currentTimeMillis();
        }

        public static UniqueIDGeneratorService getIdGeneratorService() {
            return ID_GENERATOR_SERVICE;
        }

        public Object getId()
        {
            return id;
        }
        public DefaultSessionBuilder id(final String id)
        {
            this.id = id;
            return this;
        }

        public DefaultSessionBuilder sessionAttributes(final Map<String, Object> sessionAttributes)
        {
            this.sessionAttributes = sessionAttributes;
            return this;
        }
        public DefaultSessionBuilder creationTime(long creationTime)
        {
            this.creationTime = creationTime;
            return this;
        }
        public DefaultSessionBuilder eventDispatcher(final DispatchHandler eventDispatcher)
        {
            this.eventDispatcher = eventDispatcher;
            return this;
        }
        public DefaultSessionBuilder status(Status status)
        {
            this.status = status;
            return this;
        }

    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        throw new IllegalArgumentException("id cannot be set in this implementation, since it is final");
    }

    @Override
    public void setAttribute(String key, Object value) {

    }

    @Override
    public Object getAttribute(String key) {
        return sessionAttributes.get(key);
    }

    @Override
    public void removeAttribute(String key) {

    }

    @Override
    public void onEvent(FullSipRequest event) {

    }

    @Override
    public void close() {

    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    public SipContactAOR getSipContactAOR() {
        return sipContactAOR;
    }

    public void setSipContactAOR(SipContactAOR sipContactAOR) {
        this.sipContactAOR = sipContactAOR;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

package com.dxp.sip.bus.future;

import com.dxp.sip.util.ConstantPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
public class ResponseFuture {

    private static final Logger logger = LoggerFactory.getLogger(ResponseFuture.class);

    private static final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    static
    {
        scheduledExecutor.scheduleWithFixedDelay(new CleanTimeOutJob(), 2 * 1000, 100, TimeUnit.MILLISECONDS);
    }

    private static final ConcurrentHashMap<FutureKey, ResponseFuture> FUTURE_MAP = new ConcurrentHashMap<FutureKey, ResponseFuture>(2048);
    private volatile Status status;
    private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();
    private Byte ret;
    private byte[] value;
    private final long startTime;

    protected ResponseFuture()
    {
        this.status = Status.DOING;
        this.value = null;
        ret = null;
        this.startTime = System.currentTimeMillis();
    }

    public static ResponseFuture newFuture(FutureKey key)
    {
        ResponseFuture future = new ResponseFuture();
        FUTURE_MAP.put(key, future);
        return future;
    }

    public static ResponseFuture updateFuture(FutureKey key, byte ret, byte[] value)
    {
        ResponseFuture future = FUTURE_MAP.remove(key);
        if (future != null && !future.isDone())
            future.setResponse(ret, value);
        else
            logger.warn("The timeout response finally return from device [{}].", key.getToURI());
        return future;
    }

    public byte getRet()
    {
        return getRet(ConstantPool.RESEND_TIME);
    }

    public byte getRet(int timeoutInMillis)
    {
        if (timeoutInMillis <= 0)
            timeoutInMillis = ConstantPool.RESEND_TIME;

        if (!isDone())
        {
            long start = System.currentTimeMillis();
            lock.lock();
            try
            {
                while (!isDone())
                {
                    done.await(timeoutInMillis, TimeUnit.MILLISECONDS);
                    if (isDone() || (System.currentTimeMillis() - start) > timeoutInMillis)
                    {
                        break;
                    }
                }
            } catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            } finally
            {
                lock.unlock();
            }

            if (!isDone())
            {
                //设置结果超时
                setTimeOut();
            }
        }
        return returnRet();
    }

    public byte[] getVal()
    {
        return value;
    }

    public boolean isDone()
    {
        return status == Status.DONE;
    }

    private byte returnRet()
    {
        if (ret != null)
            return ret;
        throw new RuntimeException("Ret is null.");
    }

    private void setResponse(byte ret, byte[] value)
    {
        lock.lock();
        try
        {
            if (!isDone())
            {
                this.ret = ret;
                this.value = value;
                status = Status.DONE;
                done.signal();
            }
        } finally
        {
            lock.unlock();
        }
    }

    private void setTimeOut()
    {
        setResponse(ConstantPool.Ret.TIMEOUT, null);
    }

    private long getStartTime()
    {
        return startTime;
    }

    enum Status
    {
        DOING, DONE
    }

    static class CleanTimeOutJob implements Runnable
    {
        @Override
        public void run()
        {
            long currentTime = System.currentTimeMillis();
            ResponseFuture future;
            for (Map.Entry<FutureKey, ResponseFuture> entry : FUTURE_MAP.entrySet())
            {
                future = entry.getValue();
                if (future.isDone())
                    FUTURE_MAP.remove(entry.getKey());
                else
                {
                    if ((currentTime - future.getStartTime()) > ConstantPool.RESEND_TIME)
                    {
                        future.setTimeOut();
                        FUTURE_MAP.remove(entry.getKey());
                    }
                }
            }
        }
    }
}

package com.dxp.sip.bus.fun;

import com.dxp.sip.bus.future.FutureKey;
import com.dxp.sip.bus.future.ResponseFuture;
import com.dxp.sip.codec.sip.AbstractSipHeaders;
import com.dxp.sip.codec.sip.DefaultFullSipRequest;
import com.dxp.sip.codec.sip.FullSipRequest;
import com.dxp.sip.codec.sip.SipHeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMsgProcessor  implements HandlerController<DefaultFullSipRequest> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMsgProcessor.class);


    protected  void dealResponse(DefaultFullSipRequest fullSipRequest, byte result,byte[] data){
        if (fullSipRequest != null)
        {
            AbstractSipHeaders headers = fullSipRequest.headers();
            String cSeq = headers.get(SipHeaderNames.CSEQ);
            FutureKey futureKey = new FutureKey(fullSipRequest.uri(), Long.valueOf(cSeq));
            ResponseFuture.updateFuture(futureKey, result, data);
        }
    }


}

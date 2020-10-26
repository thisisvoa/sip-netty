package com.dxp.sip.bus.handler;

import com.dxp.sip.util.CharsetUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.ByteBufFormat;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.StringUtil;

/**
 * 编码问题
 *
 * @author carzy
 * @date 2020/8/14
 */
public final class GbLoggingHandler extends LoggingHandler {

    public GbLoggingHandler(LogLevel level) {
        super(level);
    }

    protected String format(ChannelHandlerContext ctx, String eventName, Object arg) {

       if(arg instanceof   ByteBuf){
        return    this.formatByteBuf(ctx, eventName, (ByteBuf) arg);
       }
       else if(arg instanceof   ByteBufHolder){
           return  formatByteBufHolder(ctx, eventName, (ByteBufHolder) arg);
       }
       else{
           return  formatSimple(ctx, eventName, arg);
       }

    }

    protected String format( ChannelHandlerContext ctx,  String eventName) {
        String chStr = ctx.channel().id().asShortText();
        return chStr+""+ eventName;
    }

    private final String formatByteBuf(ChannelHandlerContext ctx, String eventName, ByteBuf msg) {
        String chStr = ctx.channel().id().asShortText();
        int length = msg.readableBytes();
        String var10000;
          if (length == 0) {
              var10000 = chStr + ' ' + eventName + ": 0B";
        } else {
            int outputLength = chStr.length() + 1 + eventName.length() + 2 + 10 + 1;
            if (byteBufFormat() == ByteBufFormat.HEX_DUMP) {
                int rows = length / 16 +  (length % 15 == 0? 0 : 1) + 4;
                int hexDumpLength = 2 + rows * 80;
                outputLength += hexDumpLength;
            }
              StringBuilder buf =new StringBuilder(outputLength);
            buf.append(chStr).append(' ').append(eventName).append(": ").append(length).append('B');
            if (byteBufFormat() == ByteBufFormat.HEX_DUMP) {
                buf.append(StringUtil.NEWLINE);
                appendPrettyHexDump(buf, msg);
            }
              var10000=  buf.toString();
        }
        return var10000;
    }

    /**
     * Generates the default log message of the specified event whose argument is a [ByteBufHolder].
     */
    private final String formatByteBufHolder(ChannelHandlerContext ctx, String eventName, ByteBufHolder msg) {
        String chStr = ctx.channel().id().asShortText();
        String msgStr = msg.toString();
        ByteBuf content = msg.content();
        int length = content.readableBytes();
        String var10000;
        if (length == 0) {
            var10000 = chStr + ' ' + eventName + ", " + msgStr + ", 0B";
        } else {
            int outputLength = chStr.length() + 1 + eventName.length() + 2 + msgStr.length() + 2 + 10 + 1;
            if (byteBufFormat() == ByteBufFormat.HEX_DUMP) {
                int rows = length / 16 + (  (length % 15 == 0) ? 0 : 1) + 4;
                int hexDumpLength = 2 + rows * 80;
                outputLength += hexDumpLength;
            }
            StringBuilder buf = new StringBuilder(outputLength);
            buf.append(chStr).append(' ').append(eventName).append(": ")
                    .append(msgStr).append(", ").append(length).append('B');
            if (byteBufFormat() == ByteBufFormat.HEX_DUMP) {
                buf.append(StringUtil.NEWLINE);
                appendPrettyHexDump(buf, content);
            }
            var10000=  buf.toString();
        }
        return var10000;
    }
    public final void appendPrettyHexDump( StringBuilder dump,  ByteBuf buf) {
        dump.append(buf.toString(CharsetUtils.GB_2313));
    }

    private final String formatSimple(ChannelHandlerContext ctx, String eventName, Object msg) {
        String chStr = ctx.channel().id().asShortText();
        String msgStr = msg.toString();
        return chStr + ' ' + eventName + ": " + msgStr;
    }

}
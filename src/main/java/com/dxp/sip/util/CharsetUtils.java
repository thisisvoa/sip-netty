package com.dxp.sip.util;

import java.nio.charset.Charset;
import io.netty.util.CharsetUtil;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.CharsetEncoder;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.InternalThreadLocalMap;
import java.nio.charset.CharsetDecoder;
import java.util.Map;

/**
 * @author carzy
 * @date 2020/8/12
 */
public final class CharsetUtils {
    /**
     * 16-bit UTF (UCS Transformation Format) whose byte order is identified by
     * an optional byte-order mark
     */

    public static final Charset GB_2313= Charset.forName("gb2312");

    /**
     * 16-bit UTF (UCS Transformation Format) whose byte order is identified by
     * an optional byte-order mark
     */

    public static final Charset UTF_16 = CharsetUtil.UTF_16;

    /**
     * 16-bit UTF (UCS Transformation Format) whose byte order is big-endian
     */

    public static final Charset UTF_16BE = CharsetUtil.UTF_16BE;

    /**
     * 16-bit UTF (UCS Transformation Format) whose byte order is little-endian
     */

    public static final Charset UTF_16LE = CharsetUtil.UTF_16LE;

    /**
     * 8-bit UTF (UCS Transformation Format)
     */

    public static final Charset UTF_8 = CharsetUtil.UTF_8;

    /**
     * ISO Latin Alphabet No. 1, as known as <tt>ISO-LATIN-1</tt>
     */

    public static final Charset ISO_8859_1 = CharsetUtil.ISO_8859_1;

    /**
     * 7-bit ASCII, as known as ISO646-US or the Basic Latin block of the
     * Unicode character set
     */

    public static final Charset US_ASCII = CharsetUtil.US_ASCII;

    private static final Charset[] CHARSETS = new Charset[]{UTF_16, UTF_16BE, UTF_16LE, UTF_8, ISO_8859_1, US_ASCII};


    /**
     * Returns a new [CharsetEncoder] for the [Charset] with specified error actions.
     *
     * @param charset                   The specified charset
     * @param malformedInputAction      The encoder's action for malformed-input errors
     * @param unmappableCharacterAction The encoder's action for unmappable-character errors
     * @return The encoder for the specified `charset`
     */
    private final CharsetEncoder encoder(Charset charset, CodingErrorAction malformedInputAction, CodingErrorAction unmappableCharacterAction) {
        ObjectUtil.checkNotNull(charset, "charset");
        CharsetEncoder e = charset.newEncoder();
        e.onMalformedInput(malformedInputAction).onUnmappableCharacter(unmappableCharacterAction);
        return e;
    }

    /**
     * Returns a new [CharsetEncoder] for the [Charset] with the specified error action.
     *
     * @param charset           The specified charset
     * @param codingErrorAction The encoder's action for malformed-input and unmappable-character errors
     * @return The encoder for the specified `charset`
     */
    private final CharsetEncoder encoder(Charset charset, CodingErrorAction codingErrorAction) {
        return encoder(charset, codingErrorAction, codingErrorAction);
    }

    /**
     * Returns a cached thread-local [CharsetEncoder] for the specified [Charset].
     *
     * @param charset The specified charset
     * @return The encoder for the specified `charset`
     */
    private final CharsetEncoder encoder(Charset charset) {
        ObjectUtil.checkNotNull(charset, "charset");
        Map map = InternalThreadLocalMap.get().charsetEncoderCache();
        CharsetEncoder e = (CharsetEncoder) map.get(charset);
        if (e != null) {
            e.reset().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            return e;
        }
        e = encoder(charset, CodingErrorAction.REPLACE, CodingErrorAction.REPLACE);
        map.put(charset,e);
        return e;
    }

    /**
     * Returns a new [CharsetDecoder] for the [Charset] with specified error actions.
     *
     * @param charset                   The specified charset
     * @param malformedInputAction      The decoder's action for malformed-input errors
     * @param unmappableCharacterAction The decoder's action for unmappable-character errors
     * @return The decoder for the specified `charset`
     */
    private final CharsetDecoder decoder(Charset charset, CodingErrorAction malformedInputAction, CodingErrorAction unmappableCharacterAction) {
        ObjectUtil.checkNotNull(charset, "charset");
        CharsetDecoder d = charset.newDecoder();
        d.onMalformedInput(malformedInputAction).onUnmappableCharacter(unmappableCharacterAction);
        return d;
    }

    /**
     * Returns a new [CharsetDecoder] for the [Charset] with the specified error action.
     *
     * @param charset           The specified charset
     * @param codingErrorAction The decoder's action for malformed-input and unmappable-character errors
     * @return The decoder for the specified `charset`
     */
    private final CharsetDecoder decoder(Charset charset, CodingErrorAction codingErrorAction) {
        return decoder(charset, codingErrorAction, codingErrorAction);
    }

    /**
     * Returns a cached thread-local [CharsetDecoder] for the specified [Charset].
     *
     * @param charset The specified charset
     * @return The decoder for the specified `charset`
     */
    private final CharsetDecoder decoder(Charset charset) {
        ObjectUtil.checkNotNull(charset, "charset");
        Map map = InternalThreadLocalMap.get().charsetDecoderCache();
        CharsetDecoder d = (CharsetDecoder) map.get(charset);
        if (d != null) {
            d.reset().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            return d;
        }
        d = decoder(charset, CodingErrorAction.REPLACE, CodingErrorAction.REPLACE);
       map.put(charset,d);
        return d;
    }
}
// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

/**
 *
 * @author Zqq
 */
public class CharArrayUtil {

    public static int lastNotWhitespaceOrNewLine(final char[] buf, final int from, final int end) {
        int pos;
        finish:
        for (pos = end - 1; pos >= from; pos--) {
            switch (buf[pos]) {
                case ' ':
                case '\t':
                case '\b':
                case '\f':
                    continue finish;
                default:
                    // not a blank line
                    return pos;
            }
        }
        return pos;
    }

//    public static int getTrimRightBlankLinePosition(final char[] buf) {
//        return getTrimRightBlankLinePosition(buf, 0, buf.length);
//    }
//
//    public static int getTrimRightBlankLinePosition(final char[] buf, final int from, final int end) {
//        int pos = lastNotWhitespaceOrNewLine(buf, from, end);
//        if (pos < from) {
//            return end;
//        } else if (buf[pos] == '\n') {
//            //linux or windows new line
//            if (pos != from) {
//                //when not the first one
//                if (buf[pos - 1] == '\r') {
//                    pos--; //Windows new line
//                }
//            }
//            return pos;
//        } else if (buf[pos] == '\r') {
//            return pos;
//        } else {
//            return end;
//        }
//    }
}

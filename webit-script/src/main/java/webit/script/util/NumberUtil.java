// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import webit.script.io.Out;

/**
 *
 * @author zqq90
 */
public class NumberUtil {

    private static final char[] DIGIT_TENS = {
        '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
        '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
        '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
        '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
        '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
        '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
        '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
        '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
        '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
        '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'
    };
    private static final char[] DIGIT_ONES = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    public static void print(long i, Out out) {
        if (i == Long.MIN_VALUE) {
            out.write("-9223372036854775808");
            return;
        }
        long q;
        int r;
        int charPos = 20;
        final boolean negative;
        final char[] buf = new char[20];

        if (i < 0) {
            negative = true;
            i = -i;
        } else {
            negative = false;
        }
        final char[] digitOnes = DIGIT_ONES;
        final char[] digitsTens = DIGIT_TENS;
        while (i > Integer.MAX_VALUE) {
            q = i / 100;
            // really: r = i - (q * 100)
            r = (int) (i - ((q << 6) + (q << 5) + (q << 2)));
            i = q;
            buf[--charPos] = digitOnes[r];
            buf[--charPos] = digitsTens[r];
        }

        int q2;
        int i2 = (int) i;
        while (i2 >= 65536) {
            q2 = i2 / 100;
            // really: r = i2 - (q * 100)
            r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
            i2 = q2;
            buf[--charPos] = digitOnes[r];
            buf[--charPos] = digitsTens[r];
        }

        for (;;) {
            q2 = (i2 * 52429) >>> (16 + 3);
            buf[--charPos] = digitOnes[i2 - ((q2 << 3) + (q2 << 1))]; // DIGITS[i2-(q2*10)]
            i2 = q2;
            if (i2 == 0) {
                break;
            }
        }
        if (negative) {
            buf[--charPos] = '-';
        }
        out.write(buf, charPos, 20 - charPos);
    }

    public static void print(int i, Out out) {
        if (i == Integer.MIN_VALUE) {
            out.write("-2147483648");
            return;
        }
        int q, r;
        int charPos = 11;
        final boolean negative;
        final char[] buf = new char[11];

        if (i < 0) {
            negative = true;
            i = -i;
        } else {
            negative = false;
        }

        final char[] digitOnes = DIGIT_ONES;
        final char[] digitsTens = DIGIT_TENS;
        while (i >= 65536) {
            q = i / 100;
            // really: r = i - (q * 100)
            r = i - ((q << 6) + (q << 5) + (q << 2));
            i = q;
            buf[--charPos] = digitOnes[r];
            buf[--charPos] = digitsTens[r];
        }

        for (;;) {
            q = (i * 52429) >>> (16 + 3);
            buf[--charPos] = digitOnes[i - ((q << 3) + (q << 1))]; // DIGITS[i-(q*10)]
            i = q;
            if (i == 0) {
                break;
            }
        }
        if (negative) {
            buf[--charPos] = '-';
        }
        out.write(buf, charPos, 11 - charPos);
    }

}

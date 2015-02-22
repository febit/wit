// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A mini version of 'Jodd-props', refer to the
 * <a href="https://github.com/oblac/jodd">Jodd</a> project.
 *
 * @author zqq90
 */
public final class Props {

    private static final int STATE_TEXT = 1;
    private static final int STATE_ESCAPE = 2;
    private static final int STATE_ESCAPE_NEWLINE = 3;
    private static final int STATE_COMMENT = 4;
    private static final int STATE_VALUE = 5;

    private final Map<String, Entry> data;

    public Props() {
        this.data = new HashMap<String, Entry>();
    }

    /**
     * Loads props from the char array.
     *
     * @param chars
     */
    public void load(final char[] chars) {
        parse(chars);
    }

    public void merge(final Props props) {
        for (Map.Entry<String, Entry> entry : props.data.entrySet()) {
            Entry propsEntry = entry.getValue();
            put(entry.getKey(), propsEntry.value, propsEntry.append);
        }
    }

    public String remove(final String name) {
        return resolveValue(data.remove(name));
    }

    public String get(final String key) {
        return resolveValue(data.get(key));
    }

    public void set(final String name, final String value) {
        put(name, value, false);
    }

    public void append(final String name, final String value) {
        put(name, value, true);
    }

    @SuppressWarnings("unchecked")
    public void extractTo(final Map target) {
        for (Map.Entry<String, Entry> entry : this.data.entrySet()) {
            target.put(entry.getKey(), resolveValue(entry.getValue()));
        }
    }

    public Iterable<String> keySet() {
        return this.data.keySet();
    }

    private void put(String key, String value, boolean append) {
        if (value == null) {
            if (!append) {
                data.remove(key);
            }
        } else {
            if (append) {
                Entry pv = data.get(key);
                if (pv != null) {
                    value = pv.value + ',' + value;
                    append = pv.append;
                }
            }
            data.put(key, new Entry(value, append));
        }
    }

    /**
     * Adds accumulated value to key and current section.
     */
    private void put(String section, String key, String value, boolean append) {

        // ignore lines without =
        if (key == null) {
            return;
        }

        if (section != null) {
            if (key.length() != 0) {
                key = section + '.' + key;
            } else {
                key = section;
            }
        }

        put(key, value, append);
    }

    private String resolveValue(Entry entry) {
        if (entry == null) {
            return null;
        }
        return resolveValue(entry.value, 0);
    }

    private String resolveValue(String template, int deep) {
        if (deep > 100) {
            //Note: MAX_MACROS_DEEP
            return template;
        }
        ++deep;
        if (!template.contains("${")) {
            return template;
        }

        final int len = template.length();
        final StringBuilder result = new StringBuilder(len);

        int i = 0;
        while (i < len) {
            int ndx = template.indexOf("${", i);
            if (ndx < 0) {
                result.append(i == 0 ? template : template.substring(i));
                break;
            }

            // check escaped
            int j = ndx - 1;
            boolean escape = false;
            int count = 0;

            while ((j >= 0) && (template.charAt(j) == '\\')) {
                escape = !escape;
                if (escape) {
                    count++;
                }
                j--;
            }
            result.append(template.substring(i, ndx - count));

            if (escape) {
                result.append("${");
                i = ndx + 2;
                continue;
            }

            // find macros end
            ndx += 2;
            int end = template.indexOf('}', ndx);
            if (end < 0) {
                throw new IllegalArgumentException("Invalid string template, unclosed macro at: " + (ndx - 2));
            }

            // find value and append
            Entry entry = data.get(template.substring(ndx, end));
            if (entry != null) {
                result.append(resolveValue(entry.value, deep));
            }
            i = end + 1;
        }
        return result.toString();
    }

    private void parse(final char[] in) {

        final StringBuilder sb = new StringBuilder();
        final int len = in.length;

        int state = STATE_TEXT;
        int stateOnEscape = -1;

        boolean insideSection = false;
        String currentSection = null;
        String key = null;
        boolean append = false;

        int ndx = 0;
        while (ndx < len) {
            final char c = in[ndx++];

            if (state == STATE_COMMENT) {
                // comment, skip to the end of the line
                if (c == '\n') {
                    state = STATE_TEXT;
                }
            } else if (state == STATE_ESCAPE) {
                state = stateOnEscape;//STATE_VALUE
                switch (c) {
                    case '\r':
                    case '\n':
                        // if the EOL is \n or \r\n, escapes both chars
                        state = STATE_ESCAPE_NEWLINE;
                        break;
                    // encode UTF character
                    case 'u':
                        int value = 0;

                        for (int i = 0; i < 4; i++) {
                            final char hexChar = in[ndx++];
                            if (hexChar >= '0' && hexChar <= '9') {
                                value = (value << 4) + hexChar - '0';
                            } else if (hexChar >= 'a' && hexChar <= 'f') {
                                value = (value << 4) + 10 + hexChar - 'a';
                            } else if (hexChar >= 'A' && hexChar <= 'F') {
                                value = (value << 4) + 10 + hexChar - 'A';
                            } else {
                                throw new IllegalArgumentException("Malformed \\uXXXX encoding.");
                            }
                        }
                        sb.append((char) value);
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    default:
                        sb.append(c);
                }
            } else if (state == STATE_TEXT) {
                switch (c) {
                    case '\\':
                        // escape char, take the next char as is
                        stateOnEscape = state;
                        state = STATE_ESCAPE;
                        break;

                    // start section
                    case '[':
                        sb.setLength(0);
                        insideSection = true;
                        break;

                    // end section
                    case ']':
                        if (insideSection) {
                            currentSection = sb.toString().trim();
                            if (currentSection.length() == 0) {
                                currentSection = null;
                            } else {
                                int split = currentSection.indexOf(':');
                                if (split == 0) {
                                    throw new IllegalArgumentException("Invalid section, should not start with ':' : " + currentSection);
                                }
                                if (split > 0) {
                                    String type = currentSection.substring(split + 1).trim();
                                    currentSection = currentSection.substring(0, split).trim();
                                    if (type.length() != 0) {
                                        put(currentSection + ".@class", type, false);
                                    }
                                }
                            }
                            sb.setLength(0);
                            insideSection = false;
                        } else {
                            sb.append(c);
                        }
                        break;

                    case '#':
                    case ';':
                        state = STATE_COMMENT;
                        break;

                    // assignment operator
                    case '+':
                        if (ndx == len || in[ndx] != '=') {
                            sb.append(c);
                            break;
                        }
                        append = true;
                        continue;
                    case '=':
                        if (key == null) {
                            key = sb.toString().trim();
                            sb.setLength(0);
                        } else {
                            sb.append(c);
                        }
                        state = STATE_VALUE;
                        break;

                    case '\r':
                    case '\n':
                        put(currentSection, key, sb.toString().trim(), append);
                        sb.setLength(0);
                        key = null;
                        append = false;
                        break;

                    case ' ':
                    case '\t':
                        // ignore whitespaces
                        break;
                    default:
                        sb.append(c);
                }
            } else {
                switch (c) {
                    case '\\':
                        // escape char, take the next char as is
                        stateOnEscape = state;
                        state = STATE_ESCAPE;
                        break;

                    case '\r':
                    case '\n':
                        if ((state != STATE_ESCAPE_NEWLINE) || (c != '\n')) {
                            put(currentSection, key, sb.toString().trim(), append);
                            sb.setLength(0);
                            key = null;
                            append = false;

                            // end of value, continue to text
                            state = STATE_TEXT;
                        }
                        break;

                    case ' ':
                    case '\t':
                        if ((state == STATE_ESCAPE_NEWLINE)) {
                            break;
                        }
                    default:
                        sb.append(c);
                        state = STATE_VALUE;

                        // check for ''' beginning
                        if (sb.length() == 3
                                && sb.charAt(0) == '\''
                                && sb.charAt(1) == '\''
                                && sb.charAt(2) == '\'') {

                            int end = ndx;
                            int count = 0;
                            while (end < len) {
                                if (in[end] == '\'') {
                                    if (count == 2) {
                                        end -= 2;
                                        break;
                                    }
                                    count++;
                                } else {
                                    count = 0;
                                }
                                end++;
                            }

                            put(currentSection, key, new String(in, ndx, end - ndx), append);

                            sb.setLength(0);
                            key = null;
                            append = false;

                            // end of value, continue to text
                            state = STATE_TEXT;
                            ndx = end + 3;
                        }
                }
            }
        }

        if (key != null) {
            put(currentSection, key, sb.toString().trim(), append);
        }
    }

    private static class Entry {

        final String value;
        final boolean append;

        Entry(final String value, boolean append) {
            this.value = value;
            this.append = append;
        }
    }

}

// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

    private List<String> modules;

    public Props() {
        this.data = new HashMap<>();
    }

    /**
     * Loads props from the string.
     *
     * @param str
     */
    public Props load(final String str) {
        if (str != null) {
            parse(str.toCharArray());
        }
        return this;
    }

    /**
     * Loads props from the char array.
     *
     * @param chars
     */
    public Props load(final char[] chars) {
        if (chars != null) {
            parse(chars);
        }
        return this;
    }

    public void addModule(String module) {
        if (module == null) {
            return;
        }
        initModules();
        this.modules.add(module);
    }

    private void initModules() {
        if (this.modules == null) {
            this.modules = new ArrayList<>();
        }
    }

    public boolean containsModule(String name) {
        if (this.modules == null) {
            return false;
        }
        return modules.contains(name);
    }

    public List<String> getModules() {
        if (this.modules == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(modules);
    }

    public String getModulesString() {
        if (this.modules == null) {
            return "";
        }
        return StringUtil.join(this.modules, ',');
    }

    public void addModules(Collection<String> module) {
        if (module == null) {
            return;
        }
        initModules();
        this.modules.addAll(module);
    }

    public void merge(final Props props) {
        props.data.forEach((k, v) -> {
            put(k, v.value, v.append);
        });
        addModules(props.modules);
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
    public void export(final Map target) {
        this.extractTo(target);
    }

    @SuppressWarnings("unchecked")
    public void extractTo(final Map target) {
        this.data.forEach((k, v) -> {
            target.put(k, resolveValue(v));
        });
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
            key = key.isEmpty() ? section : section + '.' + key;
        }
        put(key, value, append);
    }

    private String resolveValue(Entry entry) {
        if (entry == null) {
            return null;
        }
        return resolveValue(entry.value, 0);
    }

    private String resolveValue(String template, int macrosTimes) {
        if (macrosTimes > 100) {
            //Note: MAX_MACROS
            return template;
        }

        int ndx = template.indexOf("${");
        if (ndx < 0) {
            return template;
        }

        // check escaped
        int i = ndx - 1;
        boolean escape = false;
        int count = 0;

        while ((i >= 0) && (template.charAt(i) == '\\')) {
            escape = !escape;
            if (escape) {
                count++;
            }
            i--;
        }
        final String partStart = template.substring(0, ndx - count);

        // Anyway, resolve ending first
        final String resolvedEnding = resolveValue(template.substring(ndx + 2), ++macrosTimes);

        // If ${ is escaped
        if (escape) {
            return partStart + "${" + resolvedEnding;
        }

        //
        int end = resolvedEnding.indexOf('}');

        if (end < 0) {
            //XXX lost index
            throw new IllegalArgumentException("Invalid string template, unclosed macro ");
        }
        final String partEnd = resolvedEnding.substring(end + 1);
        // find value and append
        Entry entry = data.get(resolvedEnding.substring(0, end));
        if (entry != null && entry.value != null) {
            return partStart + resolveValue(entry.value, ++macrosTimes) + partEnd;
        } else {
            return partStart.concat(partEnd);
        }
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
                            if (currentSection.isEmpty()) {
                                currentSection = null;
                            } else {
                                int split = currentSection.indexOf(':');
                                if (split == 0) {
                                    throw new IllegalArgumentException("Invalid section, should not start with ':' : " + currentSection);
                                }
                                if (split > 0) {
                                    String type = currentSection.substring(split + 1).trim();
                                    currentSection = currentSection.substring(0, split).trim();
                                    if (!type.isEmpty()) {
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
                        if (state == STATE_ESCAPE_NEWLINE) {
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

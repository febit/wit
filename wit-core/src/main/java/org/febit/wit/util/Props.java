// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.util.*;
import java.util.function.BiConsumer;

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
        props.data.forEach((k, v) -> put(k, v.value, v.append));
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

    public void forEach(BiConsumer<? super String, ? super String> action) {
        Objects.requireNonNull(action);
        this.data.forEach((k, v) -> action.accept(k, resolveValue(v)));
    }

    public void extractTo(final Map<? super String, ? super String> target) {
        forEach(target::put);
    }

    private void put(String key, String value, boolean append) {
        if (value == null) {
            if (!append) {
                data.remove(key);
            }
            return;
        }
        if (append) {
            Entry pv = data.get(key);
            if (pv != null) {
                value = pv.value + ',' + value;
                append = pv.append;
            }
        }
        data.put(key, new Entry(key, value, append));
    }

    /**
     * Adds accumulated value to key and current section.
     */
    private void put(String section, String key, String value, boolean append) {
        // ignore lines without =
        if (key == null) {
            return;
        }
        if (section == null) {
            put(key, value, append);
            return;
        }
        put(key.isEmpty()
                        ? section
                        : section + '.' + key,
                value, append);
    }

    protected Entry resolveEntry(final String space, final String key) {
        if (space == null || space.isEmpty()) {
            return data.get(key);
        }
        Entry entry = data.get(space + '.' + key);
        if (entry != null) {
            return entry;
        }
        return resolveEntry(cutSpace(space), key);
    }

    private String resolveValue(Entry entry) {
        if (entry == null) {
            return null;
        }
        return resolveValue(entry.space(), entry.value, 0);
    }

    private String resolveValue(String space, String template, int macrosTimes) {
        if (macrosTimes > 100) {
            //Note: MAX_MACROS
            throw new IllegalArgumentException("Invalid string template, macros nested times > 100");
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
        final String resolvedEnding = resolveValue(space, template.substring(ndx + 2), ++macrosTimes);

        // If ${ is escaped
        if (escape) {
            return partStart + "${" + resolvedEnding;
        }

        //
        int end = resolvedEnding.indexOf('}');
        if (end < 0) {
            // XXX lost index
            throw new IllegalArgumentException("Invalid string template, unclosed macro ");
        }
        final String partEnd = resolvedEnding.substring(end + 1);
        // find value and append
        String key = resolvedEnding.substring(0, end);
        Entry entry = resolveEntry(space, key);
        if (entry != null && entry.value != null) {
            return partStart + resolveValue(entry.space(), entry.value, ++macrosTimes) + partEnd;
        } else {
            return partStart + partEnd;
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
                continue;
            }
            if (state == STATE_ESCAPE) {
                state = stateOnEscape;//STATE_VALUE
                switch (c) {
                    case '\r':
                    case '\n':
                        // if the EOL is \n or \r\n, escapes both chars
                        state = STATE_ESCAPE_NEWLINE;
                        break;
                    // encode UTF character
                    case 'u':
                        sb.append(readUtf(in, ndx));
                        ndx += 4;
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
                continue;
            }
            if (state == STATE_TEXT) {
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
                            currentSection = formatSection(sb.toString());
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
                        if (sb.length() > 0) {
                            sb.append(c);
                        }
                        // ignore whitespaces before the key
                        break;
                    default:
                        sb.append(c);
                }
                continue;
            }

            // STATE_VALUE || STATE_ESCAPE_NEWLINE
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

                    if (isMultiLineStartFlag(sb)) {
                        int end = findMultiLineEndFlagPos(in, ndx);
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

        if (key != null) {
            put(currentSection, key, sb.toString().trim(), append);
        }
    }

    private String formatSection(String section) {
        String currentSection = section.trim();
        if (currentSection.isEmpty()) {
            return null;
        }
        int split = currentSection.indexOf(':');
        if (split == 0) {
            throw new IllegalArgumentException("Invalid section, should not start with ':' : " + currentSection);
        }
        if (split < 0) {
            return currentSection;
        }
        String type = currentSection.substring(split + 1).trim();
        currentSection = currentSection.substring(0, split).trim();
        if (!type.isEmpty()) {
            put(currentSection + ".@class", type, false);
        }
        return currentSection;
    }

    private static char readUtf(final char[] in, final int start) {
        final int end = start + 4;
        if (end >= in.length) {
            throw new IllegalArgumentException("No more chars for UTF");
        }
        int value = 0;
        for (int i = start; i < end; i++) {
            final char hexChar = in[i];
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
        return (char) value;
    }

    private static int findMultiLineEndFlagPos(final char[] in, final int start) {
        final int len = in.length;
        int end = start;
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
        return end;
    }

    private static boolean isMultiLineStartFlag(final StringBuilder sb) {
        return sb.length() == 3
                && sb.charAt(0) == '\''
                && sb.charAt(1) == '\''
                && sb.charAt(2) == '\'';
    }

    private static String cutSpace(String key) {
        int i = key.lastIndexOf('.');
        return i < 0 ? null : key.substring(0, i);
    }

    private static class Entry {

        final String key;
        final String value;
        final boolean append;

        Entry(final String key, final String value, boolean append) {
            this.key = key;
            this.value = value;
            this.append = append;
        }

        String space() {
            return cutSpace(key);
        }
    }
}

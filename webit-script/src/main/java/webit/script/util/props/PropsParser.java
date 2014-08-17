// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.
package webit.script.util.props;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import webit.script.util.CharUtil;
import webit.script.util.StringUtil;

/**
 * {@link Props} parser.
 */
final class PropsParser {
    
    final PropsData propsData;

    /**
     * Don't include empty properties.
     */
    private boolean skipEmptyProps = true;

    private static final char[] MULTILINE_END = "'''".toCharArray();

    PropsParser() {
        this.propsData = new PropsData();
    }

    void setSkipEmptyProps(boolean skipEmptyProps) {
        this.skipEmptyProps = skipEmptyProps;
    }

    /**
     * Parsing states.
     */
    private enum ParseState {

        TEXT,
        ESCAPE,
        ESCAPE_NEWLINE,
        COMMENT,
        VALUE
    }

    /**
     * Different assignment operators.
     */
    private enum Operator {

        ASSIGN,
        QUICK_APPEND,
        COPY
    }

    /**
     * Loads properties.
     */
    void parse(final char[] in) {

        ParseState state = ParseState.TEXT;
        ParseState stateOnEscape = null;

        boolean insideSection = false;
        String currentSection = null;
        String key = null;
        Operator operator = Operator.ASSIGN;
        final StringBuilder sb = new StringBuilder();

        final int len = in.length;
        int ndx = 0;
        while (ndx < len) {
            final char c = in[ndx];
            ndx++;

            if (state == ParseState.COMMENT) {
                // comment, skip to the end of the line
                if (c == '\n') {
                    state = ParseState.TEXT;
                }
            } else if (state == ParseState.ESCAPE) {
                state = stateOnEscape;//ParseState.VALUE;
                switch (c) {
                    case '\r':
                    case '\n':
                        // if the EOL is \n or \r\n, escapes both chars
                        state = ParseState.ESCAPE_NEWLINE;
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
            } else if (state == ParseState.TEXT) {
                switch (c) {
                    case '\\':
                        // escape char, take the next char as is
                        stateOnEscape = state;
                        state = ParseState.ESCAPE;
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
                            sb.setLength(0);
                            insideSection = false;
                            if (currentSection.length() == 0) {
                                currentSection = null;
                            }
                        } else {
                            sb.append(c);
                        }
                        break;

                    case '#':
                    case ';':
                        state = ParseState.COMMENT;
                        break;

                    // copy operator
                    case '<':
                        if (ndx == len || in[ndx] != '=') {
                            sb.append(c);
                            break;
                        }
                        operator = Operator.COPY;
                        //ndx++;
                        continue;

                    // assignment operator
                    case '+':
                        if (ndx == len || in[ndx] != '=') {
                            sb.append(c);
                            break;
                        }
                        operator = Operator.QUICK_APPEND;
                        //ndx++;
                        continue;
                    case '=':
                    case ':':
                        if (key == null) {
                            key = sb.toString().trim();
                            sb.setLength(0);
                        } else {
                            sb.append(c);
                        }
                        state = ParseState.VALUE;
                        break;

                    case '\r':
                    case '\n':
                        add(currentSection, key, sb, true, operator);
                        sb.setLength(0);
                        key = null;
                        operator = Operator.ASSIGN;
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
                        state = ParseState.ESCAPE;
                        break;

                    case '\r':
                    case '\n':
                        if ((state != ParseState.ESCAPE_NEWLINE) || (c != '\n')) {
                            add(currentSection, key, sb, true, operator);
                            sb.setLength(0);
                            key = null;
                            operator = Operator.ASSIGN;

                            // end of value, continue to text
                            state = ParseState.TEXT;
                        }
                        break;

                    case ' ':
                    case '\t':
                        if ((state == ParseState.ESCAPE_NEWLINE)) {
                            break;
                        }
                    default:
                        sb.append(c);
                        state = ParseState.VALUE;

                        if (sb.length() == 3) {

                            // check for ''' beginning
                            if (sb.toString().equals("'''")) {
                                sb.setLength(0);
                                int endIndex = CharUtil.indexOf(in, MULTILINE_END, ndx);
                                if (endIndex == -1) {
                                    endIndex = len;
                                }
                                sb.append(in, ndx, endIndex - ndx);

                                // append
                                add(currentSection, key, sb, false, operator);
                                sb.setLength(0);
                                key = null;
                                operator = Operator.ASSIGN;

                                // end of value, continue to text
                                state = ParseState.TEXT;
                                ndx = endIndex + 3;
                            }
                        }
                }
            }
        }

        if (key != null) {
            add(currentSection, key, sb, true, operator);
        }
    }

    /**
     * Adds accumulated value to key and current section.
     */
    private void add(
            final String section, final String key,
            final StringBuilder value, final boolean trim, final Operator operator) {

        // ignore lines without : or =
        if (key == null) {
            return;
        }
        String fullKey = key;

        if (section != null) {
            if (fullKey.length() != 0) {
                fullKey = section + '.' + fullKey;
            } else {
                fullKey = section;
            }
        }
        String v = value.toString();

        if (trim) {
            v = v.trim();
        }

        if (v.length() == 0 && skipEmptyProps) {
            return;
        }

        extractProfilesAndAdd(fullKey, v, operator);
    }

    /**
     * Extracts profiles from the key name and adds key-value to them.
     */
    private void extractProfilesAndAdd(final String key, final String value, final Operator operator) {
        String fullKey = key;
        int ndx = fullKey.indexOf('<'); //PROFILE_LEFT
        if (ndx == -1) {
            justAdd(fullKey, value, null, operator);
            return;
        }

        // extract profiles
        ArrayList<String> keyProfiles = new ArrayList<String>();

        while (true) {
            ndx = fullKey.indexOf('<'); //PROFILE_LEFT
            if (ndx == -1) {
                break;
            }

            final int len = fullKey.length();

            int ndx2 = fullKey.indexOf('>', ndx + 1); //PROFILE_RIGHT
            if (ndx2 == -1) {
                ndx2 = len;
            }

            // remember profile
            final String profile = fullKey.substring(ndx + 1, ndx2);
            keyProfiles.add(profile);

            // extract profile from key
            ndx2++;
            final String right = (ndx2 == len) ? "" : fullKey.substring(ndx2);
            fullKey = fullKey.substring(0, ndx) + right;
        }

        if (fullKey.length() != 0 && fullKey.charAt(0) == '.') {
            // check for special case when only profile is defined in section
            fullKey = fullKey.substring(1);
        }

        // add value to extracted profiles
        justAdd(fullKey, value, keyProfiles, operator);
    }

    /**
     * Core key-value addition.
     */
    private void justAdd(final String key, final String value, final ArrayList<String> keyProfiles, final Operator operator) {
        if (operator == Operator.COPY) {
            HashMap<String, Object> target = new HashMap<String, Object>();

            String[] profiles = null;
            if (keyProfiles != null) {
                profiles = keyProfiles.toArray(new String[keyProfiles.size()]);
            }

            String[] sources = StringUtil.splitc(value, ',');
            for (String source : sources) {
                source = source.trim();
                String[] wildcards = new String[]{source + ".*"};
                propsData.extract(target, profiles, wildcards);

                for (Map.Entry<String, Object> entry : target.entrySet()) {
                    String entryKey = entry.getKey();
                    String suffix = entryKey.substring(source.length());

                    String newKey = key + suffix;
                    if (profiles == null) {
                        propsData.putBaseProperty(newKey, "${" + entryKey + "}", false);
                    } else {
                        for (final String p : keyProfiles) {
                            propsData.putProfileProperty(newKey, "${" + entryKey + "}", p, false);
                        }
                    }
                }
            }
            return;
        }

        boolean append = operator == Operator.QUICK_APPEND;
        if (keyProfiles == null) {
            propsData.putBaseProperty(key, value, append);
            return;
        }
        for (final String p : keyProfiles) {
            propsData.putProfileProperty(key, value, p, append);
        }

    }
}

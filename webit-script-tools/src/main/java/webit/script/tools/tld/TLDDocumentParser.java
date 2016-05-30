// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.tools.tld;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class TLDDocumentParser {

    public static TLDFunction[] parse(InputStream input) throws Exception {

        final NodeList functionNodeList = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(input)
                .getDocumentElement()
                .getElementsByTagName("function");

        final int len;
        final TLDFunction[] functions = new TLDFunction[len = functionNodeList.getLength()];

        for (int i = 0; i < len; i++) {
            final Element functionElement = (Element) functionNodeList.item(i);

            final String name = getRequestChildElementValue(functionElement, "name").trim();
            final String declaredClass = getRequestChildElementValue(functionElement, "function-class").trim();

            final String returnType;
            final String methodName;
            final String[] parameterTypes;
            final String functionSignature = getRequestChildElementValue(functionElement, "function-signature").trim();

            final int lparenIndex = functionSignature.indexOf('(');
            int methodNameEnd = lparenIndex;
            while (methodNameEnd >= 0) {
                if (functionSignature.charAt(--methodNameEnd) > ' ') {
                    methodNameEnd++;
                    break;
                }
            }
            int returnTypeEndIndex = functionSignature.lastIndexOf(' ', methodNameEnd - 1);
            returnType = functionSignature.substring(0, returnTypeEndIndex).trim();

            methodName = functionSignature.substring(returnTypeEndIndex + 1, methodNameEnd);

            String parameterTypesString = functionSignature.substring(lparenIndex + 1, functionSignature.lastIndexOf(')')).trim();
            if (parameterTypesString.length() != 0) {
                parameterTypes = StringUtil.toArray(parameterTypesString);
            } else {
                parameterTypes = null;
            }

            functions[i] = new TLDFunction(name, declaredClass, returnType, methodName, parameterTypes);
        }
        return functions;
    }

    protected static String getRequestChildElementValue(Element parent, String name) {
        NodeList list = parent.getElementsByTagName(name);
        if (list == null || list.getLength() == 0) {
            throw new RuntimeException("Not found child element named: " + name);
        }
        return list.item(0).getFirstChild().getNodeValue();
    }
}

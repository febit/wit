// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.tld;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import webit.script.util.CharUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class TLDDocumentParser {

    public static TLDFunction[] parse(InputStream inputStream) throws Exception {

        final NodeList functionNodeList = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(inputStream)
                .getDocumentElement()
                .getElementsByTagName("function");

        final int len;
        final TLDFunction[] functions = new TLDFunction[len = functionNodeList.getLength()];

        for (int i = 0; i < len; i++) {
            final Element functionElement = (Element) functionNodeList.item(i);

            final String name = getRequestChildElementValue(functionElement, "name").trim();
            final String declaredClass = getRequestChildElementValue(functionElement, "function-class").trim();

            final String methodName;
            final String[] argumentTypes;
            final String functionSignature = getRequestChildElementValue(functionElement, "function-signature").trim();

            final int lparenIndex = functionSignature.indexOf('(');
            int methodNameEnd = lparenIndex;
            while (methodNameEnd >= 0) {
                if (CharUtil.isWhitespace(functionSignature.charAt(--methodNameEnd)) == false) {
                    methodNameEnd++;
                    break;
                }
            }
            methodName = functionSignature.substring(
                    functionSignature.lastIndexOf(' ', methodNameEnd - 1) + 1, methodNameEnd);

            String argumentTypeString = functionSignature.substring(lparenIndex + 1, functionSignature.lastIndexOf(')')).trim();
            if (argumentTypeString.length() != 0) {
                StringUtil.trimAll(argumentTypes = StringUtil.splitc(argumentTypeString, ','));
            } else {
                argumentTypes = null;
            }

            functions[i] = new TLDFunction(name, declaredClass, methodName, argumentTypes);
        }
        return functions;
    }

    protected static String getRequestChildElementValue(Element parent, String name) {
        NodeList list = parent.getElementsByTagName(name);
        if (list == null || list.getLength() == 0) {
            return null;
        } else {
            return list.item(0).getFirstChild().getNodeValue();
        }
    }
}

/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.extern.lib.tld;

import lombok.experimental.UtilityClass;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@UtilityClass
public class TldFunctionsParser {

    public static List<TldFunction> parse(InputStream input)
            throws ParserConfigurationException, IOException, SAXException {
        var nodes = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(input)
                .getDocumentElement()
                .getElementsByTagName("function");

        var len = nodes.getLength();
        var functions = new TldFunction[len];
        for (int i = 0; i < len; i++) {
            var element = (Element) nodes.item(i);
            functions[i] = constructFunction(element);
        }
        return List.of(functions);
    }

    private static TldFunction constructFunction(Element element) throws IOException {
        var name = requireChildValue(element, "name");
        var declaredClass = requireChildValue(element, "function-class");
        var signature = requireChildValue(element, "function-signature");

        final int lparenIdx = signature.indexOf('(');
        int methodNameEnd = lparenIdx;
        while (methodNameEnd >= 0) {
            if (signature.charAt(--methodNameEnd) > ' ') {
                methodNameEnd++;
                break;
            }
        }
        int returnTypeEndIndex = signature.lastIndexOf(' ', methodNameEnd - 1);
        var returnType = signature.substring(0, returnTypeEndIndex).trim();
        var methodName = signature.substring(returnTypeEndIndex + 1, methodNameEnd);

        String typesString =
                signature.substring(lparenIdx + 1, signature.lastIndexOf(')')).trim();
        List<String> paramTypes = !typesString.isEmpty()
                ? List.of(typesString.split("\\s*,\\s*"))
                : List.of();

        return TldFunction.builder()
                .name(name)
                .declaredClass(declaredClass)
                .returnType(returnType)
                .methodName(methodName)
                .parameterTypes(paramTypes)
                .build();
    }

    private static String requireChildValue(Element parent, String name) throws IOException {
        var list = parent.getElementsByTagName(name);
        if (list.getLength() == 0) {
            throw new IOException("No such child element named: " + name);
        }
        return list.item(0)
                .getFirstChild()
                .getNodeValue()
                .trim();
    }
}

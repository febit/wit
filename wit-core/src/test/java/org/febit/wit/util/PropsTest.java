// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author zqq90
 */
public class PropsTest {

    @Test
    public void test() {
        final Props props = new Props();

        props.load(("name= first name \n"
                + "empty=\n"
                + "empty2= \t  \n"
                + "[user]\n"
                + "name=user ${name}\r"
                + "list=item1,item2,''',item3\n"
                + "list2='''\nitem4,',item5,'',item6\n'''\n"
                + "list3='''item4,item5\nitem6'''\r\n"
                + "[user2]\r\n"
                + "name=user2\\\\${ $name\r"
                + "[user.dept]\n"
                + "name=${user.name} dept name\n"
                + "[]\n"
                + "list2='''\nitem7,item9''\n'\n\n"
                + "").toCharArray());

        Map<String, String> map = new HashMap<>();

        props.extractTo(map);

        assertEquals("first name", map.get("name"));
        assertEquals("", map.get("empty"));
        assertEquals("", map.get("empty2"));
        assertEquals("user first name", map.get("user.name"));
        assertEquals("item1,item2,''',item3", map.get("user.list"));
        assertEquals("\nitem4,',item5,'',item6\n", map.get("user.list2"));
        assertEquals("item4,item5\nitem6", map.get("user.list3"));
        assertEquals("user2${ $name", map.get("user2.name"));
        assertEquals("user first name dept name", map.get("user.dept.name"));
        assertEquals("\nitem7,item9''\n'\n\n", map.get("list2"));
    }

}

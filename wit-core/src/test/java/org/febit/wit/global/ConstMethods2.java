package org.febit.wit.global;

import java.util.Map;

/**
 *
 * @author zqq90
 */
public class ConstMethods2 {
    
    public String const2Foo(String obj) {
        return "const2Foo";
    }
    public String const2Member(String obj) {
        return "const2MemberString";
    }

    public String const2Member(Object obj) {
        return "const2MemberObject";
    }

    public int const2Size(String obj) {
        return obj.length();
    }

    public int const2Size(Map obj) {
        return obj.size();
    }

    public int const2Size(Object[] arr) {
        return arr.length;
    }

}

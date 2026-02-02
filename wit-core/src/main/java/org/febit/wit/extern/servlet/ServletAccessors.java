package org.febit.wit.extern.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.experimental.UtilityClass;
import org.febit.wit.accessor.AccessorConsumer;
import org.febit.wit.extern.servlet.accessor.*;
import org.febit.wit.extern.servlet.facade.HttpServletRequestAttributes;
import org.febit.wit.extern.servlet.facade.HttpServletRequestHeader;
import org.febit.wit.extern.servlet.facade.HttpServletRequestHeaders;
import org.febit.wit.extern.servlet.facade.HttpServletRequestParameters;

@UtilityClass
public class ServletAccessors {

    public static void registerAll(AccessorConsumer consumer){
        consumer.accept(HttpServletRequest.class, new HttpServletRequestAccessor());
        consumer.accept(HttpServletRequestAttributes.class, new HttpServletRequestAttributesAccessor());
        consumer.accept(HttpServletRequestHeader.class, new HttpServletRequestHeaderAccessor());
        consumer.accept(HttpServletRequestParameters.class, new HttpServletRequestParametersAccessor());
        consumer.accept(HttpServletRequestHeaders.class, new HttpServletRequestHeadersAccessor());
        consumer.accept(HttpSession.class, new HttpSessionAccessor());
    }
}

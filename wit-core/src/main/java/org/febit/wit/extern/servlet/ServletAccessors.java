package org.febit.wit.extern.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.experimental.UtilityClass;
import org.febit.wit.extern.servlet.accessor.HttpServletRequestAccessor;
import org.febit.wit.extern.servlet.accessor.HttpServletRequestAttributesAccessor;
import org.febit.wit.extern.servlet.accessor.HttpServletRequestHeaderAccessor;
import org.febit.wit.extern.servlet.accessor.HttpServletRequestHeadersAccessor;
import org.febit.wit.extern.servlet.accessor.HttpServletRequestParametersAccessor;
import org.febit.wit.extern.servlet.accessor.HttpSessionAccessor;
import org.febit.wit.extern.servlet.facade.HttpServletRequestAttributes;
import org.febit.wit.extern.servlet.facade.HttpServletRequestHeader;
import org.febit.wit.extern.servlet.facade.HttpServletRequestHeaders;
import org.febit.wit.extern.servlet.facade.HttpServletRequestParameters;
import org.febit.wit.runtime.accessor.AccessorConsumer;

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

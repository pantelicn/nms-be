package com.opdev.common.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.experimental.UtilityClass;

/**
 * Useful when working with a {@link HttpServletRequest} or
 * {@link HttpServletResponse}
 */
@UtilityClass
public class AppRequestResponseUtils {

    /**
     * Retrieves the current {@link HttpServletRequest}
     * 
     * @return the current {@link HttpServletRequest}
     */
    public static HttpServletRequest getCurrentRequest() {
        final ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes();
        return servletRequestAttributes.getRequest();
    }

    /**
     * Retrieves the current {@link HttpServletResponse}
     * 
     * @return the current {@link HttpServletResponse}
     */
    public static HttpServletResponse getCurrentResponse() {
        final ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes();
        return servletRequestAttributes.getResponse();
    }

}

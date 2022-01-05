package com.opdev.common.events;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opdev.common.utils.AppRequestResponseUtils;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * The interface inherits the {@link ApplicationListener} and delegates the
 * provided generic <code>E</code> parameter. It provides a couple of
 * convenience methods that are available to sub-classes.
 *
 * @param <E>
 */
public interface CommonApplicationEventListener<E extends ApplicationEvent> extends ApplicationListener<E> {

    /**
     * Retrieves the current {@link HttpServletRequest}
     * 
     * @return the current {@link HttpServletRequest}
     */
    default HttpServletRequest getRequest() {
        return AppRequestResponseUtils.getCurrentRequest();
    }

    /**
     * Retrieves the current {@link HttpServletResponse}
     * 
     * @return the current {@link HttpServletResponse}
     */
    default HttpServletResponse getResponse() {
        return AppRequestResponseUtils.getCurrentResponse();
    }

}
package com.opdev.common.events;

import org.springframework.context.ApplicationEvent;

public class ResourceUpdatedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ApplicationEvent.
     *
     * @param source
     *                   the object on which the event initially occurred (never
     *                   {@code null})
     */
    public ResourceUpdatedEvent(final Object source) {
        super(source);
    }
}

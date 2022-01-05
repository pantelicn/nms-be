package com.opdev.common.events;

import java.util.Objects;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * This event should be fired by a controller when a new resource is created.
 */
@Getter
public class ResourceCreatedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    /** a new resource's ID */
    private final Long newResourcesId;

    /**
     * Creates a new ApplicationEvent.
     *
     * @param source
     *                           the object on which the event initially occurred
     *                           (never {@code null})
     * @param newResourcesId
     *                           a new resource's ID
     */
    public ResourceCreatedEvent(final Object source, final Long newResourcesId) {
        super(source);
        this.newResourcesId = Objects.requireNonNull(newResourcesId);
    }

}
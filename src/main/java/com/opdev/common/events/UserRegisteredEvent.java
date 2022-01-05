package com.opdev.common.events;

import com.opdev.model.user.User;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * This event should be fired by a controller when a new user (talent or a
 * company) is registered.
 */
@Getter
public class UserRegisteredEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;
    private final User user;

    public UserRegisteredEvent(final Object source, final User user) {
        super(source);
        this.user = user;
    }

}
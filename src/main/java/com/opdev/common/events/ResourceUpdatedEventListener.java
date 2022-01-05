package com.opdev.common.events;

import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 *
 * Triggered when the {@link ResourceUpdatedEvent} is fired.
 *
 * Sets {@link HttpStatus#OK} code to the response.
 *
 */
@Component
class ResourceUpdatedEventListener implements CommonApplicationEventListener<ResourceUpdatedEvent> {

    @Override
    public void onApplicationEvent(final ResourceUpdatedEvent event) {
        Objects.requireNonNull(event);

        final HttpServletResponse response = getResponse();
        response.setStatus(HttpStatus.OK.value());
    }

}
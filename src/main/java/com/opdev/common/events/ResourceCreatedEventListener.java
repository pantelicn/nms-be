package com.opdev.common.events;

import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * Event listener triggered when the {@link ResourceCreatedEvent} is fired.
 *
 * Creates a location header and sets {@link HttpStatus#CREATED} code to a
 * response.
 *
 */
@Slf4j
@Component
class ResourceCreatedEventListener implements CommonApplicationEventListener<ResourceCreatedEvent> {

    @Override
    public void onApplicationEvent(final ResourceCreatedEvent event) {
        Objects.requireNonNull(event);

        final String uriLocation = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(event.getNewResourcesId()).toUriString(); // a shortcut to encode() and toUriString()

        LOGGER.info("Created a resource that's available at the location: {}", uriLocation);

        final HttpServletResponse response = getResponse();
        response.setHeader(HttpHeaders.LOCATION, uriLocation);
        response.setStatus(HttpStatus.CREATED.value());
    }

}
package com.opdev.config;

import com.opdev.exception.ApiBadRequestException;
import com.opdev.exception.ApiCompanyAlreadySubscribedException;
import com.opdev.exception.ApiContactEditValidationException;
import com.opdev.exception.ApiEmailExistsException;
import com.opdev.exception.ApiEntityDisabledException;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.exception.ApiErrorCodes;
import com.opdev.exception.ApiSkillBadStatusException;
import com.opdev.exception.ApiUnauthorizedException;
import com.opdev.exception.ApiVerificationTokenExpiredException;
import com.opdev.exception.ApiVerificationTokenInvalidException;
import com.opdev.exception.dto.ApiErrorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
class ApiControllerAdvice extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ApiContactEditValidationException.class)
    ResponseEntity<?> handleApiContactEditValidationException(final ApiContactEditValidationException e) {
        final String message = resolveMessage(e.getMessage());
        final HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        final ApiErrorDto apiError = ApiErrorDto.builder().message(message).build();

        return logAndSendResponse(new ResponseEntity<>(apiError, responseStatus), e);
    }

    @ExceptionHandler(ApiBadRequestException.class)
    ResponseEntity<?> handleApiBadRequestException(final ApiBadRequestException e) {
        final String message = resolveMessage(e.getMessage());
        final HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        final ApiErrorDto apiError = ApiErrorDto.builder().message(message).build();

        return logAndSendResponse(new ResponseEntity<>(apiError, responseStatus), e);
    }

    @ExceptionHandler(ApiCompanyAlreadySubscribedException.class)
    ResponseEntity<?> handleApiCompanyAlreadySubscribedException(final ApiCompanyAlreadySubscribedException e) {
        final String message = resolveMessage(e.getMessage());
        final HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        final ApiErrorDto apiError = ApiErrorDto.builder().message(message).build();

        return logAndSendResponse(new ResponseEntity<>(apiError, responseStatus), e);
    }

    @ExceptionHandler(ApiEmailExistsException.class)
    ResponseEntity<?> handleApiEmailExistsException(final ApiEmailExistsException e) {
        final HttpStatus responseStatus = HttpStatus.CREATED;

        return logAndSendResponse(new ResponseEntity<>(HttpEntity.EMPTY, responseStatus), e);
    }

    @ExceptionHandler(ApiEntityDisabledException.class)
    ResponseEntity<?> handleApiEntityDisabledException(final ApiEntityDisabledException e) {
        final String message = resolveMessage(e.getMessage(), e.getId(), e.getEntity());
        final HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        final ApiErrorDto apiError = ApiErrorDto.builder().message(message).build();

        return logAndSendResponse(new ResponseEntity<>(apiError, responseStatus), e);
    }

    @ExceptionHandler(ApiEntityNotFoundException.class)
    ResponseEntity<?> handleApiEntityNotFountException(final ApiEntityNotFoundException e) {
        final String message = resolveMessage(e.getMessage(), e.getId(), e.getEntity());
        final HttpStatus responseStatus = HttpStatus.NOT_FOUND;
        final ApiErrorDto apiError = ApiErrorDto.builder().message(message).build();

        return logAndSendResponse(new ResponseEntity<>(apiError, responseStatus), e);
    }

    @ExceptionHandler(ApiUnauthorizedException.class)
    ResponseEntity<?> handleApiUserNotLoggedException(final ApiUnauthorizedException e) {
        final String message = resolveMessage(e.getMessage());
        final HttpStatus responseStatus = HttpStatus.UNAUTHORIZED;
        final ApiErrorDto apiError = ApiErrorDto.builder().message(message).build();

        return logAndSendResponse(new ResponseEntity<>(apiError, responseStatus), e);
    }

    @ExceptionHandler(ApiVerificationTokenExpiredException.class)
    ResponseEntity<?> handleApiVerificationTokenExpiredException(final ApiVerificationTokenExpiredException e) {
        return handleVerificationTokenExceptions(e, e.getToken());
    }

    @ExceptionHandler(ApiVerificationTokenInvalidException.class)
    ResponseEntity<?> handleApiVerificationTokenInvalidException(final ApiVerificationTokenInvalidException e) {
        return handleVerificationTokenExceptions(e, e.getToken());
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<?> handleAccessDeniedException(final AccessDeniedException e) {
        final ApiErrorDto apiError = ApiErrorDto.builder().message(e.getMessage()).build();
        return logAndSendResponse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError), e);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> handleException(final Exception e) {
        final String message = resolveMessage(ApiErrorCodes.INTERNAL_SERVER_ERROR);
        final HttpStatus responseStatus = getHttpStatus(e).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        final ApiErrorDto apiError = ApiErrorDto.builder().message(message).build();

        return logAndSendResponse(new ResponseEntity<>(apiError, responseStatus), e);
    }

    @ExceptionHandler(ApiSkillBadStatusException.class)
    ResponseEntity<?> handleBadSkillStatusException(final ApiSkillBadStatusException e) {
        final String message = resolveMessage(e.getMessage());
        final ApiErrorDto apiError = ApiErrorDto.builder().message(message).build();

        return logAndSendResponse(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError), e);
    }

    protected <E extends Exception> ResponseEntity<?> handleVerificationTokenExceptions(final E e, final String token) {
        Objects.requireNonNull(e);
        Objects.requireNonNull(token);

        final HttpStatus responseStatus = HttpStatus.UNAUTHORIZED;

        final HttpHeaders headers = new HttpHeaders();
        final URI locationUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/userverification")
                .replaceQueryParam("token", token).build().encode().toUri();
        headers.setLocation(locationUri);

        final String message = resolveMessage(e.getMessage());
        final ApiErrorDto apiError = ApiErrorDto.builder().message(message).build();

        return logAndSendResponse(new ResponseEntity<>(apiError, headers, responseStatus), e);
    }

    protected String resolveMessage(final String messageCode, final Object... args) {
        Objects.requireNonNull(messageCode);

        final Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(messageCode, args, messageCode, locale);
    }

    protected ResponseEntity<?> logAndSendResponse(final ResponseEntity<?> response, final Throwable t) {
        Objects.requireNonNull(response);

        LOGGER.error("Handling exception:", t);
        LOGGER.error("Responding with: {}", response);
        return response;
    }

    /**
     * Tries to extract a {@link ResponseStatus} annotation from the passed
     * exception. If it finds the annotation, an {@link Optional} with the HTTP
     * status code from the annotation is returned. If there's no annotation, an
     * empty {@link Optional} is returned.
     *
     * @param exception an exception from which to extract {@link ResponseStatus}
     *                  annotation
     * @param <E>       any subclass of the {@link Exception}
     * @return an {@link Optional}
     */
    protected <E extends Exception> Optional<HttpStatus> getHttpStatus(final E exception) {
        Objects.requireNonNull(exception);

        final Class<? extends Exception> clazz = exception.getClass();
        final ResponseStatus responseStatusAnnotation = AnnotationUtils.getAnnotation(clazz, ResponseStatus.class);

        final HttpStatus status = responseStatusAnnotation != null ? responseStatusAnnotation.code() : null;
        return Optional.ofNullable(status);
    }
}

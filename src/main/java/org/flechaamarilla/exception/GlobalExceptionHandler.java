package org.flechaamarilla.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler to provide consistent error responses
 */
@Provider
@Slf4j
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    /**
     * Error response structure
     */
    public static class ErrorResponse {
        public String message;
        public String type;

        public ErrorResponse(String message, String type) {
            this.message = message;
            this.type = type;
        }
    }

    @Override
    public Response toResponse(Exception exception) {
        log.error("Unhandled exception", exception);

        // Determine appropriate status code based on exception type
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;

        if (exception instanceof IllegalArgumentException) {
            status = Response.Status.BAD_REQUEST;
        }

        // Create a structured error response
        ErrorResponse errorResponse = new ErrorResponse(
                exception.getMessage(),
                exception.getClass().getSimpleName()
        );

        return Response
                .status(status)
                .entity(errorResponse)
                .build();
    }
}
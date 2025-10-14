package com.web.recommendationservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid input", HttpStatus.BAD_REQUEST),

    USER_NOT_EXISTED(1004, "User not found", HttpStatus.NOT_FOUND),
    MOVIE_NOT_EXISTED(1004, "Movie not found", HttpStatus.NOT_FOUND),
    
    UNAUTHENTICATED(1006, "Authentication failed", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    
    NO_USER_HISTORY(1014, "No user history found to generate recommendations", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_DATA(1015, "Insufficient data to generate recommendations", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}

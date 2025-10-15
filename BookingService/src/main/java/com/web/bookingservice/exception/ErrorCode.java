package com.web.bookingservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid input", HttpStatus.BAD_REQUEST),

    USER_EXISTED(1002, "User already exists", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1002, "Email already exists", HttpStatus.BAD_REQUEST),

    INVALID_USERNAME(1003, "Username must be at least 4 characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1003, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1004, "Invalid or expired OTP", HttpStatus.BAD_REQUEST),

    USER_NOT_EXISTED(1004, "User not found", HttpStatus.NOT_FOUND),
    MOVIE_NOT_EXISTED(1004, "Movie not found", HttpStatus.NOT_FOUND),
    ACTOR_NOT_EXISTED(1004, "Actor not found", HttpStatus.NOT_FOUND),
    GENRE_NOT_EXISTED(1004, "Genre not found", HttpStatus.NOT_FOUND),
    ROOM_NOT_EXISTED(1004, "Room not found", HttpStatus.NOT_FOUND),
    CINEMA_NOT_EXISTED(1004, "Cinema not found", HttpStatus.NOT_FOUND),
    SHOWTIME_NOT_EXISTED(1004, "Showtime not found", HttpStatus.NOT_FOUND),
    SEAT_NOT_EXISTED(1004, "Seat not found", HttpStatus.NOT_FOUND),
    INVOICE_NOT_EXISTED(1004, "Invoice not found", HttpStatus.NOT_FOUND),
    TICKET_NOT_EXISTED(1004, "Ticket not found", HttpStatus.NOT_FOUND),

    UNAUTHENTICATED(1006, "Authentication failed", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    PERMISSION_NOT_EXISTED(1008, "Permission not found", HttpStatus.NOT_FOUND),
    USER_NOT_ACTIVE(1009, "User account is not active", HttpStatus.FORBIDDEN),

    TICKET_ALREADY_SCANNED(1010, "Ticket has already been scanned", HttpStatus.BAD_REQUEST),
    INVALID_QR_CODE(1011, "Invalid QR code", HttpStatus.BAD_REQUEST),
    TICKET_NOT_ACTIVE(1012, "Ticket is not active", HttpStatus.BAD_REQUEST),
    TICKET_NOT_READY(1013, "Ticket is not ready", HttpStatus.BAD_REQUEST),

    USERNAME_NOT_BLANK(9000, "Username cannot be blank", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_BLANK(9000, "Password cannot be blank", HttpStatus.BAD_REQUEST),
    FIRST_NAME_NOT_BLANK(9000, "First name cannot be blank", HttpStatus.BAD_REQUEST),
    LAST_NAME_NOT_BLANK(9000, "Last name cannot be blank", HttpStatus.BAD_REQUEST),
    TITLE_NOT_BLANK(9000, "Title cannot be blank", HttpStatus.BAD_REQUEST),
    SEAT_NAME_NOT_BLANK(9000, "Seat name cannot be blank", HttpStatus.BAD_REQUEST),
    CINEMA_NAME_NOT_BLANK(9000, "Cinema name cannot be blank", HttpStatus.BAD_REQUEST),
    ADDRESS_NOT_BLANK(9000, "Address cannot be blank", HttpStatus.BAD_REQUEST),
    ROOM_NAME_NOT_BLANK(9000, "Room name cannot be blank", HttpStatus.BAD_REQUEST),

    ROOM_ID_NOT_NULL(9000, "Room ID cannot be null", HttpStatus.BAD_REQUEST),
    SHOWTIME_ID_NOT_NULL(9000, "Showtime ID cannot be null", HttpStatus.BAD_REQUEST),
    CINEMA_ID_NOT_NULL(9000, "Cinema ID cannot be null", HttpStatus.BAD_REQUEST),
    MOVIE_ID_NOT_NULL(9000, "Movie ID cannot be null", HttpStatus.BAD_REQUEST),
    START_TIME_NOT_NULL(9000, "Start time cannot be null", HttpStatus.BAD_REQUEST),
    DURATION_NOT_NULL(9000, "Duration cannot be null", HttpStatus.BAD_REQUEST),

    DURATION_MIN(9000, "Duration must be at least 1 minute", HttpStatus.BAD_REQUEST),
    TOTAL_SEATS_MIN(9000, "Total seats must be at least 1", HttpStatus.BAD_REQUEST),
    SEAT_SELECTION_REQUIRED(9000, "At least one seat must be selected", HttpStatus.BAD_REQUEST)    ;


    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    public static ErrorCode fromMessage(String message) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getMessage().equals(message)) {
                return errorCode;
            }
        }
        return ErrorCode.UNCATEGORIZED_EXCEPTION;
    }
}

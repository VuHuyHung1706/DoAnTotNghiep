package com.web.movieservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Dữ liệu nhập không hợp lệ", HttpStatus.BAD_REQUEST),

    USER_EXISTED(1002, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1002, "Email đã tồn tại", HttpStatus.BAD_REQUEST),

    INVALID_USERNAME(1003, "Tên đăng nhập phải có ít nhất 4 ký tự", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1003, "Mật khẩu phải có ít nhất 8 ký tự", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1004, "Mã OTP không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST),

    USER_NOT_EXISTED(1004, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    MOVIE_NOT_EXISTED(1004, "Không tìm thấy phim", HttpStatus.NOT_FOUND),
    ACTOR_NOT_EXISTED(1004, "Không tìm thấy diễn viên", HttpStatus.NOT_FOUND),
    GENRE_NOT_EXISTED(1004, "Không tìm thấy thể loại", HttpStatus.NOT_FOUND),
    ROOM_NOT_EXISTED(1004, "Không tìm thấy phòng chiếu", HttpStatus.NOT_FOUND),
    CINEMA_NOT_EXISTED(1004, "Không tìm thấy rạp chiếu", HttpStatus.NOT_FOUND),
    SHOWTIME_NOT_EXISTED(1004, "Không tìm thấy suất chiếu", HttpStatus.NOT_FOUND),
    SEAT_NOT_EXISTED(1004, "Không tìm thấy ghế", HttpStatus.NOT_FOUND),
    INVOICE_NOT_EXISTED(1004, "Không tìm thấy hóa đơn", HttpStatus.NOT_FOUND),
    TICKET_NOT_EXISTED(1004, "Không tìm thấy vé", HttpStatus.NOT_FOUND),
    REVIEW_NOT_EXISTED(1004, "Không tìm thấy đánh giá", HttpStatus.NOT_FOUND),

    UNAUTHENTICATED(1006, "Xác thực thất bại", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "Bạn không có quyền thực hiện hành động này", HttpStatus.FORBIDDEN),
    PERMISSION_NOT_EXISTED(1008, "Không tìm thấy quyền truy cập", HttpStatus.NOT_FOUND),
    USER_NOT_ACTIVE(1009, "Tài khoản người dùng chưa được kích hoạt", HttpStatus.FORBIDDEN),

    TICKET_ALREADY_SCANNED(1010, "Vé đã được quét trước đó", HttpStatus.BAD_REQUEST),
    INVALID_QR_CODE(1011, "Mã QR không hợp lệ", HttpStatus.BAD_REQUEST),
    TICKET_NOT_ACTIVE(1012, "Vé chưa được kích hoạt", HttpStatus.BAD_REQUEST),
    REVIEW_ALREADY_EXISTS(1013, "Bạn đã đánh giá phim này rồi", HttpStatus.BAD_REQUEST),
    CANNOT_UPDATE_DEFAULT_REVIEW(1014, "Không thể cập nhật đánh giá mặc định", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_DEFAULT_REVIEW(1015, "Không thể xóa đánh giá mặc định", HttpStatus.BAD_REQUEST),

    CANNOT_DELETE_MOVIE_HAS_SHOWTIMES(1020, "Không thể xóa phim vì còn suất chiếu liên quan", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_MOVIE_HAS_REVIEWS(1021, "Không thể xóa phim vì còn đánh giá liên quan", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_SHOWTIME_HAS_TICKETS(1022, "Không thể xóa suất chiếu vì đã có vé được đặt", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_ACTOR_HAS_MOVIES(1023, "Không thể xóa diễn viên vì còn phim liên quan", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_GENRE_HAS_MOVIES(1024, "Không thể xóa thể loại vì còn phim liên quan", HttpStatus.BAD_REQUEST),

    FILE_EMPTY(1016, "File không được để trống", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(1017, "Định dạng file không hợp lệ. Chỉ chấp nhận file ảnh", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(1018, "Kích thước file vượt quá giới hạn 5MB", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED(1019, "Upload file thất bại", HttpStatus.INTERNAL_SERVER_ERROR),

    USERNAME_NOT_BLANK(9000, "Tên đăng nhập không được để trống", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_BLANK(9000, "Mật khẩu không được để trống", HttpStatus.BAD_REQUEST),
    FIRST_NAME_NOT_BLANK(9000, "Tên không được để trống", HttpStatus.BAD_REQUEST),
    LAST_NAME_NOT_BLANK(9000, "Họ không được để trống", HttpStatus.BAD_REQUEST),
    TITLE_NOT_BLANK(9000, "Tiêu đề không được để trống", HttpStatus.BAD_REQUEST),
    SEAT_NAME_NOT_BLANK(9000, "Tên ghế không được để trống", HttpStatus.BAD_REQUEST),
    CINEMA_NAME_NOT_BLANK(9000, "Tên rạp không được để trống", HttpStatus.BAD_REQUEST),
    ADDRESS_NOT_BLANK(9000, "Địa chỉ không được để trống", HttpStatus.BAD_REQUEST),
    ROOM_NAME_NOT_BLANK(9000, "Tên phòng chiếu không được để trống", HttpStatus.BAD_REQUEST),

    ROOM_ID_NOT_NULL(9000, "Mã phòng chiếu không được để trống", HttpStatus.BAD_REQUEST),
    SHOWTIME_ID_NOT_NULL(9000, "Mã suất chiếu không được để trống", HttpStatus.BAD_REQUEST),
    CINEMA_ID_NOT_NULL(9000, "Mã rạp chiếu không được để trống", HttpStatus.BAD_REQUEST),
    MOVIE_ID_NOT_NULL(9000, "Mã phim không được để trống", HttpStatus.BAD_REQUEST),
    START_TIME_NOT_NULL(9000, "Thời gian bắt đầu không được để trống", HttpStatus.BAD_REQUEST),
    DURATION_NOT_NULL(9000, "Thời lượng không được để trống", HttpStatus.BAD_REQUEST),
    RATING_NOT_NULL(9000, "Đánh giá không được để trống", HttpStatus.BAD_REQUEST),
    RATING_MIN(9000, "Đánh giá phải ít nhất là 1 sao", HttpStatus.BAD_REQUEST),
    RATING_MAX(9000, "Đánh giá tối đa là 5 sao", HttpStatus.BAD_REQUEST),

    DURATION_MIN(9000, "Thời lượng phải ít nhất 1 phút", HttpStatus.BAD_REQUEST),
    TOTAL_SEATS_MIN(9000, "Số lượng ghế phải ít nhất là 1", HttpStatus.BAD_REQUEST),
    SEAT_SELECTION_REQUIRED(9000, "Phải chọn ít nhất một ghế", HttpStatus.BAD_REQUEST),

    SHOWTIME_CONFLICTING(9100, "Ca chiếu bị xung đột với một ca chiếu khác", HttpStatus.BAD_REQUEST),
    SHOWTIME_INVALID_TIME(9100, "Thời gian của phim phải nhỏ hơn khoảng thời gian bắt đầu và kết thúc ca chiếu", HttpStatus.BAD_REQUEST),
    SHOWTIME_INVALID(9100, "Phim chưa phát hành, không thể tạo ca chiếu", HttpStatus.BAD_REQUEST);

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

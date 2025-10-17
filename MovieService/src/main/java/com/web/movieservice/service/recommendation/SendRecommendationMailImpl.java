package com.web.movieservice.service.recommendation;

import com.web.movieservice.dto.response.ApiResponse;
import com.web.movieservice.dto.response.CustomerResponse;
import com.web.movieservice.dto.response.RoomResponse;
import com.web.movieservice.dto.response.UserRecommendationResponse;
import com.web.movieservice.entity.Showtime;
import com.web.movieservice.exception.AppException;
import com.web.movieservice.exception.ErrorCode;
import com.web.movieservice.repository.client.CinemaServiceClient;
import com.web.movieservice.repository.client.UserServiceClient;
import com.web.movieservice.service.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Slf4j
@Service
public class SendRecommendationMailImpl implements SendRecommendationMail {
    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MailService mailService;

    @Autowired
    private CinemaServiceClient cinemaServiceClient;

    @Override
    @Async
    public void startAsyncTask(LinkedList<UserRecommendationResponse> users, Showtime showtime) {
        log.info("[INFO] Bắt đầu thực thi tác vụ gửi recommendation mails. Thread: " + Thread.currentThread().getName());
        ApiResponse<RoomResponse> roomResponseApiResponse = cinemaServiceClient.getRoomById(showtime.getRoomId());

        if (roomResponseApiResponse.getCode() != 1000)
        {
            throw new AppException(ErrorCode.fromMessage(roomResponseApiResponse.getMessage()));
        }
        try {
            RoomResponse roomResponse = roomResponseApiResponse.getResult();

            for (UserRecommendationResponse user : users) {
                try
                {
                    ApiResponse<CustomerResponse> customerResponseApiResponse = userServiceClient.getCustomerByUserName(user.getUsername());
                    CustomerResponse customerResponse = customerResponseApiResponse.getResult();
                    mailService.sendMovieRecommendationEmail(customerResponse.getEmail(), showtime.getMovie(), showtime, roomResponse);
//                    mailService.sendMail(customerResponse.getEmail(), "[HHK Cinema] Một phim mới sắp được chiếu tại rạp tôi muốn đề xuất cho bạn", "test");
                } catch (Exception e) {
                    log.error("[ERROR] Can't send a recommendation to " + user.getUsername() + "/n" + e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("[ERROR] " + Thread.currentThread().getName() + " đã bị ngắt: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}

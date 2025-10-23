package com.web.movieservice.service.recommendation;

import com.web.movieservice.dto.response.ApiResponse;
import com.web.movieservice.dto.response.CustomerResponse;
import com.web.movieservice.dto.response.RoomResponse;
import com.web.movieservice.dto.response.UserRecommendationResponse;
import com.web.movieservice.entity.Showtime;
import com.web.movieservice.exception.AppException;
import com.web.movieservice.exception.ErrorCode;
import com.web.movieservice.repository.client.CinemaServiceClient;
import com.web.movieservice.repository.client.RecommendationServiceClient;
import com.web.movieservice.repository.client.UserServiceClient;
import com.web.movieservice.service.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SendRecommendationMailImpl implements SendRecommendationMail {
    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MailService mailService;

    @Autowired
    private CinemaServiceClient cinemaServiceClient;

    @Autowired
    RecommendationServiceClient recommendationServiceClient;

    @Override
    @Async
    public void startAsyncTask(Integer movieId, Showtime showtime) {
        log.info("[INFO] Bắt đầu thực thi tác vụ gửi recommendation mails. Thread: " + Thread.currentThread().getName());
        log.info("-------STEP1: Lấy danh sách user để đề xuất phim. Thread: " + Thread.currentThread().getName() + "----------");
        ApiResponse<List<UserRecommendationResponse>> userRecommendationResponse = recommendationServiceClient.getUsersForMovie(movieId);

        if (userRecommendationResponse.getCode() != 1000) {
            throw new AppException(ErrorCode.fromMessage(userRecommendationResponse.getMessage()));
        }
        ApiResponse<RoomResponse> roomResponseApiResponse = cinemaServiceClient.getRoomById(showtime.getRoomId());
        if (roomResponseApiResponse.getCode() != 1000)
        {
            throw new AppException(ErrorCode.fromMessage(roomResponseApiResponse.getMessage()));
        }

        log.info("-------STEP2: Gửi một movie recommendation mail cho user. Thread: " + Thread.currentThread().getName() + "----------");
        try {
            LinkedList<UserRecommendationResponse> users =  userRecommendationResponse.getResult().stream().collect(Collectors.toCollection(LinkedList::new));
            RoomResponse roomResponse = roomResponseApiResponse.getResult();

            for (UserRecommendationResponse user : users) {
                try
                {
                    ApiResponse<CustomerResponse> customerResponseApiResponse = userServiceClient.getCustomerByUserName(user.getUsername());
                    CustomerResponse customerResponse = customerResponseApiResponse.getResult();
                    mailService.sendMovieRecommendationEmail(customerResponse.getEmail(), showtime.getMovie(), showtime, roomResponse);
                } catch (Exception e) {
                    log.error("[ERROR] Can't send a recommendation to " + user.getUsername() + "/n" + e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("[ERROR] " + Thread.currentThread().getName() + " đã bị ngắt: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        log.info("[INFO] Kết thúc thực thi tác vụ gửi recommendation mails. Thread: " + Thread.currentThread().getName());
    }
}

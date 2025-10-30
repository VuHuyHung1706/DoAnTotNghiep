package com.web.movieservice.mapper;

import com.web.movieservice.dto.request.ReviewRequest;
import com.web.movieservice.dto.response.ReviewResponse;
import com.web.movieservice.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "isDefault", ignore = true)
    Review toReview(ReviewRequest request);

    ReviewResponse toReviewResponse(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "movieId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "isDefault", ignore = true)
    void updateReview(@MappingTarget Review review, ReviewRequest request);
}

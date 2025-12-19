package com.web.userservice.mapper;


import com.web.userservice.dto.request.StaffRegistrationRequest;
import com.web.userservice.dto.request.UpdateManagerProfileRequest;
import com.web.userservice.dto.request.UpdateStaffRequest;
import com.web.userservice.dto.response.ManagerResponse;
import com.web.userservice.entity.Manager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ManagerMapper {

    @Mapping(source = "account.username", target = "username")
    @Mapping(source = "account.status", target = "status")
    ManagerResponse toManagerResponse(Manager manager);

    Manager toManager(StaffRegistrationRequest request);

    void updateManager(@MappingTarget Manager manager, UpdateStaffRequest request);

    void updateManagerProfile(@MappingTarget Manager manager, UpdateManagerProfileRequest request);
}

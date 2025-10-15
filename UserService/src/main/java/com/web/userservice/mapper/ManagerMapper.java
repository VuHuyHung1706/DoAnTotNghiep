package com.web.userservice.mapper;


import com.web.userservice.dto.response.ManagerResponse;
import com.web.userservice.entity.Manager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ManagerMapper {

    @Mapping(source = "account.username", target = "username")
    @Mapping(source = "account.status", target = "status")
    ManagerResponse toManagerResponse(Manager manager);
}

package com.web.userservice.mapper;


import com.web.userservice.dto.response.CustomerResponse;
import com.web.userservice.dto.resquest.UpdateProfileRequest;
import com.web.userservice.dto.resquest.UserRegistrationRequest;
import com.web.userservice.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toCustomer(UserRegistrationRequest request);

    @Mapping(source = "account.username", target = "username")
    @Mapping(source = "account.status", target = "status")
    CustomerResponse toCustomerResponse(Customer customer);

    void updateCustomer(@MappingTarget Customer customer, UpdateProfileRequest request);
}

package com.web.userservice.service.account;

import com.web.userservice.dto.request.StaffRegistrationRequest;
import com.web.userservice.dto.request.UpdateStaffRequest;
import com.web.userservice.dto.response.CustomerResponse;
import com.web.userservice.dto.response.ManagerResponse;
import com.web.userservice.dto.resquest.ChangePasswordRequest;
import com.web.userservice.dto.resquest.UpdateProfileRequest;
import com.web.userservice.dto.resquest.UserRegistrationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    CustomerResponse registerUser(UserRegistrationRequest request);
    void sendOtp(UserRegistrationRequest request);
    CustomerResponse completeRegistration(UserRegistrationRequest request, String otp);
    void changePassword(ChangePasswordRequest request);
    CustomerResponse updateProfile(UpdateProfileRequest request);
    CustomerResponse getMyProfile();
    ManagerResponse getManager();
    CustomerResponse getCustomerByUsername(String username);
    CustomerResponse getCustomerById(Integer id);
    void resetPassword(String username);

    Page<CustomerResponse> getAllCustomers(Pageable pageable);
    CustomerResponse createCustomer(UserRegistrationRequest request);
    void deleteCustomer(String username);
    void updateCustomerPassword(String username, String newPassword);
    CustomerResponse updateCustomer(String username, UpdateProfileRequest request);
    Page<CustomerResponse> searchCustomers(String keyword, Pageable pageable);

    Page<ManagerResponse> getAllStaff(Pageable pageable);
    Page<ManagerResponse> searchStaff(String keyword, Pageable pageable);
    ManagerResponse createStaff(StaffRegistrationRequest request);
    ManagerResponse updateStaff(String username, UpdateStaffRequest request);
    void deleteStaff(String username);
    void updateStaffPassword(String username, String newPassword);
}

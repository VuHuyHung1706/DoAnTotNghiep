package com.web.userservice.service.account;

import com.web.userservice.dto.response.CustomerResponse;
import com.web.userservice.dto.response.ManagerResponse;
import com.web.userservice.dto.resquest.ChangePasswordRequest;
import com.web.userservice.dto.resquest.UpdateProfileRequest;
import com.web.userservice.dto.resquest.UserRegistrationRequest;
import com.web.userservice.entity.Account;
import com.web.userservice.entity.Customer;
import com.web.userservice.entity.Manager;
import com.web.userservice.exception.AppException;
import com.web.userservice.exception.ErrorCode;
import com.web.userservice.mapper.CustomerMapper;
import com.web.userservice.mapper.ManagerMapper;
import com.web.userservice.repository.AccountRepository;
import com.web.userservice.repository.CustomerRepository;
import com.web.userservice.repository.ManagerRepository;
import com.web.userservice.service.mail.MailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ManagerMapper managerMapper;

    @Autowired
    private MailService mailService;

//    @Override
//    public CustomerResponse registerUser(UserRegistrationRequest request) {
//        if (accountRepository.existsByUsername(request.getUsername())) {
//            throw new AppException(ErrorCode.USER_EXISTED);
//        }
//
//        if (request.getEmail() != null && customerRepository.existsByEmail(request.getEmail())) {
//            throw new AppException(ErrorCode.EMAIL_EXISTED);
//        }
//
//        mailService.sendMail(request.getEmail());
//
//        Account account = Account.builder()
//                .username(request.getUsername())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .status(true)
//                .build();
//
//        account = accountRepository.save(account);
//
//        Customer customer = customerMapper.toCustomer(request);
//        customer.setAccount(account);
//
//        customer = customerRepository.save(customer);
//
//        return customerMapper.toCustomerResponse(customer);
//    }

    @Override
    public void sendOtp(UserRegistrationRequest request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (request.getEmail() != null && customerRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // Generate and send OTP
        mailService.sendMail(request.getEmail());
    }

    @Override
    public CustomerResponse completeRegistration(UserRegistrationRequest request, String otp) {
        // Verify OTP
        boolean isOtpValid = mailService.verifyOtp(request.getEmail(), otp);
        if (!isOtpValid) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        Account account = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(true)
                .build();

        account = accountRepository.save(account);

        Customer customer = customerMapper.toCustomer(request);
        customer.setAccount(account);

        customer = customerRepository.save(customer);

        return customerMapper.toCustomerResponse(customer);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);
    }

    @Override
    public CustomerResponse updateProfile(UpdateProfileRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        Customer customer = customerRepository.findByAccountUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getEmail() != null && !request.getEmail().equals(customer.getEmail()) && customerRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        customerMapper.updateCustomer(customer, request);
        customer = customerRepository.save(customer);

        return customerMapper.toCustomerResponse(customer);
    }

    @Override
    public CustomerResponse getMyProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        Customer customer = customerRepository.findByAccountUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return customerMapper.toCustomerResponse(customer);
    }

    @Override
    public ManagerResponse getManager() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Manager manager = managerRepository.findByAccountUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return managerMapper.toManagerResponse(manager);
    }
}

package com.web.userservice.service.account;

import com.web.userservice.dto.request.StaffRegistrationRequest;
import com.web.userservice.dto.request.UpdateStaffRequest;
import com.web.userservice.dto.response.CustomerResponse;
import com.web.userservice.dto.response.ManagerResponse;
import com.web.userservice.dto.resquest.ChangePasswordRequest;
import com.web.userservice.dto.resquest.UpdateProfileRequest;
import com.web.userservice.dto.resquest.UserRegistrationRequest;
import com.web.userservice.entity.Account;
import com.web.userservice.entity.Customer;
import com.web.userservice.entity.Manager;
import com.web.userservice.enums.Position;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

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

    @Override
    public CustomerResponse registerUser(UserRegistrationRequest request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (request.getEmail() != null && customerRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

//        mailService.sendMail(request.getEmail());

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

    @Override
    public CustomerResponse getCustomerByUsername(String username) {
        Customer customer = customerRepository.findByAccountUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return customerMapper.toCustomerResponse(customer);
    }

    @Override
    public CustomerResponse getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return customerMapper.toCustomerResponse(customer);
    }

    @Override
    public void resetPassword(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        account.setPassword(passwordEncoder.encode("12345678"));
        accountRepository.save(account);
    }

    @Override
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        Page<Customer> customers = customerRepository.findAll(pageable);
        return customers.map(customerMapper::toCustomerResponse);
    }

    @Override
    public CustomerResponse createCustomer(UserRegistrationRequest request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (request.getEmail() != null && customerRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
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
    public void deleteCustomer(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        account.setStatus(false);
        accountRepository.save(account);
    }

    @Override
    public void updateCustomerPassword(String username, String newPassword) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    @Override
    public CustomerResponse updateCustomer(String username, UpdateProfileRequest request) {
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
    public Page<CustomerResponse> searchCustomers(String keyword, Pageable pageable) {
        Page<Customer> customers = customerRepository.searchCustomers(keyword, pageable);
        return customers.map(customerMapper::toCustomerResponse);
    }

    @Override
    public Page<ManagerResponse> getAllStaff(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Manager currentManager = managerRepository.findByAccountUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Position> allowedPositions;
        if (currentManager.getPosition() == Position.ADMIN) {
            allowedPositions = Arrays.asList(Position.MANAGER, Position.STAFF);
        } else if (currentManager.getPosition() == Position.MANAGER) {
            allowedPositions = Arrays.asList(Position.STAFF);
        } else {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Page<Manager> staff = managerRepository.findAllByPositionIn(allowedPositions, pageable);
        return staff.map(managerMapper::toManagerResponse);
    }

    @Override
    public Page<ManagerResponse> searchStaff(String keyword, Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Manager currentManager = managerRepository.findByAccountUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Position> allowedPositions;
        if (currentManager.getPosition() == Position.ADMIN) {
            allowedPositions = Arrays.asList(Position.MANAGER, Position.STAFF);
        } else if (currentManager.getPosition() == Position.MANAGER) {
            allowedPositions = Arrays.asList(Position.STAFF);
        } else {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Page<Manager> staff = managerRepository.searchStaffByPositions(keyword, allowedPositions, pageable);
        return staff.map(managerMapper::toManagerResponse);
    }

    @Override
    public ManagerResponse createStaff(StaffRegistrationRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Manager currentManager = managerRepository.findByAccountUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (currentManager.getPosition() == Position.MANAGER && request.getPosition() != Position.STAFF) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (request.getEmail() != null && managerRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        Account account = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(true)
                .build();

        account = accountRepository.save(account);

        Manager manager = managerMapper.toManager(request);
        manager.setAccount(account);

        manager = managerRepository.save(manager);

        return managerMapper.toManagerResponse(manager);
    }

    @Override
    public ManagerResponse updateStaff(String username, UpdateStaffRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Manager currentManager = managerRepository.findByAccountUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Manager targetStaff = managerRepository.findByAccountUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (currentManager.getPosition() == Position.MANAGER) {
            if (targetStaff.getPosition() != Position.STAFF || request.getPosition() != Position.STAFF) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

        if (request.getEmail() != null && !request.getEmail().equals(targetStaff.getEmail())
                && managerRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        managerMapper.updateManager(targetStaff, request);
        targetStaff = managerRepository.save(targetStaff);

        return managerMapper.toManagerResponse(targetStaff);
    }

    @Override
    public void deleteStaff(String username) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Manager currentManager = managerRepository.findByAccountUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Manager targetStaff = managerRepository.findByAccountUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (currentManager.getPosition() == Position.MANAGER && targetStaff.getPosition() != Position.STAFF) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        account.setStatus(false);
        accountRepository.save(account);
    }

    @Override
    public void updateStaffPassword(String username, String newPassword) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Manager currentManager = managerRepository.findByAccountUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Manager targetStaff = managerRepository.findByAccountUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (currentManager.getPosition() == Position.MANAGER && targetStaff.getPosition() != Position.STAFF) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }
}

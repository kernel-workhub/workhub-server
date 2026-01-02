package com.workhub.userTable.service.user;

import com.workhub.global.error.ErrorCode;
import com.workhub.global.error.exception.BusinessException;
import com.workhub.global.security.CustomUserDetails;
import com.workhub.userTable.dto.user.request.UserLoginRecord;
import com.workhub.userTable.dto.user.response.LoginResult;
import com.workhub.userTable.dto.user.response.UserDetailResponse;
import com.workhub.userTable.dto.user.response.UserLoginResponse;
import com.workhub.userTable.dto.user.response.UserNameResponse;
import com.workhub.userTable.entity.Status;
import com.workhub.userTable.entity.UserRole;
import com.workhub.userTable.entity.UserTable;
import com.workhub.userTable.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void save(UserTable user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserTable> getUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getUser(Long userId){
        UserTable userTable = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTS));
        return UserDetailResponse.from(userTable);

    }

    @Transactional
    public UserTable getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTS));
    }

    @Transactional(readOnly = true)
    public LoginResult login(UserLoginRecord userLoginRecord) {
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                userLoginRecord.loginId(),
                userLoginRecord.password()
        );

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authRequest);
        } catch (AuthenticationException exception) {
            throw new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
        }

        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        UserTable user = getUserById(userId);
        UserLoginResponse loginResponse = UserLoginResponse.from(user);

        return new LoginResult(authentication, loginResponse);
    }

    @Transactional(readOnly = true)
    public void validateLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_USER);
        }
    }

    @Transactional(readOnly = true)
    public void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS__EMAIL);
        }
    }

    @Transactional(readOnly = true)
    public Map<Long, UserTable> getUserMapByUserIdIn(List<Long> userIds) {
        return userRepository.findMapByUserIdIn(userIds);
    }

    @Transactional(readOnly = true)
    public List<UserNameResponse> getUserMapByCompanyIdIn(Long companyId) {

        List<UserTable> userNames = userRepository.findMapByCompanyIdIn(companyId);

        return userNames.stream()
                .map(UserNameResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Long countActiveUsers(){
        return userRepository.countByStatus(Status.ACTIVE);
    }

    @Transactional(readOnly = true)
    public Long countActiveUsersUntil(LocalDateTime monthEnd) {
        return userRepository.countActiveUsersUntil(monthEnd);
    }

    /**
     * 특정 회사의 특정 역할 및 상태를 가진 사용자 수 조회
     *
     * @param companyId 회사 ID
     * @param role 사용자 역할
     * @param status 사용자 상태
     * @return 조건에 맞는 사용자 수
     */
    @Transactional(readOnly = true)
    public Long countByCompanyIdAndRoleAndStatus(Long companyId, UserRole role, Status status) {
        return userRepository.countByCompanyIdAndRoleAndStatus(companyId, role, status);
    }
}

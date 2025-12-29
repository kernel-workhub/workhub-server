package com.workhub.userTable.service.user;

import com.workhub.global.error.ErrorCode;
import com.workhub.global.error.exception.BusinessException;
import com.workhub.global.util.SecurityUtil;
import com.workhub.userTable.dto.email.EmailVerificationConfirmRequest;
import com.workhub.userTable.dto.user.request.AdminPasswordResetRequest;
import com.workhub.userTable.dto.user.request.UpdatePhoneRequest;
import com.workhub.userTable.dto.user.request.UserPasswordChangeRequest;
import com.workhub.userTable.dto.user.response.UserTableResponse;
import com.workhub.userTable.entity.UserRole;
import com.workhub.userTable.entity.UserTable;
import com.workhub.userTable.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UpdateUserService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    public void updatePhone(UpdatePhoneRequest request) {

        Long loginUser = SecurityUtil.getCurrentUserIdOrThrow();
        UserTable user = userService.getUserById(loginUser);

        user.updatePhone(request.phone());
    }

    public void verifyCodeAndUpdateEmail(EmailVerificationConfirmRequest request) {

        boolean verifyCode = emailVerificationService.verifyCode(request.email(), request.code());
        if (!verifyCode) {
            throw new BusinessException(ErrorCode.NOT_EQUAL_CODE);
        }

        UserTable user = userService.getUserById(SecurityUtil.getCurrentUserIdOrThrow());
        user.updateEmail(request.email());
    }

    public void changePassword(Long targetUserId, UserPasswordChangeRequest passwordChangeRequest) {

        UserTable userTable = getUserById(targetUserId);

        if (!passwordEncoder.matches(passwordChangeRequest.currentPassword(), userTable.getPassword())) {
            throw new BusinessException(ErrorCode.NOT_EQUAL_PASSWORD);
        }

        userTable.updatePassword(passwordEncoder.encode(passwordChangeRequest.newPassword()));
    }


    public void resetPassword(Long targetUserId, AdminPasswordResetRequest passwordResetRequest) {

        UserTable userTable = getUserById(targetUserId);
        userTable.updatePassword(passwordEncoder.encode(passwordResetRequest.newPassword()));
    }

    public UserTableResponse updateRole(Long userId, UserRole role) {

        UserTable userTable = getUserById(userId);
        userTable.updateRole(role);

        return UserTableResponse.from(userTable);
    }

    private UserTable getUserById(Long userId) {
        return userService.getUserById(userId);
    }
}

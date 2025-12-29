package com.workhub.userTable.service.user;

import com.workhub.userTable.dto.user.request.UserRegisterRecord;
import com.workhub.userTable.entity.UserTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreateUserService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserTable register(UserRegisterRecord request) {

        validateLoginIdAndEmail(request.loginId(), request.email());

        UserTable user = UserTable.of(request, passwordEncoder.encode(request.password()));
        userService.save(user);

        return user;
    }

    private void validateLoginIdAndEmail(String loginId, String email) {
        userService.validateLoginId(loginId);
        userService.validateEmail(email);
    }
}

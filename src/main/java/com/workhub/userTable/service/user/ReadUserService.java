package com.workhub.userTable.service.user;

import com.workhub.userTable.dto.user.request.UserLoginRecord;
import com.workhub.userTable.dto.user.response.LoginResult;
import com.workhub.userTable.dto.user.response.UserDetailResponse;
import com.workhub.userTable.dto.user.response.UserListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadUserService {

    private final UserService userService;

    public List<UserListResponse> getUsers() {
        return userService.getUsers().stream()
                .map(UserListResponse::from)
                .toList();
    }

    public UserDetailResponse getUser(Long userId){
        return  userService.getUser(userId);
    }

    public LoginResult login(UserLoginRecord userLoginRecord) {
        return userService.login(userLoginRecord);
    }
}

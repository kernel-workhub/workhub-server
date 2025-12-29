package com.workhub.userTable.service.user;

import com.workhub.userTable.entity.Status;
import com.workhub.userTable.entity.UserTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeleteUserService {

    private final UserService userService;

    public void deleteUser(Long userId) {
        UserTable userTable = getUserById(userId);
        userTable.updateStatus(Status.INACTIVE);
    }

    private UserTable getUserById(Long userId){
        return userService.getUserById(userId);
    }
}

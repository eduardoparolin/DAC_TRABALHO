package com.dac.auth.service.interfaces;

import com.dac.auth.dto.user.UserCreateDTO;
import com.dac.auth.dto.user.UserDTO;
import com.dac.auth.dto.user.UserUpdateDTO;
import com.dac.auth.model.User;

public interface UserService {
    UserDTO save(UserCreateDTO user);
    User findById(String id);
    User findByEmail(String email);
    UserDTO update(UserUpdateDTO user, String id, String requesterId);
    void delete(String id, String requesterId);
}

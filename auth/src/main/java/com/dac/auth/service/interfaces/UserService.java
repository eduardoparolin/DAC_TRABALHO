package com.dac.auth.service.interfaces;

import com.dac.auth.model.User;

public interface UserService {
    User save(User user);
    User findById(String id);
    User findByEmail(String email);
    User update(User user, String id);
    void delete(String id);
}

package com.dac.auth.service;

import com.dac.auth.model.User;
import com.dac.auth.infra.repository.UserRepository;
import com.dac.auth.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User save(User user) {
        User existingUser = findByEmail(user.getEmail());
        if(Objects.nonNull(existingUser)) {
            System.out.println("Email em uso");
        }
        return repository.save(user);
    }

    @Override
    public User findById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    @Override
    public User update(User user, String id) {
        User updated = repository.findById(id).orElse(null);
        assert updated != null;
        updated.setEmail(user.getEmail());
        return repository.save(user);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    private User getById(String id) {
        Optional<User> user = repository.findById(id);
        if(user.isEmpty()) {
            throw new RuntimeException("Não encontrado");
        }
        return user.get();
    }
}

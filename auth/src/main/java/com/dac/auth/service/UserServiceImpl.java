package com.dac.auth.service;

import com.dac.auth.dto.user.UserCreateDTO;
import com.dac.auth.dto.user.UserDTO;
import com.dac.auth.dto.user.UserUpdateDTO;
import com.dac.auth.enums.Role;
import com.dac.auth.exception.custom.ApiException;
import com.dac.auth.infra.configuration.security.AuthenticationService;
import com.dac.auth.model.User;
import com.dac.auth.infra.repository.UserRepository;
import com.dac.auth.service.interfaces.AuthenticationFacade;
import com.dac.auth.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final AuthenticationFacade authentication;

    @Override
    public UserDTO save(UserCreateDTO dto) {
        User existingUser = findByEmail(dto.getEmail());
        if(Objects.nonNull(existingUser)) {
            throw new ApiException("Email em uso.", HttpStatus.BAD_REQUEST);
        }

        User createUser = new User(
                dto.getId(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getRole()
        );

        return UserDTO.fromEntity(repository.save(createUser));
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
    public UserDTO update(UserUpdateDTO dto, String id, String requesterId) {
        User existingUser = findById(id);

        if(Objects.isNull(existingUser)) {
            throw new ApiException("Usuario não encontrado.", HttpStatus.BAD_REQUEST);
        }

        User requester = findById(requesterId);

        if(Role.ADMIN.equals(requester.getRole()) && Objects.nonNull(dto.getRole())) {
            throw new ApiException("Apenas admnistradores podem alterar roles", HttpStatus.FORBIDDEN);
        }

        User updateUser = updateValidFields(existingUser, dto);

        return UserDTO.fromEntity(repository.save(updateUser));
    }

    @Override
    public void delete(String id) {
        getById(id);
        repository.deleteById(id);
    }

    private User getById(String id) {
        Optional<User> user = repository.findById(id);
        if(user.isEmpty()) {
            throw new ApiException("Usuario não encontrado.", HttpStatus.BAD_REQUEST);
        }
        return user.get();
    }

    private User updateValidFields(User user, UserUpdateDTO dto) {
        if(validField(dto.getPassword())) {
            user.setPassword(dto.getPassword());
        }

        if(validField(dto.getRole().toString())) {
          user.setRole(dto.getRole());
        }

        return user;
    }

    private boolean validField(String field) {
        return !Objects.isNull(field) && !field.isEmpty();
    }
}

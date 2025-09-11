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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final AuthenticationFacade authentication;

    @Override
    public UserDTO save(UserCreateDTO dto) {
        log.info("Criando usuario");
        User existingUser = repository.findById(dto.getId()).orElse(null);
        if(Objects.nonNull(existingUser)) {
            throw new ApiException("Usuario com ID já existente.", HttpStatus.BAD_REQUEST);
        }

        existingUser = repository.findByEmail(dto.getEmail()).orElse(null);
        if(Objects.nonNull(existingUser)) {
            throw new ApiException("Email em uso.", HttpStatus.BAD_REQUEST);
        }

        User createUser = new User(
                dto.getId(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getRole()
        );

        log.info("Usuario criado com sucesso");
        return UserDTO.fromEntity(repository.save(createUser));
    }

    @Override
    public User findById(String id) {
        log.info("Buscando usuario com ID {}", id);
        return repository.findById(id).orElseThrow(() -> new ApiException("Usuario com id "+id+" não encontrado", HttpStatus.NOT_FOUND));
    }

    @Override
    public User findByEmail(String email) {
        log.info("Buscando usuario com Email {}", email);
        return repository.findByEmail(email).orElseThrow(() -> new ApiException("Usuario com email "+email+" não encontrado", HttpStatus.NOT_FOUND));
    }

    @Override
    public UserDTO update(UserUpdateDTO dto, String id, String requesterId) {
        log.info("Atualizando usuario");
        User existingUser = findById(id);

        if(Objects.isNull(existingUser)) {
            throw new ApiException("Usuario não encontrado.", HttpStatus.BAD_REQUEST);
        }

        User requester = findById(requesterId);

        if(Role.ADMIN.equals(requester.getRole()) && Objects.nonNull(dto.getRole())) {
            throw new ApiException("Apenas admnistradores podem alterar roles", HttpStatus.FORBIDDEN);
        }

        User updateUser = updateValidFields(existingUser, dto);

        log.info("Usuario atualizado com sucesso");
        return UserDTO.fromEntity(repository.save(updateUser));
    }

    @Override
    public void delete(String id, String requesterId) {
        log.info("Excluindo usuario");
        if(requesterId.equals(id)) {
            throw new ApiException("Usuário não pode deletar a si mesmo", HttpStatus.BAD_REQUEST);
        }
        findById(id);
        repository.deleteById(id);
        log.info("Usuario deletado com sucesso");
    }

    private User updateValidFields(User user, UserUpdateDTO dto) {
        if(validField(dto.getPassword())) {
            user.setPassword(dto.getPassword());
        }

        if(Objects.nonNull(dto.getRole()) && validField(dto.getRole().toString())) {
          user.setRole(dto.getRole());
        }

        return user;
    }

    private boolean validField(String field) {
        return !Objects.isNull(field) && !field.isEmpty();
    }
}

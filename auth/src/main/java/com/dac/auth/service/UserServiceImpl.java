package com.dac.auth.service;

import com.dac.auth.dto.user.UserCreateDTO;
import com.dac.auth.dto.user.UserDTO;
import com.dac.auth.dto.user.UserUpdateDTO;
import com.dac.auth.enums.Role;
import com.dac.auth.exception.custom.ApiException;
import com.dac.auth.infra.configuration.security.AuthenticationService;
import com.dac.auth.infra.email.EmailService;
import com.dac.auth.infra.password.PasswordData;
import com.dac.auth.infra.password.PasswordGenerator;
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
    private final PasswordGenerator passwordGenerator;
    private final EmailService emailService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public UserDTO save(UserCreateDTO dto) {
        log.info("Criando usuario");
        User existingUser = repository.findByCpf(dto.getCpf()).orElse(null);
        if (Objects.nonNull(existingUser)) {
            throw new ApiException("Usuario com CPF já existente.", HttpStatus.BAD_REQUEST);
        }

        existingUser = repository.findByEmail(dto.getEmail()).orElse(null);
        if (Objects.nonNull(existingUser)) {
            throw new ApiException("Email em uso.", HttpStatus.BAD_REQUEST);
        }

        // Generate temporary password - will be replaced during approval
        PasswordData passwordData = generateRandomPassword();
        // Note: Email is NOT sent here - it will be sent during approval with the final password

        User createUser = User.builder()
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .role(dto.getRole())
                .password(passwordData.encoded())
                .name(dto.getName())
                .build();

        createUser.setUserId(dto.getId());

        log.info("Usuario criado com sucesso com senha temporária");
        return UserDTO.fromEntity(repository.save(createUser));
    }

    @Override
    public User findByCpf(String cpf) {
        log.info("Buscando usuario com cpf {}", cpf);
        return repository.findByCpf(cpf).orElseThrow(
                () -> new ApiException("Usuario com cpf " + cpf + " não encontrado", HttpStatus.NOT_FOUND));
    }

    @Override
    public User findById(Long id, Role role) {
        log.info("Buscando usuario com id {}", id);
        if (Role.CLIENTE.equals(role)) {
            return repository.findByClientId(id).orElseThrow(
                    () -> new ApiException("Cliente com id " + id + " não encontrado", HttpStatus.NOT_FOUND));
        }
        return repository.findByManagerId(id)
                .orElseThrow(() -> new ApiException("Manager com id " + id + " não encontrado", HttpStatus.NOT_FOUND));
    }

    @Override
    public User findByEmail(String email) {
        log.info("Buscando usuario com Email {}", email);
        return repository.findByEmail(email).orElseThrow(
                () -> new ApiException("Usuario com email " + email + " não encontrado", HttpStatus.NOT_FOUND));
    }

    @Override
    public UserDTO update(UserUpdateDTO dto, String cpf) {
        log.info("Atualizando usuario");
        User existingUser = findByCpf(cpf);

        if (Objects.isNull(existingUser)) {
            throw new ApiException("Usuario não encontrado.", HttpStatus.BAD_REQUEST);
        }

        User updateUser = updateValidFields(existingUser, dto);

        log.info("Usuario atualizado com sucesso");
        return UserDTO.fromEntity(repository.save(updateUser));
    }

    @Override
    public void delete(Long id, Long requesterId, Role role) {
        log.info("Excluindo usuario");
//        if (requesterId == null) {
//            throw new ApiException("Identificador do solicitante é obrigatório.", HttpStatus.BAD_REQUEST);
//        }
//
//        if (requesterId.equals(id)) {
//            throw new ApiException("Usuário não pode deletar a si mesmo", HttpStatus.BAD_REQUEST);
//        }

        User user = findById(id, role);
        repository.delete(user);
        log.info("Usuario deletado com sucesso");
    }

    private User updateValidFields(User user, UserUpdateDTO dto) {
        if (validField(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }

        if (validField(dto.getName())) {
            user.setName(dto.getName());
        }

        if (validField(dto.getPassword())) {
            // Hash the provided password and update
            String encodedPassword = passwordEncoder.encode(dto.getPassword());
            user.setPassword(encodedPassword);
        }

        return user;
    }

    private boolean validField(String field) {
        return !Objects.isNull(field) && !field.isEmpty();
    }

    private PasswordData generateRandomPassword() {
        return passwordGenerator.generateRandomPassword();
    }

    @Override
    public UserDTO updatePassword(String cpf, String newPassword) {
        log.info("Atualizando senha do usuario com cpf {}", cpf);
        User user = findByCpf(cpf);

        if (Objects.isNull(user)) {
            throw new ApiException("Usuario não encontrado.", HttpStatus.NOT_FOUND);
        }

        // Generate new password data
        PasswordData passwordData = generateRandomPassword();

        // Update user password
        user.setPassword(passwordData.encoded());
        User savedUser = repository.save(user);

        // Send email with new password
        emailService.sendPasswordEmail(user.getName(), user.getEmail(), passwordData.raw());

        log.info("Senha atualizada com sucesso e email enviado");
        return UserDTO.fromEntity(savedUser);
    }
}

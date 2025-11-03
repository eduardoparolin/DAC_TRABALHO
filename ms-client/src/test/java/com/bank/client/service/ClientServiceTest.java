package com.bank.client.service;

import com.bank.client.dto.ClientRequest;
import com.bank.client.dto.ClientResponse;
import com.bank.client.entities.Client;
import com.bank.client.exception.DuplicateResourceException;
import com.bank.client.exception.NotFoundException;
import com.bank.client.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes de unidade do ClientService sem acessar banco.
 * Usa Mockito para isolar o repositório.
 *
 * Observação:
 * - Este teste não depende do tipo de retorno do create(...) para compilar;
 *   verificamos comportamento (chamada ao repo e normalização de CPF).
 * - Ajuste imports dos Exceptions se o pacote for diferente no seu projeto.
 */
@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository repo;

    @InjectMocks
    private ClientService service;

    private ClientRequest reqBase;

    @BeforeEach
    void setUp() {
        reqBase = new ClientRequest();
        // Ajuste os nomes dos campos conforme seu DTO atual:
        reqBase.setName("João da Silva");
        reqBase.setEmail("joao@exemplo.com");
        reqBase.setCpf("529.982.247-25"); // válido; service deve normalizar para 52998224725
        reqBase.setPhone("(41) 99999-0000");
        reqBase.setSalary(new BigDecimal("3500.00"));
        reqBase.setStreet("Rua A");
        reqBase.setNumber("123");
        reqBase.setComplement("Casa");
        reqBase.setZipCode("80000000");
        reqBase.setCity("Curitiba");
        reqBase.setState("PR");
    }

    @Test
    void create_deveLancarQuandoCpfJaExiste() {
        // Dado: repo indica CPF já existente (após normalização)
        when(repo.existsByCpf("52998224725")).thenReturn(true);

        // Quando + Então
        assertThrows(DuplicateResourceException.class, () -> service.create(reqBase));
        verify(repo, never()).save(any());
    }

    @Test
    void create_deveSalvarComCpfNormalizadoEEmailUnico() {
        // Dado: não existe CPF nem email duplicados
        when(repo.existsByCpf("52998224725")).thenReturn(false);
        when(repo.existsByEmail("joao@exemplo.com")).thenReturn(false);

        // Ao salvar, simulamos que o ID foi gerado
        when(repo.save(any(Client.class))).thenAnswer(inv -> {
            Client c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        // Quando: chamamos create
        // (não dependemos do retorno para o teste compilar)
        service.create(reqBase);

        // Então: o Client salvo deve ter CPF normalizado (apenas dígitos)
        ArgumentCaptor<Client> cap = ArgumentCaptor.forClass(Client.class);
        verify(repo).save(cap.capture());
        Client salvo = cap.getValue();
        assertEquals("52998224725", salvo.getCpf());
        assertEquals("João da Silva", salvo.getName());
        assertEquals("joao@exemplo.com", salvo.getEmail());
    }

    @Test
    void getById_deveRetornarClientResponseQuandoEncontrado() {
        // Dado: entidade existente no repositório
        Client e = new Client();
        e.setId(42L);
        e.setName("Ana");
        e.setEmail("ana@ex.com");
        e.setCpf("11122233344");
        e.setPhone("41999990000");
        e.setSalary(new BigDecimal("2500.00"));

        when(repo.findById(42L)).thenReturn(Optional.of(e));

        // Quando
        ClientResponse resp = service.getById(42L);

        // Então
        assertNotNull(resp);
        assertEquals(42L, resp.getId());
        assertEquals("Ana", resp.getName());
        assertEquals("11122233344", resp.getCpf());
        assertEquals("ana@ex.com", resp.getEmail());
    }

    @Test
    void getById_deveLancarNotFoundQuandoInexistente() {
        when(repo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getById(999L));
    }

    @Test
    void update_naoDevePermitirTrocarParaEmailJaUsadoPorOutro() {
        // Dado: existe um cliente com ID 7
        Client existente = new Client();
        existente.setId(7L);
        existente.setName("Mario");
        existente.setEmail("mario@ex.com");
        existente.setCpf("12312312300");
        existente.setPhone("41988887777");
        existente.setSalary(new BigDecimal("1000.00"));

        when(repo.findById(7L)).thenReturn(Optional.of(existente));

        // E o novo email já está tomado por outro registro
        ClientRequest req = cloneReq(reqBase);
        req.setEmail("email-ja-uso@ex.com");
        when(repo.existsByEmail("email-ja-uso@ex.com")).thenReturn(true);

        // Quando + Então
        assertThrows(DuplicateResourceException.class, () -> service.update(7L, req));
        verify(repo, never()).save(any());
    }

    // ==== helpers ====

    private static ClientRequest cloneReq(ClientRequest r) {
        ClientRequest c = new ClientRequest();
        c.setName(r.getName());
        c.setEmail(r.getEmail());
        c.setCpf(r.getCpf());
        c.setPhone(r.getPhone());
        c.setSalary(r.getSalary());
        c.setStreet(r.getStreet());
        c.setNumber(r.getNumber());
        c.setComplement(r.getComplement());
        c.setZipCode(r.getZipCode());
        c.setCity(r.getCity());
        c.setState(r.getState());
        return c;
    }
}

package com.bank.manager.controller;

import com.bank.manager.ManagerDTO;
import com.bank.manager.service.ManagerService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService  service;

    @GetMapping
    public ResponseEntity<List<ManagerDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // POST /gerentes
    @PostMapping
    public ResponseEntity<ManagerDTO> create(@RequestBody ManagerDTO dto) {
        ManagerDTO saved = service.create(dto);
        //Retorna status http 201 para resultado de sucesso criação
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<ManagerDTO> getByCpf(@PathVariable(name = "cpf") String cpf) {
        return ResponseEntity.ok(service.getByCpf(cpf));
    }

    // PUT /gerentes/{cpf}
    @PutMapping("/{cpf}")
    public ResponseEntity<ManagerDTO> update(@PathVariable String cpf, @RequestBody ManagerDTO dto) {
        return service.update(cpf,dto)
                .map(ResponseEntity::ok) // Se existe -> 200
                .orElse(ResponseEntity.notFound().build()); // se não existe -> 404
    }

    // DELETE /gerentes/{cpf}
    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> delete(@PathVariable String cpf) {
        service.deleteByCpf(cpf);
        // 204 No Content é apropriado para deleção bem-sucedida sem ccorpo
        return ResponseEntity.noContent().build();
    }
}
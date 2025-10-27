package com.bank.manager.controller;

import com.bank.manager.dto.ManagerDTO;
import com.bank.manager.dto.ManagerUpdateDTO;
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

    @PostMapping
    public ResponseEntity<ManagerDTO> create(@RequestBody ManagerDTO dto) {
        ManagerDTO saved = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<ManagerDTO> getByCpf(@PathVariable(name = "cpf") String cpf) {
        return ResponseEntity.ok(service.getByCpf(cpf));
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<ManagerDTO> update(@PathVariable String cpf, @RequestBody ManagerUpdateDTO dto) {
        return ResponseEntity.ok(service.update(cpf,dto));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> delete(@PathVariable String cpf) {
        service.deleteByCpf(cpf);
        return ResponseEntity.noContent().build();
    }
}
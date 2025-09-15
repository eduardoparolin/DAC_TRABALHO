package com.bank.manager.controller;

import com.bank.manager.model.Manager;
import com.bank.manager.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/managers")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @PostMapping
    public ResponseEntity<Manager> createManager(@RequestBody Manager manager) {
        Manager savedManager = managerService.save(manager);
        return new ResponseEntity<>(savedManager, HttpStatus.CREATED);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<Manager> getManagerByCpf(@PathVariable String cpf) {
        return managerService.findByCpf(cpf)
                .map(manager -> new ResponseEntity<>(manager, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Manager>> getAllManagers() {
        List<Manager> managers = managerService.findAll();
        return new ResponseEntity<>(managers, HttpStatus.OK);
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteManager(@PathVariable String cpf) {
        managerService.deleteByCpf(cpf);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/{cpf}")
    public ResponseEntity<Manager> getManagerByCpf(@PathVariable String cpf) {
        return managerService.findByCpf(cpf)
                .map(manager -> new ResponseEntity<>(manager, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Manager>> getAllManagers() {
        List<Manager> managers = managerService.findAll();
        return new ResponseEntity<>(managers, HttpStatus.OK);
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteManager(@PathVariable String cpf) {
        managerService.deleteByCpf(cpf);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/{cpf}")
    public ResponseEntity<Manager> getManagerByCpf(@PathVariable String cpf) {
        return managerService.findByCpf(cpf)
                .map(manager -> new ResponseEntity<>(manager, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Manager>> getAllManagers() {
        List<Manager> managers = managerService.findAll();
        return new ResponseEntity<>(managers, HttpStatus.OK);
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteManager(@PathVariable String cpf) {
        managerService.deleteByCpf(cpf);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/{cpf}")
    public ResponseEntity<Manager> getManagerByCpf(@PathVariable String cpf) {
        return managerService.findByCpf(cpf)
                .map(manager -> new ResponseEntity<>(manager, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Manager>> getAllManagers() {
        List<Manager> managers = managerService.findAll();
        return new ResponseEntity<>(managers, HttpStatus.OK);
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteManager(@PathVariable String cpf) {
        managerService.deleteByCpf(cpf);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
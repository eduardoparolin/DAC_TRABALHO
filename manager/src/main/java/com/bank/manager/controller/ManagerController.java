package com.bank.manager.controller;

import com.bank.manager.ManagerDTO;
import com.bank.manager.service.ManagerService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager") // base path para todas as rotas
public class ManagerController {

    private final ManagerService  service

    //Declarando direto no construtor para não usar @Autowired
    public ManagerController(ManagerService service) {
        this.service = service;
    }

    // GET /gerentes
    @GetMapping
    public ResponseEntity<List<ManagerDTO>> getAll() {
        // Delega função de pegar todos a service para retornar a lista de DTOs.
        return ResponseEntity.ok(service.findAll());
    }

    // POST /gerentes
    @PostMapping
    public ResponseEntity<ManagerDTO> create(@RequestBody ManagerDTO dto) {
        ManagerDTO saved = service.create(dto);
        //Retorna status http 201 para resultado de sucesso criação
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // GET /gerentes/${cpf}
    @GetMapping("/${cpf}")
    public ResponseEntity<ManagerDTO> getByCpf(@PathVariable String cpf) {
        // service.findByCpf retorna Optional<ManagerDTO> ((TODO: verificar o que é o Optional da service..))
        return service.findByCpf(cpf)
                .map(ResponseEntity::ok) // Se existe -> 200 + body
                .orElse(ResponseEntity.notFound().biuld()); // se não existe -> 404
    }

    // PUT /gerentes/${cpf}
    @PutMapping("/${cpf}")
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
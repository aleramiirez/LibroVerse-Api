package com.biblioteca.backend.controller;

import com.biblioteca.backend.model.Saga;
import com.biblioteca.backend.service.SagaServiceI;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sagas")
@Tag(name = "Sagas", description = "Operaciones para gestionar las sagas de libros")
public class SagaController {

    private final SagaServiceI sagaService;

    public SagaController(SagaServiceI sagaService) {
        this.sagaService = sagaService;
    }

    @GetMapping
    public ResponseEntity<List<Saga>> getAllSagas() {
        return ResponseEntity.ok(sagaService.getAllSagas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Saga> getSagaById(@PathVariable Long id) {
        return ResponseEntity.ok(sagaService.getSagaById(id));
    }

    @PostMapping
    public ResponseEntity<Saga> createSaga(@RequestBody Saga saga) {
        return ResponseEntity.ok(sagaService.createSaga(saga));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Saga> updateSaga(@PathVariable Long id, @RequestBody Saga saga) {
        return ResponseEntity.ok(sagaService.updateSaga(id, saga));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSaga(@PathVariable Long id) {
        sagaService.deleteSaga(id);
        return ResponseEntity.noContent().build();
    }
}

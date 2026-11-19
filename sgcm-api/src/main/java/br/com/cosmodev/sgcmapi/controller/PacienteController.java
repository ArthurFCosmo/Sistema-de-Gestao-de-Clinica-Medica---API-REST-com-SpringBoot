package br.com.cosmodev.sgcmapi.controller;

import br.com.cosmodev.sgcmapi.dtos.PacienteRequestDto;
import br.com.cosmodev.sgcmapi.dtos.PacienteResponseDto;
import br.com.cosmodev.sgcmapi.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paciente")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @PostMapping
    public ResponseEntity<PacienteResponseDto> salvarPaciente(@RequestBody @Valid PacienteRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.salvarPaciente(dto));
    }

    @GetMapping
    public ResponseEntity<Page<PacienteResponseDto>> listarPacientes(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable
    ) {
        return ResponseEntity.ok(pacienteService.listarPacientes(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDto> buscarPacientePorId(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.buscarPacientePorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPacientePorId(@PathVariable Long id) {
        pacienteService.deletarPaciente(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDto> atualizarPaciente(
            @PathVariable Long id,
            @RequestParam boolean ativo,
            @RequestBody @Valid PacienteRequestDto dto
    ) {
        return ResponseEntity.ok(pacienteService.atualizarPaciente(id, dto, ativo));
    }

}

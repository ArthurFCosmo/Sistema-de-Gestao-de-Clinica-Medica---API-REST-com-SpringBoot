package br.com.cosmodev.sgcmapi.controller;

import br.com.cosmodev.sgcmapi.dtos.EspecialidadeRequestDto;
import br.com.cosmodev.sgcmapi.dtos.EspecialidadeResponseDto;
import br.com.cosmodev.sgcmapi.service.EspecialidadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/especialidade")
@RequiredArgsConstructor
public class EspecialidadeController {

    private final EspecialidadeService especialidadeService;

    @PostMapping
    public ResponseEntity<EspecialidadeResponseDto> salvarEspecialidade(@RequestBody @Valid EspecialidadeRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(especialidadeService.salvarEspecialidade(dto));
    }

    @GetMapping
    public ResponseEntity<List<EspecialidadeResponseDto>> listarEspecalidades() {
        return ResponseEntity.ok(especialidadeService.listarEspecialidades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadeResponseDto> buscarEspecialidadePorId(@PathVariable Long id) {
        return ResponseEntity.ok(especialidadeService.buscarEspecialidadePorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEspecialidadePorId(@PathVariable Long id) {
        especialidadeService.deletarEspecialidade(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadeResponseDto> atualizarEspecialdiade(@PathVariable Long id, @RequestBody @Valid EspecialidadeRequestDto dto) {
        return ResponseEntity.ok(especialidadeService.atualizarEspecialidade(id, dto));
    }

}

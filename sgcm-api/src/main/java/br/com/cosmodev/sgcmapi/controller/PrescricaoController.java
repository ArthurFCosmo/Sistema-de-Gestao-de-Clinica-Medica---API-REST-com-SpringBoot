package br.com.cosmodev.sgcmapi.controller;

import br.com.cosmodev.sgcmapi.dtos.PrescricaoRequestDto;
import br.com.cosmodev.sgcmapi.dtos.PrescricaoResponseDto;
import br.com.cosmodev.sgcmapi.service.PrescricaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prescricao")
@RequiredArgsConstructor
public class PrescricaoController {

    private final PrescricaoService prescricaoService;

    @PostMapping
    public ResponseEntity<PrescricaoResponseDto> salvarPrescricao(@RequestBody @Valid PrescricaoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prescricaoService.salvarPrescricao(dto));
    }

    @GetMapping
    public ResponseEntity<List<PrescricaoResponseDto>> listarPrescricoes() {
        return ResponseEntity.ok(prescricaoService.listarPrescricoes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescricaoResponseDto> buscarPrescricaoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(prescricaoService.buscarPrescricaoPeloId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPrescricaoPorId(@PathVariable Long id) {
        prescricaoService.deletarPrescricaoPeloId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescricaoResponseDto> atualizarPrescricao(@PathVariable Long id, @RequestBody @Valid PrescricaoRequestDto dto) {
        return ResponseEntity.ok(prescricaoService.atualizarPrescricao(id, dto));
    }

}

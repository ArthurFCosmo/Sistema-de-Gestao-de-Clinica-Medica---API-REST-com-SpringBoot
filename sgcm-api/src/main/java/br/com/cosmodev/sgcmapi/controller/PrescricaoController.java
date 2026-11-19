package br.com.cosmodev.sgcmapi.controller;

import br.com.cosmodev.sgcmapi.dtos.PrescricaoRequestDto;
import br.com.cosmodev.sgcmapi.dtos.PrescricaoResponseDto;
import br.com.cosmodev.sgcmapi.service.PrescricaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<PrescricaoResponseDto>> listarPrescricoes(
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(prescricaoService.listarPrescricoes(pageable));
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
    public ResponseEntity<PrescricaoResponseDto> atualizarPrescricao(
        @PathVariable Long id, 
        @RequestBody @Valid PrescricaoRequestDto dto
    ) {
        return ResponseEntity.ok(prescricaoService.atualizarPrescricao(id, dto));
    }

    // ENDPOINTS DE CONSULTA PERSONALIZADA ------------------------------------------------------------------------------------------------

    @GetMapping("/consulta/{id}")
    public ResponseEntity<PrescricaoResponseDto> buscarPrescricaoPorConsultaId(@PathVariable("id") Long idConsulta) {
        return ResponseEntity.ok(prescricaoService.buscarPrescricaoPorConsultaId(idConsulta));
    }

}

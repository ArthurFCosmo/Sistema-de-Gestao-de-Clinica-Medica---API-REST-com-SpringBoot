package br.com.cosmodev.sgcmapi.controller;

import br.com.cosmodev.sgcmapi.dtos.ConsultaRequestDto;
import br.com.cosmodev.sgcmapi.dtos.ConsultaResponseDto;
import br.com.cosmodev.sgcmapi.enums.StatusConsulta;
import br.com.cosmodev.sgcmapi.service.ConsultaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/consulta")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService consultaService;

    @PostMapping
    public ResponseEntity<ConsultaResponseDto> salvarConsulta(@RequestBody @Valid ConsultaRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(consultaService.salvarConsulta(dto));
    }

    // @GetMapping
    // public ResponseEntity<List<ConsultaResponseDto>> listarConsultas() {
    //     return ResponseEntity.ok(consultaService.listarConsultas());
    // }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponseDto> buscarConsultaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.buscarConsultaPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarConsultaPorId(@PathVariable Long id) {
        consultaService.deletarConsultaPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponseDto> atualizarConsulta(
            @PathVariable Long id,
            @RequestParam StatusConsulta status,
            @RequestBody @Valid ConsultaRequestDto dto
    ) {
        return ResponseEntity.ok(consultaService.atualizarConsulta(id, dto, status));
    }

    
    // ENDPOINTS PERSONALIZADOS -------------------------------------------------------------------------------------------

    @GetMapping
    public ResponseEntity<Page<ConsultaResponseDto>> listarPersonalizado(
        @RequestParam(required = false) Long medicoId,
        @PageableDefault(size = 20, sort = "dataHora") Pageable pageable
    ) {

        return ResponseEntity.ok(consultaService.listarPersonalizado(medicoId, pageable));

    }
}

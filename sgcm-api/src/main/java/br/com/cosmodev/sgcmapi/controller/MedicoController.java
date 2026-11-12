package br.com.cosmodev.sgcmapi.controller;

import br.com.cosmodev.sgcmapi.dtos.MedicoRequestDto;
import br.com.cosmodev.sgcmapi.dtos.MedicoResponseDto;
import br.com.cosmodev.sgcmapi.service.MedicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/medico")
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoService medicoService;
    
    // ENDPOINTS DO CRUD -----------------------------------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<MedicoResponseDto> salvarMedico(@RequestBody @Valid MedicoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicoService.salvarMedico(dto));
    }

    // @GetMapping
    // public ResponseEntity<List<MedicoResponseDto>> listarMedicos() {
    //     return ResponseEntity.ok(medicoService.listarMedicos());
    // }

     @GetMapping("/{id}")
     public ResponseEntity<MedicoResponseDto> buscarMedicoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medicoService.buscarMedicoPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMedicoPorId(@PathVariable Long id) {
        medicoService.deletarMedico(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicoResponseDto> atualizarMedico(
            @PathVariable Long id,
            @RequestParam boolean ativo,
            @RequestBody @Valid MedicoRequestDto dto
    ) {
        return ResponseEntity.ok(medicoService.atualizarMedico(id, dto, ativo));
    }

    // ENDPOINTS PERSONALIZADOS -------------------------------------------------------------------------------------------


    // Get com parâmetros de filtragem e paginação
    @GetMapping
    public ResponseEntity<Page<MedicoResponseDto>> listarPersonalizado(
        @RequestParam(required = false) Long especialidadeId,
        @PageableDefault(size = 20, sort = "nome") Pageable pageable
    ) {

        return ResponseEntity.ok(medicoService.listarPersonalizado(especialidadeId, pageable));
        
    }
    
}

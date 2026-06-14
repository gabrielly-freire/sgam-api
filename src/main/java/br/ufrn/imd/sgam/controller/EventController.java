package br.ufrn.imd.sgam.controller;

import br.ufrn.imd.sgam.dto.EventDTO;
import br.ufrn.imd.sgam.dto.PresentationRequestDTO;
import br.ufrn.imd.sgam.service.EventService;
import br.ufrn.imd.sgam.service.PresentationRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/events")
@Tag(name = "Evento", description = "Gestão de eventos institucionais")
public class EventController {

    private final EventService service;
    private final PresentationRequestService presentationRequestService;

    @Operation(summary = "Cadastro de evento")
    @PostMapping
    public ResponseEntity<EventDTO> create(@Valid @RequestBody EventDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @Operation(summary = "Listagem cronológica de eventos")
    @GetMapping
    public ResponseEntity<Page<EventDTO>> list(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @Operation(summary = "Buscar evento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @Operation(summary = "Listar apresentações confirmadas para o evento")
    @GetMapping("/{id}/apresentacoes-confirmadas")
    public ResponseEntity<Page<PresentationRequestDTO>> listConfirmedPresentations(
            @PathVariable Long id,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(presentationRequestService.listConfirmedByEvent(id, pageable));
    }

    @Operation(summary = "Editar evento")
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> update(@PathVariable Long id, @Valid @RequestBody EventDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Cancelar (excluir) evento")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

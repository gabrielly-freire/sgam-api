package br.ufrn.imd.sgam.controller;

import br.ufrn.imd.sgam.dto.PresentationRequestCancelDTO;
import br.ufrn.imd.sgam.dto.PresentationRequestCreateDTO;
import br.ufrn.imd.sgam.dto.PresentationRequestDTO;
import br.ufrn.imd.sgam.model.UserInfo;
import br.ufrn.imd.sgam.service.PresentationRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/solicitacoes-apresentacao")
@Tag(name = "Solicitação de Apresentação", description = "Gestão de solicitações de apresentações")
public class PresentationRequestController {

    private final PresentationRequestService presentationRequestService;

    @Operation(summary = "Criação de solicitação de apresentação")
    @PostMapping
    public ResponseEntity<PresentationRequestDTO> create(
            @Valid @RequestBody PresentationRequestCreateDTO dto,
            @AuthenticationPrincipal UserInfo user
    ) {
        return ResponseEntity.ok(presentationRequestService.save(dto, user.getId()));
    }

    @Operation(summary = "Listagem de solicitações pendentes do coordenador autenticado")
    @GetMapping("/pendentes")
    public ResponseEntity<Page<PresentationRequestDTO>> listPendingByCoordinator(
            @PageableDefault Pageable pageable,
            @AuthenticationPrincipal UserInfo user
    ) {
        return ResponseEntity.ok(presentationRequestService.listPendingByCoordinator(user.getId(), pageable));
    }

    @Operation(summary = "Confirmação de solicitação de apresentação")
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<PresentationRequestDTO> confirm(
            @PathVariable Long id,
            @AuthenticationPrincipal UserInfo user
    ) {
        return ResponseEntity.ok(presentationRequestService.confirm(id, user.getId()));
    }

    @Operation(summary = "Cancelamento de solicitação de apresentação")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PresentationRequestDTO> cancel(
            @PathVariable Long id,
            @Valid @RequestBody PresentationRequestCancelDTO dto,
            @AuthenticationPrincipal UserInfo user
    ) {
        return ResponseEntity.ok(presentationRequestService.cancel(id, dto, user.getId()));
    }
}

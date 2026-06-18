package br.ufrn.imd.sgam.controller;

import br.ufrn.imd.sgam.dto.CancelPresentationRequestDTO;
import br.ufrn.imd.sgam.dto.ConfirmPresentationRequestDTO;
import br.ufrn.imd.sgam.dto.CreatePresentationRequestDTO;
import br.ufrn.imd.sgam.dto.PresentationRequestResponseDTO;
import br.ufrn.imd.sgam.model.UserInfo;
import br.ufrn.imd.sgam.service.PresentationRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Solicitações de Apresentação", description = "Endpoints para gerenciamento das solicitações de apresentação musical")
@RestController
@RequestMapping("/presentation-requests")
@RequiredArgsConstructor
public class PresentationRequestController {

        private final PresentationRequestService service;

        @Operation(summary = "Criar solicitação de apresentação", description = "Cria uma nova solicitação de apresentação para um evento.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Solicitação criada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
                        @ApiResponse(responseCode = "404", description = "Evento não encontrado")
        })
        @PostMapping
        public ResponseEntity<PresentationRequestResponseDTO> create(
                @Valid @RequestBody CreatePresentationRequestDTO dto,
                @RequestParam Long groupId, // Captura o ID do grupo da URL
                @RequestParam String time,   // Captura a data da URL
                @AuthenticationPrincipal UserInfo user) {

                        return ResponseEntity.ok(service.createComDadosExtras(dto, groupId, time, user));
        }

        @Operation(summary = "Confirmar solicitação", description = "Permite que o coordenador do grupo confirme uma solicitação de apresentação.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Solicitação confirmada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Grupo já possui apresentação no mesmo horário"),
                        @ApiResponse(responseCode = "403", description = "Usuário não é coordenador do grupo"),
                        @ApiResponse(responseCode = "404", description = "Solicitação ou grupo não encontrado")
        })
        @PatchMapping("/{id}/confirm")
        public ResponseEntity<Void> confirm(

                        @PathVariable Long id,

                        @Valid @RequestBody ConfirmPresentationRequestDTO dto,

                        @AuthenticationPrincipal UserInfo user) {

                service.confirm(id, dto, user);

                return ResponseEntity.noContent()
                                .build();
        }

        @Operation(summary = "Cancelar solicitação", description = "Cancela uma solicitação informando o motivo do cancelamento.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Solicitação cancelada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Motivo do cancelamento inválido"),
                        @ApiResponse(responseCode = "403", description = "Usuário não possui permissão para cancelar"),
                        @ApiResponse(responseCode = "404", description = "Solicitação não encontrada")
        })
        @PatchMapping("/{id}/cancel")
        public ResponseEntity<Void> cancel(

                        @PathVariable Long id,

                        @Valid @RequestBody CancelPresentationRequestDTO dto,

                        @AuthenticationPrincipal UserInfo user) {

                service.cancel(id, dto, user);

                return ResponseEntity.noContent()
                                .build();
        }

        @Operation(summary = "Listar solicitações pendentes", description = "Retorna todas as solicitações pendentes. Apenas coordenadores podem acessar.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso permitido apenas para coordenadores")
        })
        @GetMapping("/pending")
        public ResponseEntity<List<PresentationRequestResponseDTO>> pending(

                        @AuthenticationPrincipal UserInfo user) {

                return ResponseEntity.ok(
                                service.listPending(user));
        }

        @Operation(summary = "Listar minhas solicitações", description = "Retorna apenas as solicitações realizadas pelo usuário autenticado.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
        })
        @GetMapping("/my")
        public ResponseEntity<List<PresentationRequestResponseDTO>> myRequests(

                        @AuthenticationPrincipal UserInfo user) {

                return ResponseEntity.ok(
                                service.listMyRequests(user));
        }

        @Operation(summary = "Listar todas as solicitações", description = "Retorna todas as solicitações cadastradas. Apenas administradores podem acessar.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Apenas administradores podem acessar")
        })
        @GetMapping
        public ResponseEntity<List<PresentationRequestResponseDTO>> all(

                        @AuthenticationPrincipal UserInfo user) {

                return ResponseEntity.ok(
                                service.listAll(user));
        }

        @Operation(summary = "Listar solicitações confirmadas", description = "Retorna todas as solicitações confirmadas.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
        })
        @GetMapping("/confirmed")
        public ResponseEntity<List<PresentationRequestResponseDTO>> confirmed() {

                return ResponseEntity.ok(
                                service.listConfirmed());
        }
}
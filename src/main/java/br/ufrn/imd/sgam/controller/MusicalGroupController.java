package br.ufrn.imd.sgam.controller;

import br.ufrn.imd.sgam.dto.MusicalGroupDTO;
import br.ufrn.imd.sgam.dto.SolicitacaoVinculoDTO;
import br.ufrn.imd.sgam.model.UserInfo;
import br.ufrn.imd.sgam.service.MusicalGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/grupos-musicais")
@Tag(name = "Grupo Musical", description = "Gerenciamento de grupos musicais")
public class MusicalGroupController {

    private final MusicalGroupService musicalGroupService;

    @Operation(summary = "Criação de um grupo musical")
    @ApiResponses(value = {
            @ApiResponse(description = "Grupo musical criado com sucesso.", responseCode = "201"),
            @ApiResponse(description = "Requisição mal formatada", responseCode = "400"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @PostMapping
    public ResponseEntity<MusicalGroupDTO> create(@Valid @RequestBody MusicalGroupDTO musicalGroup) {
        return ResponseEntity.ok(musicalGroupService.save(musicalGroup));
    }

    @Operation(summary = "Listagem de grupos musicais")
    @ApiResponses(value = {
            @ApiResponse(description = "Listagem exibida com sucesso.", responseCode = "200"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @GetMapping
    public ResponseEntity<Page<MusicalGroupDTO>> list(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(musicalGroupService.list(pageable));
    }

    @Operation(summary = "Encontrar um grupo musical pelo id")
    @ApiResponses(value = {
            @ApiResponse(description = "Grupo musical encontrado com sucesso.", responseCode = "200"),
            @ApiResponse(description = "Grupo não encontrado.", responseCode = "404"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MusicalGroupDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(musicalGroupService.get(id));
    }

    @Operation(summary = "Atualização de grupo musical")
    @ApiResponses(value = {
            @ApiResponse(description = "Grupo atualizado com sucesso.", responseCode = "200"),
            @ApiResponse(description = "Requisição mal formatada.", responseCode = "400"),
            @ApiResponse(description = "Grupo não encontrado.", responseCode = "404"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MusicalGroupDTO> update(@PathVariable Long id, @Valid @RequestBody MusicalGroupDTO musicalGroup) {
        return ResponseEntity.ok(musicalGroupService.update(id, musicalGroup));
    }

    @Operation(summary = "Exclusão do grupo musical")
    @ApiResponses(value = {
            @ApiResponse(description = "Exclusão realizada com sucesso.", responseCode = "204"),
            @ApiResponse(description = "Grupo não encontrado.", responseCode = "404"),
            @ApiResponse(description = "Não foi possível realizar a exclusão devido a lógica negocial", responseCode = "409"),
            @ApiResponse(description = "Erro interno do servidor", responseCode = "500")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        musicalGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/solicitacoes-vinculo/pendentes")
    public ResponseEntity<List<SolicitacaoVinculoDTO>> listarSolicitacoesPendentes() {
        return ResponseEntity.ok(musicalGroupService.listarSolicitacoesPendentes());
    }

    @Operation(summary = "Solicitação de vínculo de aluno ao grupo")
    @PostMapping("/{id}/vincular")
    public ResponseEntity<Void> vincularAluno(@PathVariable Long id) {
        musicalGroupService.solicitarVinculo(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/solicitacoes-vinculo/{solicitacaoId}/aprovar")
    public ResponseEntity<Void> aprovarSolicitacao(@PathVariable("solicitacaoId") Long solicitacaoId) {
    musicalGroupService.aprovarSolicitacao(solicitacaoId);
    return ResponseEntity.ok().build();
    }

    @PutMapping("/solicitacoes-vinculo/{solicitacaoId}/recusar")
    public ResponseEntity<Void> recusarSolicitacao(@PathVariable("solicitacaoId") Long solicitacaoId) {
        musicalGroupService.recusarSolicitacao(solicitacaoId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar grupos em que o aluno é integrante")
    @GetMapping("/meus-grupos")
    public ResponseEntity<List<MusicalGroupDTO>> listarMeusGrupos(@AuthenticationPrincipal UserInfo user) {
        return ResponseEntity.ok(musicalGroupService.listarPorIntegrante(user.getId()));
    }
}
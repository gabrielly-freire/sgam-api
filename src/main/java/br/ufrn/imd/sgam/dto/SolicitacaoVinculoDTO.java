package br.ufrn.imd.sgam.dto;

import br.ufrn.imd.sgam.model.SolicitacaoVinculo;

public record SolicitacaoVinculoDTO(
    Long id,
    String alunoNome,
    String alunoMatricula, // Mantemos o nome que o front-end espera receber
    String grupoNome,
    String status
) {
    public SolicitacaoVinculoDTO(SolicitacaoVinculo solicitacao) {
        this(
            solicitacao.getId(),
            solicitacao.getAluno().getName(),    
            solicitacao.getAluno().getUsername(), 
            solicitacao.getMusicalGroup().getNome(),
            solicitacao.getStatus().name()
        );
    }
}
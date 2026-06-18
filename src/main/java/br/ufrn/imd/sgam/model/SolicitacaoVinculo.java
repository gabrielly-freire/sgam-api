package br.ufrn.imd.sgam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import br.ufrn.imd.sgam.enums.StatusSolicitacao;

@Entity
@Table(name = "solicitacao_vinculo")
@Getter
@Setter
public class SolicitacaoVinculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private UserInfo aluno;

    @ManyToOne
    @JoinColumn(name = "grupo_id", nullable = false)
    private MusicalGroup musicalGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSolicitacao status = StatusSolicitacao.PENDENTE;

    private LocalDateTime dataSolicitacao = LocalDateTime.now();
}
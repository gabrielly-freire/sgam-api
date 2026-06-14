package br.ufrn.imd.sgam.model;

import br.ufrn.imd.sgam.enums.RequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLRestriction;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "presentation_request")
@SQLRestriction(value = "active = true")
public class PresentationRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Evento solicitado.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    /**
     * Usuário que realizou a solicitação.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private UserInfo requester;

    /**
     * Grupo que aceitou a solicitação.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musical_group_id")
    private MusicalGroup musicalGroup;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDENTE;

    @Column(columnDefinition = "TEXT")
    private String cancellationReason;
}
package br.ufrn.imd.sgam.repository;

import br.ufrn.imd.sgam.enums.PresentationRequestStatus;
import br.ufrn.imd.sgam.model.PresentationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PresentationRequestRepository extends GenericRepository<PresentationRequest> {

    Page<PresentationRequest> findByMusicalGroupCoordenadorIdAndStatus(
            Long coordenadorId,
            PresentationRequestStatus status,
            Pageable pageable
    );

    Page<PresentationRequest> findByEventIdAndStatus(
            Long eventId,
            PresentationRequestStatus status,
            Pageable pageable
    );

    boolean existsByMusicalGroupIdAndStatusAndEventDateTimeAndIdNot(
            Long musicalGroupId,
            PresentationRequestStatus status,
            LocalDateTime dateTime,
            Long ignoredId
    );
}

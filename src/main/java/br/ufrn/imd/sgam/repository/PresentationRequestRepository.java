package br.ufrn.imd.sgam.repository;

import br.ufrn.imd.sgam.enums.RequestStatus;
import br.ufrn.imd.sgam.model.PresentationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PresentationRequestRepository extends JpaRepository<PresentationRequest, Long> {

        List<PresentationRequest> findByStatus(
                        RequestStatus status);

        List<PresentationRequest> findByRequesterId(
                        Long requesterId);

        Page<PresentationRequest> findByEventIdAndStatus(
                        Long eventId,
                        RequestStatus status,
                        Pageable pageable);

        @Query("""
                            SELECT COUNT(pr) > 0
                            FROM PresentationRequest pr
                            WHERE pr.musicalGroup.id = :groupId
                            AND pr.event.dateTime = :dateTime
                            AND pr.status = 'CONFIRMADO'
                        """)
        boolean existsConfirmedPresentationAtSameTime(
                        Long groupId,
                        LocalDateTime dateTime);
}
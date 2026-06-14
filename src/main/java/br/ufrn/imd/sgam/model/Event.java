package br.ufrn.imd.sgam.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "event")
@SQLRestriction(value = "active = true")
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @NotNull
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;
}
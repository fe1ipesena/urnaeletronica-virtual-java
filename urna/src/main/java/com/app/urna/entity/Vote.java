package com.app.urna.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date moment;

    @ManyToOne
    @JoinColumn(name = "mayor_id", nullable = false)
    private Candidate mayor;

    @ManyToOne
    @JoinColumn(name = "councilor_id", nullable = false)
    private Candidate councilor;

    @Column(nullable = false, unique = true)
    private String receipt;

    @PrePersist
    public void prePersist() {
        if (this.receipt == null) {
            this.receipt = UUID.randomUUID().toString();
        }
        if (this.moment == null) {
            this.moment = new Date();
        }
    }
}

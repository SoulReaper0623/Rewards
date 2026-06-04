package org.visa.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "rewards")
@Getter
@Setter
@NoArgsConstructor
public class Rewards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_id")
    private Long rewardId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "point_type_id", nullable = false)
    private Long pointTypeId;

    @Column(nullable = false)
    private Long points;

    @Column
    private String description;

    @Column(name = "event_date", nullable = false, updatable = false)
    private OffsetDateTime eventDate;

    @PrePersist
    protected void onCreate() {
        this.eventDate = OffsetDateTime.now();
    }
}

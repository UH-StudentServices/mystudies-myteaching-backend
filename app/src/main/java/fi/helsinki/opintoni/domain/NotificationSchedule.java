package fi.helsinki.opintoni.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_schedules")
public class NotificationSchedule extends AbstractAuditingEntity {
    @Id
    @GeneratedValue
    public Long id;

    @Column(name = "start_date")
    @NotNull
    public LocalDateTime startDate;

    @Column(name = "end_date")
    @NotNull
    public LocalDateTime endDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "notification_id")
    public Notification notification;
}

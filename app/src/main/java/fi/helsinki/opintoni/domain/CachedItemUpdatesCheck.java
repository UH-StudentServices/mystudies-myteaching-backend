package fi.helsinki.opintoni.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "cached_item_updates_check")
public class CachedItemUpdatesCheck {
    @Id
    @GeneratedValue
    public Long id;

    @NotBlank
    public String cacheName;

    @Column(name = "last_checked")
    @NotNull
    public LocalDateTime lastChecked;
}

package fi.helsinki.opintoni.domain.portfolio;

import fi.helsinki.opintoni.domain.AbstractAuditingEntity;
import fi.helsinki.opintoni.domain.Ownership;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "free_text_content")
public class FreeTextContent extends AbstractAuditingEntity implements Ownership {
    @Id
    @GeneratedValue
    public Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    public Portfolio portfolio;

    @Enumerated(EnumType.STRING)
    @Column(name = "teacher_portfolio_section")
    public TeacherPortfolioSection teacherPortfolioSection;

    @NotBlank
    public String title;

    @NotBlank
    public String text;

    @Override
    public Long getOwnerId() {
        return portfolio.getOwnerId();
    }
}

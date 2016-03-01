package fi.helsinki.opintoni.domain.portfolio;

import fi.helsinki.opintoni.domain.AbstractAuditingEntity;
import fi.helsinki.opintoni.domain.Ownership;
import fi.helsinki.opintoni.domain.converter.portfolio.LanguageProficiencyConverter;
import fi.helsinki.opintoni.domain.converter.portfolio.PortfolioLanguageConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "language_proficiency",
       uniqueConstraints = {@UniqueConstraint(columnNames={"portfolio_id", "language_code"})})
public class PortfolioLanguageProficiency extends AbstractAuditingEntity implements Ownership {
    @Id
    @GeneratedValue
    public Long id;

    @NotNull
    @Column(name = "language_code")
    @Convert(converter = PortfolioLanguageConverter.class)
    public PortfolioLanguage languageCode;

    @NotNull
    @Convert(converter = LanguageProficiencyConverter.class)
    public LanguageProficiency proficiency;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    public Portfolio portfolio;

    @Override
    public Long getOwnerId() {
        return portfolio.getOwnerId();
    }
}

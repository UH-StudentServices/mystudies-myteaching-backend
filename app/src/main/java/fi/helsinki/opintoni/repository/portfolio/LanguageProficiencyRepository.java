package fi.helsinki.opintoni.repository.portfolio;

import fi.helsinki.opintoni.domain.portfolio.PortfolioLanguageProficiency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageProficiencyRepository extends JpaRepository<PortfolioLanguageProficiency, Long> {
    List<PortfolioLanguageProficiency> findByPortfolioId(Long id);
}

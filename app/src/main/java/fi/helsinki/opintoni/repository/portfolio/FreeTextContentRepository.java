package fi.helsinki.opintoni.repository.portfolio;

import fi.helsinki.opintoni.domain.portfolio.FreeTextContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreeTextContentRepository extends JpaRepository<FreeTextContent, Long> {
    List<FreeTextContent> findByPortfolioId(Long portfolioId);
}

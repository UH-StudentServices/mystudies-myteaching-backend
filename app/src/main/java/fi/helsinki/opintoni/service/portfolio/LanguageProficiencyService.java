package fi.helsinki.opintoni.service.portfolio;

import fi.helsinki.opintoni.domain.portfolio.PortfolioLanguageProficiency;
import fi.helsinki.opintoni.dto.portfolio.LanguageProficiencyDto;
import fi.helsinki.opintoni.repository.portfolio.LanguageProficiencyRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.converter.portfolio.LanguageProficiencyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LanguageProficiencyService {

    private final PortfolioRepository portfolioRepository;
    private final LanguageProficiencyRepository languageProficiencyRepository;
    private final LanguageProficiencyConverter languageProficiencyConverter;

    @Autowired
    public LanguageProficiencyService(PortfolioRepository portfolioRepository,
                                      LanguageProficiencyRepository languageProficiencyRepository,
                                      LanguageProficiencyConverter languageProficiencyConverter) {
        this.portfolioRepository = portfolioRepository;
        this.languageProficiencyRepository = languageProficiencyRepository;
        this.languageProficiencyConverter = languageProficiencyConverter;
    }

    public List<LanguageProficiencyDto> findByPortfolioId(Long id) {
        return languageProficiencyRepository.findByPortfolioId(id).stream()
            .map(languageProficiencyConverter::toDto)
            .collect(Collectors.toList());
    }

    public LanguageProficiencyDto addLanguageProficiency(Long portfolioId,
                                                         LanguageProficiencyDto languageProficiencyDto) {
        PortfolioLanguageProficiency portfolioLanguageProficiency = new PortfolioLanguageProficiency();
        portfolioLanguageProficiency.languageCode = languageProficiencyDto.languageCode;
        portfolioLanguageProficiency.proficiency = languageProficiencyDto.proficiency;
        portfolioLanguageProficiency.portfolio = portfolioRepository.findOne(portfolioId);

        return languageProficiencyConverter.toDto(
            languageProficiencyRepository.save(portfolioLanguageProficiency)
        );
    }

    public LanguageProficiencyDto updateLanguageProficiency(Long languageProficiencyId,
                                                            LanguageProficiencyDto languageProficiencyDto) {
        PortfolioLanguageProficiency languageProficiency =
            languageProficiencyRepository.findOne(languageProficiencyId);
        languageProficiency.proficiency = languageProficiencyDto.proficiency;

        return languageProficiencyDto;
    }

    public void deleteLanguageProficiency(Long languageProficiencyId) {
        languageProficiencyRepository.delete(languageProficiencyId);
    }
}

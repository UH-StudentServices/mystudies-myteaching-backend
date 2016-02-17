package fi.helsinki.opintoni.service.portfolio;

import fi.helsinki.opintoni.domain.portfolio.FreeTextContent;
import fi.helsinki.opintoni.dto.portfolio.FreeTextContentDto;
import fi.helsinki.opintoni.repository.portfolio.FreeTextContentRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.converter.portfolio.FreeTextContentConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FreeTextContentService {

    private final FreeTextContentRepository freeTextContentRepository;
    private final FreeTextContentConverter freeTextContentConverter;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public FreeTextContentService(
        FreeTextContentRepository freeTextContentRepository,
        FreeTextContentConverter freeTextContentConverter,
        PortfolioRepository portfolioRepository) {

        this.freeTextContentRepository = freeTextContentRepository;
        this.freeTextContentConverter = freeTextContentConverter;
        this.portfolioRepository = portfolioRepository;
    }


    public List<FreeTextContentDto> findByPortfolioId(Long portfolioId) {
        return freeTextContentRepository
            .findByPortfolioId(portfolioId)
            .stream()
            .map(freeTextContentConverter::toDto)
            .collect(Collectors.toList());
    }

    public FreeTextContentDto insertFreeTextContent(Long portfolioId, FreeTextContentDto freeTextContentDto) {
        FreeTextContent freeTextContent = new FreeTextContent();
        freeTextContent.title = freeTextContentDto.title;
        freeTextContent.text = freeTextContentDto.text;
        freeTextContent.portfolio = portfolioRepository.findOne(portfolioId);
        return freeTextContentConverter.toDto(freeTextContentRepository.save(freeTextContent));
    }

    public void updateFreeTextContent(Long freeTextContentId, FreeTextContentDto freeTextContentDto) {
        FreeTextContent freeTextContent = freeTextContentRepository.findOne(freeTextContentId);
        freeTextContent.title = freeTextContentDto.title;
        freeTextContent.text = freeTextContentDto.text;
    }

    public void deleteFreeTextContent(Long freeTextContentId) {
        freeTextContentRepository.delete(freeTextContentId);
    }
}

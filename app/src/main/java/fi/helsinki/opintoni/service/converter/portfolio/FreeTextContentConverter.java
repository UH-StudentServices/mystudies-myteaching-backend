package fi.helsinki.opintoni.service.converter.portfolio;

import fi.helsinki.opintoni.domain.portfolio.FreeTextContent;
import fi.helsinki.opintoni.dto.portfolio.FreeTextContentDto;
import org.springframework.stereotype.Component;

@Component
public class FreeTextContentConverter {
    public FreeTextContentDto toDto(FreeTextContent freeTextContent) {
        FreeTextContentDto freeTextContentDto = new FreeTextContentDto();
        freeTextContentDto.id = freeTextContent.id;
        freeTextContentDto.title = freeTextContent.title;
        freeTextContentDto.text = freeTextContent.text;

        if(freeTextContent.teacherPortfolioSection != null) {
            freeTextContentDto.portfolioSection = freeTextContent.teacherPortfolioSection.toString();
        }

        return freeTextContentDto;
    }
}

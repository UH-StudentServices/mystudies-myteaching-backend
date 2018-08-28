/*
 * This file is part of MystudiesMyteaching application.
 *
 * MystudiesMyteaching application is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MystudiesMyteaching application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MystudiesMyteaching application.  If not, see <http://www.gnu.org/licenses/>.
 */

package fi.helsinki.opintoni.service.portfolio;

import fi.helsinki.opintoni.domain.portfolio.Portfolio;
import fi.helsinki.opintoni.domain.portfolio.PortfolioBackground;
import fi.helsinki.opintoni.repository.portfolio.PortfolioBackgroundRepository;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import fi.helsinki.opintoni.service.ImageService;
import fi.helsinki.opintoni.service.UserSettingsService;
import fi.helsinki.opintoni.service.storage.FileStorage;
import fi.helsinki.opintoni.util.FileNameUtil;
import fi.helsinki.opintoni.util.UriBuilder;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.SelectBackgroundRequest;
import fi.helsinki.opintoni.web.rest.privateapi.usersettings.UploadImageBase64Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@Transactional
public class PortfolioBackgroundService {

    private static final Predicate<PortfolioBackground> HAS_DEFAULT_BACKGROUND = (pb) -> pb.backgroundFilename != null;

    private final PortfolioBackgroundRepository portfolioBackgroundRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserSettingsService userSettingsService;
    private final ImageService imageService;
    private final FileStorage fileStorage;
    private final UriBuilder uriBuilder;

    @Autowired
    public PortfolioBackgroundService(PortfolioBackgroundRepository portfolioBackgroundRepository,
                                      PortfolioRepository portfolioRepository,
                                      UserSettingsService userSettingsService,
                                      ImageService imageService,
                                      FileStorage fileStorage,
                                      UriBuilder uriBuilder) {
        this.portfolioBackgroundRepository = portfolioBackgroundRepository;
        this.portfolioRepository = portfolioRepository;
        this.userSettingsService = userSettingsService;
        this.imageService = imageService;
        this.fileStorage = fileStorage;
        this.uriBuilder = uriBuilder;
    }

    public String getPortfolioBackgroundUri(Long portfolioId) {
        return getPortfolioBackgroundUri(portfolioRepository.findById(portfolioId).get());
    }

    public String getPortfolioBackgroundUri(Portfolio portfolio) {
        Optional<PortfolioBackground> backgroundOptional = portfolioBackgroundRepository.findByPortfolioId(portfolio.id);

        if (backgroundOptional.isPresent()) {
            PortfolioBackground background = backgroundOptional.get();

            return (HAS_DEFAULT_BACKGROUND.test(background))
                ? uriBuilder.getSystemBackgroundImageUri(background.backgroundFilename)
                : uriBuilder.getCustomBackgroundImageUri(background.uploadedBackgroundFilename);
        }

        return userSettingsService.findByUserId(portfolio.user.id).backgroundUri;
    }

    public void selectBackground(Long portfolioId, SelectBackgroundRequest request) {
        PortfolioBackground portfolioBackground = getPortfolioBackgroundEntity(portfolioId);
        removeOldBackgroundFile(portfolioBackground);

        portfolioBackground.backgroundFilename = request.filename;
        portfolioBackground.uploadedBackgroundFilename = null;

        portfolioBackgroundRepository.save(portfolioBackground);
    }

    public void uploadBackground(Long portfolioId, UploadImageBase64Request request) {
        PortfolioBackground portfolioBackground = getPortfolioBackgroundEntity(portfolioId);
        byte[] bytes = imageService.createUserBackground(request.imageBase64);
        String fileName = FileNameUtil.getImageFileName();

        removeOldBackgroundFile(portfolioBackground);
        fileStorage.put(fileName, bytes);

        portfolioBackground.backgroundFilename = null;
        portfolioBackground.uploadedBackgroundFilename = fileName;

        portfolioBackgroundRepository.save(portfolioBackground);
    }

    private PortfolioBackground getPortfolioBackgroundEntity(Long portfolioId) {
        Optional<PortfolioBackground> portfolioBackgroundOptional = portfolioBackgroundRepository.findByPortfolioId(portfolioId);
        PortfolioBackground portfolioBackground;

        if (!portfolioBackgroundOptional.isPresent()) {
            portfolioBackground = new PortfolioBackground();
            portfolioBackground.portfolio = portfolioRepository.findOne(portfolioId);
        } else {
            portfolioBackground = portfolioBackgroundOptional.get();
        }

        return portfolioBackground;
    }

    private void removeOldBackgroundFile(PortfolioBackground portfolioBackground) {
        if (portfolioBackground.uploadedBackgroundFilename != null) {
            fileStorage.remove(portfolioBackground.uploadedBackgroundFilename);
        }
    }
}

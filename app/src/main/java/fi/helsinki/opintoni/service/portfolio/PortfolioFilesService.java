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

import com.google.common.base.Splitter;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.integration.fileservice.FileServiceClient;
import fi.helsinki.opintoni.integration.fileservice.FileServiceInOutStream;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioFilesService {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioFilesService.class);

    private final FileServiceClient fileServiceClient;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioFilesService(FileServiceClient fileServiceClient,
                                 PortfolioRepository portfolioRepository) {
        this.fileServiceClient = fileServiceClient;
        this.portfolioRepository = portfolioRepository;
    }

    public void addFile(String filename, byte[] data, long userId) {
        String portfolioName = getPortfolioName(userId);
        fileServiceClient.addFile(buildPortfolioFilename(portfolioName, filename), data);
    }

    public FileServiceInOutStream getFileListing(long userId) {
        return fileServiceClient.getFileListing(getPortfolioName(userId));
    }

    public FileServiceInOutStream getFile(String filename) {
        return fileServiceClient.getFile(filename);
    }

    public void deleteFile(String filename, long userId) {
        String portfolioName = getPortfolioName(userId);
        fileServiceClient.deleteFile(buildPortfolioFilename(portfolioName, filename));
    }

    private String buildPortfolioFilename(String portfolioName, String filename) {
        return String.join("/", portfolioName, filename);
    }

    private String getPortfolioName(long userId) {
        List<String> path = Splitter.on("/").splitToList(portfolioRepository.findByUserId(userId).findFirst()
            .orElseThrow(() -> new NotFoundException("Portfolio not found")).path);
        return path.get(path.size() - 1);
    }
}

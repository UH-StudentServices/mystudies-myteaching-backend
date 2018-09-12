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

import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.integration.fileservice.FileServiceClient;
import fi.helsinki.opintoni.integration.fileservice.FileServiceInOutStream;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortfolioFilesService {

    private final FileServiceClient fileServiceClient;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioFilesService(FileServiceClient fileServiceClient,
                                 PortfolioRepository portfolioRepository) {
        this.fileServiceClient = fileServiceClient;
        this.portfolioRepository = portfolioRepository;
    }

    public String addFile(String filename, byte[] data, long userId) {
        String portfolioPath = buildFilePath(getPortfolioPath(userId), filename);
        fileServiceClient.addFile(portfolioPath, data);
        return portfolioPath;
    }

    public FileServiceInOutStream getFileListing(long userId) {
        return fileServiceClient.getFileListing(getPortfolioPath(userId));
    }

    public FileServiceInOutStream getFile(String portfolioPath, String filename) {
        return fileServiceClient.getFile(String.join("/", portfolioPath, filename));
    }

    public void deleteFile(String filename, long userId) {
        String portfolioName = getPortfolioPath(userId);
        fileServiceClient.deleteFile(buildFilePath(portfolioName, filename));
    }

    private String buildFilePath(String portfolioPath, String filename) {
        return String.join("/", portfolioPath, filename);
    }

    private String getPortfolioPath(long userId) {
        return portfolioRepository.findByUserId(userId).findFirst()
            .orElseThrow(() -> new NotFoundException("Portfolio not found")).path;
    }
}

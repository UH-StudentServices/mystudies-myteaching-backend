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

package fi.helsinki.opintoni.web.rest.publicapi.portfolio;

import fi.helsinki.opintoni.integration.fileservice.FileServiceInOutStream;
import fi.helsinki.opintoni.service.portfolio.PortfolioFilesService;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLConnection;

@RestController
@RequestMapping(value = RestConstants.PUBLIC_FILES_API_V1)
public class PublicFilesResource extends AbstractResource {

    private final PortfolioFilesService portfolioFilesService;

    @Autowired
    public PublicFilesResource(PortfolioFilesService portfolioFilesService) {
        this.portfolioFilesService = portfolioFilesService;
    }

    @GetMapping("/{path}/{filename:.+}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("path") String path,
                                                       @PathVariable("filename") String filename) {
        FileServiceInOutStream inOutStream = portfolioFilesService.getFile(path, filename);
        InputStreamResource isr = new InputStreamResource(inOutStream.getInputStream());
        MediaType contentType = MediaType.valueOf(URLConnection.guessContentTypeFromName(filename));
        HttpHeaders headers = new HttpHeaders();

        headers.setContentLength(inOutStream.getSize());
        headers.setContentType(contentType);

        return new ResponseEntity<>(isr, headers, HttpStatus.OK);
    }

}

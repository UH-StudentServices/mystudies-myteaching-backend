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

package fi.helsinki.opintoni.web.rest.privateapi.portfolio;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static fi.helsinki.opintoni.web.rest.RestConstants.PUBLIC_API_V1;

/**
 * Tailored to match what CKEditor wants to see.
 */
public class FileUploadResponse {
    public int uploaded; // This is int because CKEditor wants 1 = true, 0 = false
    public String fileName;
    public String url;

    public FileUploadResponse() {}

    public FileUploadResponse(boolean ok, String fileName, String filePath) {
        this.uploaded = ok ? 1 : 0;
        this.fileName = fileName;
        this.url = String.join("/", getBaseUrl(), PUBLIC_API_V1, "files", filePath);
    }

    private String getBaseUrl() {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri();
        builder.scheme("http");
        builder.replacePath("");
        return builder.build().toString();
    }

}

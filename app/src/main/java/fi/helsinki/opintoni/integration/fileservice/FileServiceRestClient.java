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

package fi.helsinki.opintoni.integration.fileservice;

import com.google.common.base.Charsets;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.exception.http.RestClientServiceException;
import org.apache.http.*;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FileServiceRestClient implements FileServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceRestClient.class);

    private final String baseUrl;
    private final String token;
    private final String filesUrl;

    public FileServiceRestClient(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;
        this.filesUrl = baseUrl + "/files";
    }

    @Override
    public FileServiceInOutStream getFileListing(String prefix) {
        FileServiceInOutStream out;

        try {
            HttpResponse response = executeGet(getFileListingUrl(prefix));
            HttpEntity entity = response.getEntity();
            out = new FileServiceInOutStream(entity.getContentLength());
            entity.writeTo(out);
        } catch (IOException e) {
            logger.error("Failed to fetch file listing with prefix {}", prefix, e);
            throw new RestClientServiceException("Failed to fetch file listing with prefix " + prefix);
        }

        return out;
    }

    @Override
    public FileServiceInOutStream getFile(String path) {
        FileServiceInOutStream out;

        try {
            HttpResponse response = executeGet(getFileUrl(path));
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new NotFoundException("File '" + path + "' not found");
            }
            HttpEntity entity = response.getEntity();
            out = new FileServiceInOutStream(entity.getContentLength());
            entity.writeTo(out);
        } catch (IOException e) {
            logger.error("Failed to fetch file with name {}", path, e);
            throw new RestClientServiceException("Failed to fetch file file with name " + path);
        }

        return out;
    }

    @Override
    public void addFile(String path, byte[] data) {
        try {
            HttpEntity entity = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setCharset(Charsets.UTF_8)
                .addBinaryBody(path, data, ContentType.MULTIPART_FORM_DATA, path)
                .build();

            Request.Post(filesUrl)
                .addHeader(getAuthorizationHeader())
                .body(entity)
                .execute();
        } catch (IOException e) {
            logger.error("Failed to save file with name {}", path, e);
            throw new RestClientServiceException("Failed to save file with name " + path);
        }
    }

    @Override
    public void deleteFile(String path) {
        try {
            Request.Delete(getFileUrl(path)).addHeader(getAuthorizationHeader()).execute();
        } catch (IOException e) {
            logger.error("Failed to delete file with name {}", path, e);
            throw new RestClientServiceException("Failed to delete file with name " + path);
        }
    }

    private HttpResponse executeGet(String url) throws IOException {
        return Request.Get(url)
            .addHeader(getAuthorizationHeader())
            .execute().returnResponse();
    }

    private Header getAuthorizationHeader() {
        return new BasicHeader("Authorization", String.format("Bearer %s", token));
    }

    private String getFileUrl(String path) {
        return String.format("%s?file=%s", filesUrl, path);
    }

    private String getFileListingUrl(String prefix) {
        return String.format("%s?prefix=%s", filesUrl, prefix);
    }
}

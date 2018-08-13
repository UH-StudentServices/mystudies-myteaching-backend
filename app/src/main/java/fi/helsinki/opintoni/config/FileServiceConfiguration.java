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

package fi.helsinki.opintoni.config;

import fi.helsinki.opintoni.integration.fileservice.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileServiceConfiguration {

    private static final String CLIENT_IMPLEMENTATION_PROPERTY = "fileService.client.implementation";
    private static final String CLIENT_STORAGE_IMPLEMENTATION_PROPERTY = "fileService.client.storage";
    private static final String CLIENT_STORAGE_FILE_SYSTEM_PATH_PROPERTY = "fileService.client.path";
    private static final String BASE_URL_PROPERTY = "fileService.base.url";
    private static final String TOKEN_PROPERTY = "fileService.token";
    private static final String MOCK_CLIENT = "mock";
    private static final String REST_CLIENT = "rest";
    private static final String MEMORY_STORAGE = "memory";
    private static final String FILE_SYSTEM_STORAGE = "fileSystem";

    @Autowired
    private AppConfiguration appConfiguration;

    private FileServiceClient restClient() {
        return new FileServiceRestClient(
            appConfiguration.get(BASE_URL_PROPERTY),
            appConfiguration.get(TOKEN_PROPERTY)
        );
    }

    private FileServiceClient mockClient() {
        return new FileServiceMockClient(fileServiceStorage());
    }

    private FileServiceStorage memoryStorage() {
        return new FileServiceMemoryStorage();
    }

    private FileServiceStorage fileSystemStorage() {
        return new FileServiceFileSystemStorage(appConfiguration.get(CLIENT_STORAGE_FILE_SYSTEM_PATH_PROPERTY));
    }

    private String getClientImplementation() {
        return appConfiguration.get(CLIENT_IMPLEMENTATION_PROPERTY);
    }

    private String getFileServiceStorageImplementation() {
        return appConfiguration.get(CLIENT_STORAGE_IMPLEMENTATION_PROPERTY);
    }

    @Bean
    public FileServiceStorage fileServiceStorage() {
        switch (getFileServiceStorageImplementation()) {
            case FILE_SYSTEM_STORAGE:
                return fileSystemStorage();
            case MEMORY_STORAGE:
                return memoryStorage();
            default:
                return memoryStorage();
        }
    }

    @Bean
    public FileServiceClient fileServiceClient() {
        switch (getClientImplementation()) {
            case REST_CLIENT:
                return restClient();
            case MOCK_CLIENT:
                return mockClient();
            default:
                return mockClient();
        }
    }
}

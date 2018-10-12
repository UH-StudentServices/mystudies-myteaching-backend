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

import fi.helsinki.opintoni.service.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class FileStorageConfiguration {

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private FileService fileService;

    @Bean
    @Profile({Constants.SPRING_PROFILE_TEST, Constants.SPRING_PROFILE_LOCAL_DEVELOPMENT})
    public MemoryFileStorage memoryFileStorage() {
        return new MemoryFileStorage(filesMemory());
    }

    @Bean
    public FilesMemory filesMemory() {
        return new FilesMemory();
    }

    @Bean
    @Profile({
        Constants.SPRING_PROFILE_DEVELOPMENT,
        Constants.SPRING_PROFILE_QA,
        Constants.SPRING_PROFILE_DEMO,
        Constants.SPRING_PROFILE_PRODUCTION
    })
    public FileStorage fileSystemFileStorage() {
        return new FileSystemFileStorage(appConfiguration, fileService);
    }
}

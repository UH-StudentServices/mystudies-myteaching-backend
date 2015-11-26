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

package fi.helsinki.opintoni.service.storage;

import fi.helsinki.opintoni.config.AppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

public class FileSystemFileStorage implements FileStorage {

    private final AppConfiguration appConfiguration;
    private final FileService fileService;

    @Autowired
    public FileSystemFileStorage(AppConfiguration appConfiguration, FileService fileService) {
        this.appConfiguration = appConfiguration;
        this.fileService = fileService;
    }

    @Override
    public void put(String name, byte[] data) {
        try {
            File file = new File(appConfiguration.get("fileStorage.path") + "/" + name);
            fileService.writeByteArrayToFile(file, data);
        } catch (IOException e) {
            throw new RuntimeException("Could not save file");
        }
    }

    @Override
    public void remove(String name) {
        File file = new File(appConfiguration.get("fileStorage.path") + "/" + name);
        fileService.remove(file);
    }
}

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

package fi.helsinki.opintoni.service.profile;

import fi.helsinki.opintoni.exception.http.NotFoundException;
import fi.helsinki.opintoni.integration.fileservice.FileServiceClient;
import fi.helsinki.opintoni.integration.fileservice.FileServiceInOutStream;
import fi.helsinki.opintoni.repository.profile.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileFilesService {

    private final FileServiceClient fileServiceClient;
    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileFilesService(FileServiceClient fileServiceClient,
                               ProfileRepository profileRepository) {
        this.fileServiceClient = fileServiceClient;
        this.profileRepository = profileRepository;
    }

    public String addFile(String filename, byte[] data, long userId) {
        String profilePath = buildFilePath(getProfilePath(userId), filename);
        fileServiceClient.addFile(profilePath, data);
        return profilePath;
    }

    public FileServiceInOutStream getFileListing(long userId) {
        return fileServiceClient.getFileListing(getProfilePath(userId));
    }

    public FileServiceInOutStream getFile(String profilePath, String filename) {
        return fileServiceClient.getFile(String.join("/", profilePath, filename));
    }

    public void deleteFile(String filename, long userId) {
        fileServiceClient.deleteFile(buildFilePath(getProfilePath(userId), filename));
    }

    private String buildFilePath(String profilePath, String filename) {
        return String.join("/", profilePath, filename);
    }

    private String getProfilePath(long userId) {
        return profileRepository.findByUserId(userId).findFirst()
            .orElseThrow(() -> new NotFoundException("Profile not found")).path;
    }
}

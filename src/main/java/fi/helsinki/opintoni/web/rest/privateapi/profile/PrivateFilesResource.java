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

package fi.helsinki.opintoni.web.rest.privateapi.profile;

import fi.helsinki.opintoni.integration.fileservice.FileServiceInOutStream;
import fi.helsinki.opintoni.service.profile.ProfileFilesService;
import fi.helsinki.opintoni.web.arguments.UserId;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@RestController
@RequestMapping(
    value = RestConstants.PRIVATE_FILES_API_V1)
public class PrivateFilesResource extends AbstractResource {

    private final ProfileFilesService profileFilesService;

    private final Environment environment;

    @Autowired
    public PrivateFilesResource(ProfileFilesService profileFilesService, Environment environment) {
        this.profileFilesService = profileFilesService;
        this.environment = environment;
    }

    @GetMapping
    public ResponseEntity<InputStreamResource> getFileListing(@UserId Long userId) {
        FileServiceInOutStream inOutStream = profileFilesService.getFileListing(userId);
        InputStreamResource isr = new InputStreamResource(inOutStream.getInputStream());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(inOutStream.getSize());
        return new ResponseEntity<>(isr, headers, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FileUploadResponse> addFile(@RequestParam("upload") MultipartFile file, @UserId Long userId) {
        try {
            byte[] data = file.getBytes();
            String fileName = file.getOriginalFilename();
            String profilePath = profileFilesService.addFile(fileName, data, userId);
            String protocol = isLocalDev() ? "http" : "https";
            FileUploadResponse ret = new FileUploadResponse(true, fileName, profilePath, protocol);
            return new ResponseEntity<>(ret, new HttpHeaders(), HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    private boolean isLocalDev() {
        return Arrays.asList(environment.getActiveProfiles()).contains("local-dev");
    }

    @DeleteMapping("/{filename:.+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(@PathVariable("filename") String filename, @UserId Long userId) {
        profileFilesService.deleteFile(filename, userId);
    }
}

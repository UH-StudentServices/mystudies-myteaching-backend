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

package fi.helsinki.opintoni.web.rest.publicapi;

import com.codahale.metrics.annotation.Timed;
import fi.helsinki.opintoni.service.BackgroundImageService;
import fi.helsinki.opintoni.service.UserSettingsService;
import fi.helsinki.opintoni.service.profile.ProfileService;
import fi.helsinki.opintoni.web.rest.AbstractResource;
import fi.helsinki.opintoni.web.rest.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(
    value = RestConstants.PUBLIC_API_V1 + "/images")
public class PublicImageResource extends AbstractResource {

    private final UserSettingsService userSettingsService;
    private final BackgroundImageService backgroundImageService;
    private final ProfileService profileService;

    @Autowired
    public PublicImageResource(UserSettingsService userSettingsService,
                               BackgroundImageService backgroundImageService, ProfileService profileService) {
        this.userSettingsService = userSettingsService;
        this.backgroundImageService = backgroundImageService;
        this.profileService = profileService;
    }

    // When Obar is in production use, this endpoint should be removed.
    @RequestMapping(
        value = "/avatar/{personId}",
        method = RequestMethod.GET,
        produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<BufferedImage> getUserAvatarByPersonId(@PathVariable("personId") String personId) {
        return ResponseEntity.ok()
            .headers(headersWithContentType(MediaType.IMAGE_JPEG))
            .body(userSettingsService.getUserAvatarImageByPersonId(personId));
    }

    @RequestMapping(
        value = "/background/{personId}",
        method = RequestMethod.GET,
        produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<BufferedImage> getUserBackgroundByOodiPersonId(@PathVariable("personId") String personId)
        throws IOException {
        return ResponseEntity.ok()
            .headers(headersWithContentType(MediaType.IMAGE_JPEG))
            .body(userSettingsService.getUserBackgroundImage(personId));
    }

    @RequestMapping(
        value = "/backgrounds/{fileName:.+}",
        method = RequestMethod.GET,
        produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<BufferedImage> serve(@PathVariable String fileName) throws IOException {
        return ResponseEntity.ok()
            .headers(headersWithContentType(MediaType.IMAGE_JPEG))
            .body(backgroundImageService.getDefaultBackgroundImage(fileName));
    }

    @RequestMapping(value = "/backgrounds", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<String>> getBackgrounds() throws IOException {
        return response(backgroundImageService.getBackgroundImageFiles());
    }
}

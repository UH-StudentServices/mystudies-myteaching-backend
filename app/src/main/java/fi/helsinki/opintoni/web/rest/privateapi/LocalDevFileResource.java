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

package fi.helsinki.opintoni.web.rest.privateapi;

import fi.helsinki.opintoni.config.Constants;
import fi.helsinki.opintoni.service.storage.FilesMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/files")
@Profile(Constants.SPRING_PROFILE_LOCAL_DEVELOPMENT)
public class LocalDevFileResource {

    private final FilesMemory filesMemory;

    @Autowired
    public LocalDevFileResource(FilesMemory filesMemory) {
        this.filesMemory = filesMemory;
    }

    @RequestMapping(value = "/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> serve(@PathVariable String filename) throws IOException {
        return new ResponseEntity<>(filesMemory.getBytes(filename), HttpStatus.OK);
    }

}

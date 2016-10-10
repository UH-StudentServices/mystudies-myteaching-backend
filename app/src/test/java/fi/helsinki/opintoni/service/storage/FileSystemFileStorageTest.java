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
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileSystemFileStorageTest {

    private final String FILENAME = "alhfj09325qwnrfsalfkfas7809.jpg";

    @Mock
    private AppConfiguration appConfiguration;

    @Mock
    private FileService fileService;

    @InjectMocks
    private FileSystemFileStorage fileSystemFileStorage;

    @Test
    public void thatFileIsCreated() throws IOException {
        when(appConfiguration.get("fileStorage.path")).thenReturn("/images");

        fileSystemFileStorage.put(FILENAME, new byte[0]);

        verify(fileService, times(1)).writeByteArrayToFile(argThat(new FileMatcher()), eq(new byte[0]));
    }

    @Test
    public void thatFileIsRemoved() {
        when(appConfiguration.get("fileStorage.path")).thenReturn("/images");

        fileSystemFileStorage.remove(FILENAME);

        verify(fileService, times(1)).remove(argThat(new FileMatcher()));
    }

    class FileMatcher extends ArgumentMatcher<File> {

        @Override
        public boolean matches(Object argument) {
            File file = (File) argument;
            return file.getAbsolutePath().equals("/images/" + FILENAME);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("/images/" + FILENAME);
        }
    }
}

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

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.helsinki.opintoni.exception.http.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class FileServiceMockClient implements FileServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceMockClient.class);

    private final FileServiceStorage fileStorage;

    public FileServiceMockClient(FileServiceStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public FileServiceInOutStream getFileListing(String prefix) {
        List<FileListingEntry> entries = fileStorage.fileList(prefix).stream()
            .map(filename -> new FileListingEntry(fullName(prefix, filename), fileStorage.get(fullName(prefix, filename)).length))
            .collect(toList());

        FileServiceInOutStream inOutStream;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] bytes = objectMapper.writeValueAsBytes(entries);
            inOutStream = new FileServiceInOutStream(bytes.length);
            inOutStream.write(bytes);
        } catch (IOException e) {
            logger.error("Failed to write file entries to stream", e);
            throw new RuntimeException("Failed to write entries to stream");
        }

        return inOutStream;
    }

    @Override
    public FileServiceInOutStream getFile(String path) {
        byte[] data = fileStorage.get(path);
        if (data == null || data.length == 0) {
            throw new NotFoundException("File not found with name " + path);
        }
        FileServiceInOutStream inOutStream = new FileServiceInOutStream(data.length);
        inOutStream.write(data, 0, data.length);
        return inOutStream;
    }

    @Override
    public void addFile(String path, byte[] data) {
        try {
            fileStorage.put(path, data);
        } catch (IOException e) {
            logger.error("Failed to add portfolio file {}", path, e);
            throw new RuntimeException("Failed to add portfolio file " + path);
        }
    }

    @Override
    public void deleteFile(String path) {
        try {
            fileStorage.remove(path);
        } catch (IOException e) {
            logger.error("Failed to remove portfolio file {}", path, e);
            throw new RuntimeException("Failed to remove portfolio file " + path);
        }
    }

    private String fullName(String prefix, String filename) {
        if (filename.startsWith(prefix + "/")) {
            return filename;
        }
        return String.join("/", prefix, filename);
    }

    private static class FileListingEntry {

        public final String name;
        public final long size;

        public FileListingEntry(String name, long size) {
            this.name = name;
            this.size = size;
        }
    }
}

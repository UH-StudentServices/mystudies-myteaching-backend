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

import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class FileServiceFileSystemStorage implements FileServiceStorage {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceFileSystemStorage.class);

    private final String storagePath;

    public FileServiceFileSystemStorage(String storagePath) {
        this.storagePath = storagePath;
    }

    @Override
    public Set<String> fileList(String prefix) {
        File folder = getFileSystemPath(prefix).toFile();
        if (!folder.exists() || !folder.isDirectory()) {
            return new HashSet<>();
        }
        String[] files = folder.list(FileFileFilter.FILE);
        if (files != null) {
            return ImmutableSet.copyOf(files);
        }
        return new HashSet<>();
    }

    @Override
    public void clear() {
        File filesRoot = getFileSystemPath("").toFile();
        if (filesRoot.exists() || filesRoot.isDirectory()) {
            File[] childs = filesRoot.listFiles();
            if (childs == null) {
                return;
            }
            for (File child : childs) {
                child.delete();
            }
        }
    }

    @Override
    public void put(String path, byte[] data) throws IOException {
        File file = getFileSystemPath(path).toFile();
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileUtils.writeByteArrayToFile(file, data);
    }

    @Override
    public byte[] get(String path) {
        byte[] data;
        try {
            InputStream is = new FileInputStream(getFileSystemPath(path).toString());
            data = IOUtils.toByteArray(is);
            is.close();
        } catch (IOException e) {
            logger.error("Couldn't get file {}", path, e);
            return null;
        }
        return data;
    }

    @Override
    public void remove(String path) throws IOException {
        File file = getFileSystemPath(path).toFile();
        if (!file.delete()) {
            throw new IOException("Failed to delete file " + path);
        }
    }

    private Path getFileSystemPath(String path) {
        String[] pathParts = path.split("/");
        return FileSystems.getDefault().getPath(storagePath, pathParts);
    }
}

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

import java.util.HashMap;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class FileServiceMemoryStorage implements FileServiceStorage {

    private final HashMap<String, byte[]> bytesToFilename = new HashMap<>();

    @Override
    public void put(String name, byte[] data) {
        bytesToFilename.put(name, data);
    }

    @Override
    public byte[] get(String name) {
        return bytesToFilename.get(name);
    }

    @Override
    public void remove(String name) {
        bytesToFilename.remove(name);
    }

    @Override
    public Set<String> fileList(String prefix) {
        return bytesToFilename.keySet().stream().filter(key -> key.startsWith(prefix)).collect(toSet());
    }
}

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

import com.google.common.collect.Maps;

import java.util.HashMap;

public class FilesMemory {

    private final HashMap<String, byte[]> bytesByFilename = Maps.newHashMap();

    public byte[] getBytes(String name) {
        return bytesByFilename.get(name);
    }

    public void put(String name, byte[] data) {
        bytesByFilename.put(name, data);
    }

    public void remove(String name) {
        bytesByFilename.remove(name);
    }

    public boolean contains(String uploadedBackgroundFilename) {
        return bytesByFilename.containsKey(uploadedBackgroundFilename);
    }
}

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

package fi.helsinki.opintoni.service;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BackgroundImageService {

    private static final String DEFAULT_IMAGE = "Profile_1.jpg";
    private static final String BACKGROUND_IMAGES_FOLDER = "/images/backgrounds";
    private static final String BACKGROUND_IMAGES_FOLDER_EXPRESSION = BACKGROUND_IMAGES_FOLDER + "/*";

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private ImmutableMap<String, String> fileNamesToPath;

    private final ImageService imageService;

    @Autowired
    public BackgroundImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostConstruct
    public void initFilePaths() throws IOException {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        Resource[] resources = resolver.getResources(BACKGROUND_IMAGES_FOLDER_EXPRESSION);
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            builder.put(filename, getFilePath(filename));
        }
        fileNamesToPath = builder.build();
    }

    private String getFilePath(String filename) {
        return BACKGROUND_IMAGES_FOLDER + "/" + filename;
    }

    public List<String> getBackgroundImageFiles() {
        List<String> result = new ArrayList<>();
        result.addAll(fileNamesToPath.keySet());
        Collections.sort(result);
        return result;
    }

    public BufferedImage getBackgroundImage(String fileName) throws IOException {
        String filePath = fileNamesToPath.get(fileName);

        if (filePath == null) {
            return null;
        }

        Resource resource = resolver.getResource(filePath);
        return imageService.inputStreamToBufferedImage(resource.getInputStream());
    }

    public String getDefaultImageFileName() {
        return DEFAULT_IMAGE;
    }
}

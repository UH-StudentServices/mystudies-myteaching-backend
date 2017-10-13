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

import fi.helsinki.opintoni.config.AppConfiguration;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Service
public class ImageService {

    private final AppConfiguration appConfiguration;

    @Autowired
    public ImageService(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    public BufferedImage bytesToBufferedImage(byte[] bytes) {
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage inputStreamToBufferedImage(InputStream inputStream) {
        try {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] base64ToBytes(String imageBase64) {
        return Base64.getDecoder().decode(imageBase64);
    }


    public byte[] createUserAvatar(String imageBase64) {
        BufferedImage bufferedImage = bytesToBufferedImage(base64ToBytes(imageBase64));
        return toThumbnail(bufferedImage);
    }

    public byte[] createUserBackground(String imageBase64) {
        BufferedImage bufferedImage = bytesToBufferedImage(base64ToBytes(imageBase64));

        BufferedImage jpgImage = toJpg(bufferedImage);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeToByteArrayOutputStream(jpgImage, baos);

        return baos.toByteArray();
    }

    private byte[] toThumbnail(BufferedImage bufferedImage) {
        BufferedImage originalImage = toJpg(bufferedImage);

        BufferedImage thumbnailImage = Scalr.resize(
            originalImage,
            Scalr.Mode.FIT_EXACT,
            appConfiguration.getInteger("avatarSize"));

        ByteArrayOutputStream resizedImageBaos = new ByteArrayOutputStream();
        writeToByteArrayOutputStream(thumbnailImage, resizedImageBaos);

        return resizedImageBaos.toByteArray();
    }

    private void writeToByteArrayOutputStream(BufferedImage image, ByteArrayOutputStream outputStream) {
        try {
            ImageIO.write(image, "jpg", outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage toJpg(BufferedImage bufferedImage) {
        BufferedImage jpgImage = new BufferedImage(bufferedImage.getWidth(),
            bufferedImage.getHeight(),
            BufferedImage.TYPE_INT_RGB);
        jpgImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.BLACK, null);
        return jpgImage;
    }

}

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

package fi.helsinki.opintoni.service.usefullink;

import fi.helsinki.opintoni.domain.LocalizedText;
import fi.helsinki.opintoni.domain.UsefulLink;
import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.localization.Language;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultUsefulLinksService {

    protected List<UsefulLink> createUsefulLinks(List<Map<String, String>> usefulLinks, User user) {
        return usefulLinks.stream()
            .map(l -> usefulLinkFromDescriptor(l, user))
            .collect(Collectors.toList());
    }

    protected List<UsefulLink> createLocalizedUsefulLinks(List<Map<String, String>> usefulLinks, User user) {
        return usefulLinks.stream()
            .map(l -> usefulLinkFromDescriptor(l, user))
            .collect(Collectors.toList());
    }

    private UsefulLink usefulLinkFromDescriptor(Map<String, String> properties, User user) {
        UsefulLink usefulLink = new UsefulLink();
        usefulLink.type = UsefulLink.UsefulLinkType.DEFAULT;
        usefulLink.description = properties.get("description");
        usefulLink.orderIndex = Integer.valueOf(properties.get("orderIndex"));
        usefulLink.user = user;

        if (properties.containsKey("url")) {
            usefulLink.url = properties.get("url");
        } else {
            usefulLink.localizedUrl = createLocalizedUrl(properties);
        }

        return usefulLink;
    }

    private LocalizedText createLocalizedUrl(Map<String, String> properties) {
        LocalizedText localizedUrl = new LocalizedText();

        Language.getCodes().forEach(code -> localizedUrl.put(Language.fromCode(code), properties.get("url." + code)));

        return localizedUrl;
    }

    protected List<UsefulLink> getUsefulLinksByFaculty(String facultyCode, List<Map<String, String>> facultyLinkOptions,
                                               User user) {
        return facultyLinkOptions.stream()
            .filter(map -> map.get("faculty").equals(facultyCode))
            .map(properties -> usefulLinkFromDescriptor(properties, user))
            .collect(Collectors.toList());
    }
}

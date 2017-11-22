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

package fi.helsinki.opintoni.domain;

import com.google.common.collect.Maps;
import fi.helsinki.opintoni.localization.Language;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Locale;
import java.util.Map;

@Entity
@Table(name = "localized_text")
public class LocalizedText {

    @Id
    @GeneratedValue
    public Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "language_code")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "value")
    @CollectionTable(name = "localization", joinColumns = @JoinColumn(name = "localized_text_id"))
    public Map<Language, String> localizations = Maps.newHashMap();

    public void put(Language language, String value) {
        Assert.notNull(language);
        Assert.notNull(value);
        localizations.put(language, value);
    }

    public String getByLocale(Locale locale) {
        Language language = Language.fromCode(locale.getLanguage());
        return localizations.get(language);
    }
}

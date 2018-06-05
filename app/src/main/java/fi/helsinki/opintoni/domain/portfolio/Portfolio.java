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

package fi.helsinki.opintoni.domain.portfolio;

import fi.helsinki.opintoni.domain.AbstractAuditingEntity;
import fi.helsinki.opintoni.domain.Ownership;
import fi.helsinki.opintoni.domain.User;
import fi.helsinki.opintoni.domain.converter.portfolio.LanguageConverter;
import fi.helsinki.opintoni.localization.Language;
import fi.helsinki.opintoni.web.arguments.PortfolioRole;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "portfolio")
public class Portfolio extends AbstractAuditingEntity implements Ownership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Convert(converter = LanguageConverter.class)
    public Language language;

    @Column(name = "path")
    @NotBlank
    @Size(max = 500)
    public String path;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    @NotNull
    public User user;

    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    @NotNull
    public PortfolioVisibility visibility;

    @Override
    public Long getOwnerId() {
        return user.id;
    }

    @Column(name = "owner_name")
    @Size(max = 255)
    @NotBlank
    public String ownerName;

    @Column(name = "intro")
    @Size(max = 500)
    public String intro;

    @Column(name = "summary")
    @Size(max = 2500)
    public String summary;

    @Column(name="portfolio_role")
    @Enumerated(EnumType.STRING)
    @NotNull
    public PortfolioRole portfolioRole;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("path", path)
            .append("visibility", visibility.name())
            .append("ownerName", ownerName)
            .append("intro", intro)
            .append("summary", summary)
            .append("portfolioRole", portfolioRole.name())
            .toString();
    }
}

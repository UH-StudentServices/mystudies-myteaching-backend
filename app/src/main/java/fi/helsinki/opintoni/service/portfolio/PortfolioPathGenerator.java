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

package fi.helsinki.opintoni.service.portfolio;

import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PortfolioPathGenerator {

    private static final int PATH_GENERATION_MAX_TRIES = 1000;

    private final PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioPathGenerator(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public String create(String name) {
        return Optional.ofNullable(name)
            .map(StringUtils::trim)
            .map(this::spacesToDots)
            .map(StringUtils::lowerCase)
            .map(this::removeCharactersThatBreakPath)
            .map(this::makeUnique)
            .orElse(null);
    }

    private String makeUnique(String path) {
        if (isUnique(path)) {
            return path;
        }
        for (int i = 1; i < PATH_GENERATION_MAX_TRIES; i++) {
            String candidatePath = path + "-" + i;
            if (isUnique(candidatePath)) {
                return candidatePath;
            }
        }

        throw new RuntimeException("Could not generate unique portfolio path");
    }

    private boolean isUnique(String path) {
        return portfolioRepository.countByPath(path) == 0;
    }

    private String removeCharactersThatBreakPath(String path) {
        return StringUtils.replacePattern(path, "[^a-z0-9-_.]", ".");
    }

    private String spacesToDots(String name) {
        return StringUtils.replacePattern(name, "\\s+", ".");
    }
}

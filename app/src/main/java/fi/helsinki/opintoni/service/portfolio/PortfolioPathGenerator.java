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

import com.github.slugify.Slugify;
import fi.helsinki.opintoni.repository.portfolio.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PortfolioPathGenerator {

    private static final int PATH_GENERATION_MAX_TRIES = 1000;

    private final PortfolioRepository portfolioRepository;

    private final Slugify slugify;

    @Autowired
    public PortfolioPathGenerator(PortfolioRepository portfolioRepository, Slugify slugify) {
        this.portfolioRepository = portfolioRepository;
        this.slugify = slugify;
    }

    public String create(String name) {
        return Optional.ofNullable(name)
            .map(slugify::slugify)
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

}

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

package fi.helsinki.opintoni.service.news;

import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.rss.Channel;
import fi.helsinki.opintoni.dto.NewsDto;
import fi.helsinki.opintoni.service.converter.NewsConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class FetchingNewsService {

    @Autowired
    protected NewsConverter newsConverter;

    protected List<NewsDto> getAtomNews(Supplier<Feed> feedSupplier) {
        return feedSupplier.get().getEntries().stream()
            .map(newsConverter::toDtoFromAtom)
            .collect(Collectors.toList());
    }

    protected List<NewsDto> getRssNews(Supplier<Channel> channelSupplier) {
        return channelSupplier.get().getItems().stream()
            .map(newsConverter::toDtoFromRss)
            .collect(Collectors.toList());
    }

}

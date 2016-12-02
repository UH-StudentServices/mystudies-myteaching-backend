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

package fi.helsinki.opintoni.service.converter;

import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.rss.Item;
import fi.helsinki.opintoni.dto.NewsDto;
import org.springframework.stereotype.Component;

@Component
public class NewsConverter {

    public NewsDto toDtoFromAtom(Entry atomFeedEntry) {
        NewsDto newsDto = new NewsDto();
        newsDto.title = atomFeedEntry.getTitle();
        newsDto.url = atomFeedEntry.getAlternateLinks().get(0).getHref();
        if (!atomFeedEntry.getContents().isEmpty()) {
            newsDto.content = atomFeedEntry.getContents().get(0).getValue();
        } else {
            newsDto.content = "";
        }
        return newsDto;
    }

    public NewsDto toDtoFromRss(Item rssFeedItem) {
        NewsDto newsDto = new NewsDto();
        newsDto.title = rssFeedItem.getTitle();
        newsDto.url = rssFeedItem.getLink();
        if (rssFeedItem.getDescription() != null && rssFeedItem.getDescription().getValue() != null) {
            newsDto.content = rssFeedItem.getDescription().getValue();
        } else {
            newsDto.content = "";
        }
        return newsDto;
    }

}

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

package fi.helsinki.opintoni.integration.sisu;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudyEvent {
    public LocalDateTime startTime;
    public LocalizedString name;
    public LocalDate recursUntil;
    public Interval recursEvery;
    public String duration;
    public List<String> locationIds = new ArrayList<>();

    public StudyEvent(StudyEvent previousRecurringStudyEvent) {
        this.name = previousRecurringStudyEvent.name;
        this.recursUntil = previousRecurringStudyEvent.recursUntil;
        this.recursEvery = previousRecurringStudyEvent.recursEvery;
        this.duration = previousRecurringStudyEvent.duration;
        this.locationIds = previousRecurringStudyEvent.locationIds;
        this.startTime = getNextRecurringEventStartTime(previousRecurringStudyEvent.startTime);
    }

    public StudyEvent() {
    }

    private LocalDateTime getNextRecurringEventStartTime(LocalDateTime previousStartTime) {
        switch(this.recursEvery) {
            case DAILY:
                return previousStartTime.plusDays(1);
            case WEEKLY:
                return  previousStartTime.plusWeeks(1);
            case MONTHLY:
                return previousStartTime.plusMonths(1);
            case EVERY_SECOND_WEEK:
                return previousStartTime.plusMonths(2);
            default:
                throw new RuntimeException("No recurring interval provided");
        }
    }
}

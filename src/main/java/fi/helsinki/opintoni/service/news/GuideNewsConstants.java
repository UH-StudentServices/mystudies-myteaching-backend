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

/*
    10-taso opintojen tavoite (tutkintotaso periaatteessa)
    15-taso on asetus (minkä lain perusteella opinto-oikeuden tutkintorakenne on valittu)
    20-koulutusohjelma
    30-suunta
    40-vanhan asetuksen pääaine

    Etuliitteet KH ja MH ilmaisevat "uuden tutkintorakenteen" Kandin ja Maisterin.
 */
public class GuideNewsConstants {

    public static final Integer OODI_STUDY_RIGHTS_DEGREE_PROGRAMME_ID = 20;
    public static final Integer OODI_STUDY_RIGHTS_MAJOR_ID = 30;
    public static final String  OODI_STUDY_RIGHTS_BACHELOR_PROGRAMME_CODE_PREFIX = "KH";
    public static final String  OODI_STUDY_RIGHTS_MASTERS_PROGRAMME_CODE_PREFIX = "MH";
}

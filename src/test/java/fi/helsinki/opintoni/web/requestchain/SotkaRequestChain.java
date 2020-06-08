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

package fi.helsinki.opintoni.web.requestchain;

import fi.helsinki.opintoni.server.SotkaServer;

public class SotkaRequestChain<T> implements NestedRequestChain<T> {

    private final SotkaServer sotkaServer;
    private final T parentBuilder;
    private final String oodiRealisationId;

    public SotkaRequestChain(SotkaServer sotkaServer, T parentBuilder, String oodiRealisationId) {
        this.sotkaServer = sotkaServer;
        this.parentBuilder = parentBuilder;
        this.oodiRealisationId = oodiRealisationId;
    }

    public SotkaRequestChain<T> expectOodiHierarchy() {
        sotkaServer.expectOodiHierarchyRequest(oodiRealisationId);
        return this;
    }

    public SotkaRequestChain<T> expectOodiHieracry(String responseFile) {
        sotkaServer.expectOodiHierarchyRequest(oodiRealisationId, responseFile);
        return this;
    }

    public SotkaRequestChain<T> expectOodiHierarchyNotFound() {
        sotkaServer.expectOodiHierarchyRequestRealisationNotFoundFromSotka(oodiRealisationId);
        return this;
    }

    @Override
    public T and() {
        return parentBuilder;
    }
}

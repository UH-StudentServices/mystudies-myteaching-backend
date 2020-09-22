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

package fi.helsinki.opintoni.util;

import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionHelper {

    private FunctionHelper(){
    }

    private static final Logger log = LoggerFactory.getLogger(FunctionHelper.class);

    public static <T, R> Function<T, R> logAndIgnoreExceptions(Function<T, R> wrappedFunction) {
        return t -> {
            try {
                return wrappedFunction.apply(t);
            } catch (Exception e) {
                log.error(String.format("Returning null and ignoring exception that occured while processing stream with function %s, parameter %s",
                    wrappedFunction, t), e);
                return null;
            }
        };
    }

}

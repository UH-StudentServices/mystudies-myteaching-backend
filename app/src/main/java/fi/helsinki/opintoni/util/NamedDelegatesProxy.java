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

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class NamedDelegatesProxy<T> extends DelegatingProxy<T> {

    private final Supplier<String> delegateSelector;
    private final Map<String, T> delegates;

    public NamedDelegatesProxy(
        Supplier<String> delegateSelector,
        Map<String, T> delegates) {
        this.delegateSelector = delegateSelector;
        this.delegates = delegates;
    }

    @Override
    protected T getDelegate() {
        return Optional.ofNullable(delegateSelector.get())
            .map(delegates::get)
            .orElseThrow(() ->
                new IllegalArgumentException("No delegate registered with key " + delegateSelector.get()));
    }

    public static <T> Builder<T> builder(Class<T> clazz, Supplier<String> delegateSelector) {
        return new Builder<>(clazz, delegateSelector);
    }

    public static class Builder<T> {

        private final Supplier<String> delegateSelector;
        private final Class<T> clazz;
        private final Map<String, T> delegates = new HashMap<>();

        private Builder(Class<T> clazz, Supplier<String> delegateSelector) {
            this.delegateSelector = delegateSelector;
            this.clazz = clazz;
        }

        public Builder<T> with(String name, T delegate) {
            delegates.put(name, delegate);
            return this;
        }

        @SuppressWarnings("unchecked")
        public T build() {
            return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new NamedDelegatesProxy<>(
                    delegateSelector,
                    delegates));
        }

    }
}

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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NamedDelegatesProxyTest {

    private final DelegateSelector delegateSelector = new DelegateSelector();

    private final MyCalculator calculator =
            NamedDelegatesProxy.builder(MyCalculator.class, delegateSelector::getDelegateName)
                    .with("casio", new MyCasio())
                    .with("ludotronic", new MyLudotronic())
                    .build();

    @Test
    public void thatChangingDelegateWorks() {

        delegateSelector.setDelegateName("casio");
        assertThat(calculator.add(3, 4)).isEqualTo(7);

        delegateSelector.setDelegateName("ludotronic");
        assertThat(calculator.add(3, 4)).isEqualTo(12);
    }

    @Test(expected = IllegalArgumentException.class)
    public void thatChangingToNonExistentDelegateThrowsAnException() {

        delegateSelector.setDelegateName("Henning Dynesen");
        assertThat(calculator.add(3, 4)).isEqualTo(7);
    }

    interface MyCalculator {

        int add(int a, int b);
    }

    private static class MyCasio implements MyCalculator {

        @Override
        public int add(int a, int b) {
            return a + b;
        }
    }

    private static class MyLudotronic implements MyCalculator {

        @Override
        public int add(int a, int b) {
            return a * b;
        }
    }

    private static class DelegateSelector {

        private String delegateName = "casio";

        public String getDelegateName() {
            return delegateName;
        }

        public void setDelegateName(String delegateName) {
            this.delegateName = delegateName;
        }
    }

}

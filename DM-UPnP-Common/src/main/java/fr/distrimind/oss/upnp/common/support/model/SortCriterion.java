/*
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/*
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/*
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package fr.distrimind.oss.upnp.common.support.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class SortCriterion {

    final protected boolean ascending;
    final protected String propertyName;

    public SortCriterion(boolean ascending, String propertyName) {
        this.ascending = ascending;
        this.propertyName = propertyName;
    }

    public SortCriterion(String criterion) {
        this(criterion.startsWith("+"), criterion.substring(1));
        if (!(criterion.startsWith("-") || criterion.startsWith("+")))
            throw new IllegalArgumentException("Missing sort prefix +/- on criterion: " + criterion);
    }

    public boolean isAscending() {
        return ascending;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public static List<SortCriterion> valueOf(String s) {
        if (s == null || s.isEmpty()) return Collections.emptyList();
        List<SortCriterion> list = new ArrayList<>();
        String[] criteria = s.split(",");
        for (String criterion : criteria) {
            list.add(new SortCriterion(criterion.trim()));
        }
        return list;
    }

    public static String toString(SortCriterion[] criteria) {
        if (criteria == null) return "";
        StringBuilder sb = new StringBuilder();
        for (SortCriterion sortCriterion : criteria) {
            sb.append(sortCriterion.toString()).append(",");
        }
        if (sb.toString().endsWith(",")) sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    @Override
    public String toString() {
		return (ascending ? "+" : "-") +
				propertyName;
    }
}

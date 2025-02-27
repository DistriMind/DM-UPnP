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
 */ /*
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
 */ /*
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
 package fr.distrimind.oss.upnp.swing.logging;

import fr.distrimind.oss.flexilogxml.log.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class LogCategory {

    private final String name;
    private List<Group> groups = new ArrayList<>();

    public LogCategory(String name) {
        this.name = name;
    }

    public LogCategory(String name, Group[] groups) {
        this.name = name;
        this.groups = Arrays.asList(groups);
    }

    public String getName() {
        return name;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void addGroup(String name, LoggerLevel[] loggerLevels) {
        groups.add(new Group(name, loggerLevels));
    }

    static public class Group {

        private final String name;
        private List<LoggerLevel> loggerLevels = new ArrayList<>();
        private List<LoggerLevel> previousLevels  = new ArrayList<>();
        private boolean enabled;

        public Group(String name) {
            this.name = name;
        }

        public Group(String name, LoggerLevel[] loggerLevels) {
            this.name = name;
            this.loggerLevels = Arrays.asList(loggerLevels);
        }

        public String getName() {
            return name;
        }

        public List<LoggerLevel> getLoggerLevels() {
            return loggerLevels;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<LoggerLevel> getPreviousLevels() {
            return previousLevels;
        }

        public void setPreviousLevels(List<LoggerLevel> previousLevels) {
            this.previousLevels = previousLevels;
        }
    }

    static public class LoggerLevel {
        private final String logger;
        private final Level level;

        public LoggerLevel(String logger, Level level) {
            this.logger = logger;
            this.level = level;
        }

        public String getLogger() {
            return logger;
        }

        public Level getLevel() {
            return level;
        }
    }
}
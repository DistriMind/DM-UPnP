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

package fr.distrimind.oss.upnp.common.support.shared;

import fr.distrimind.oss.upnp.common.swing.Controller;

import javax.swing.JFrame;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Christian Bauer
 */
public class PlatformApple {

    public static void setup(final Controller<JFrame> appController, String appName) throws Exception {

        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
        System.setProperty("apple.awt.showGrowBox", "true");

        // Use reflection to avoid compile-time dependency
        Class<?> appClass = Class.forName("com.apple.eawt.Application");
        Object application = appClass.getConstructor().newInstance();
        Class<?> listenerClass = Class.forName("com.apple.eawt.ApplicationListener");
        Method addAppListmethod = appClass.getDeclaredMethod("addApplicationListener", listenerClass);

        // creating and adding a custom adapter/listener to the Application
        Class<?> adapterClass = Class.forName("com.apple.eawt.ApplicationAdapter");
        Object listener = AppListenerProxy.newInstance(adapterClass.getConstructor().newInstance(), appController);
        addAppListmethod.invoke(application, listener);
    }

    static class AppListenerProxy implements InvocationHandler {

        private final Controller<JFrame> appController;
        private final Object object;

        public static Object newInstance(Object obj, Controller<JFrame> appController) {
            return Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    obj.getClass().getInterfaces(),
                    new AppListenerProxy(obj, appController)
            );
        }

        private AppListenerProxy(Object obj, Controller<JFrame> appController) {
            this.object = obj;
            this.appController = appController;
        }

        @Override
		public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
            Object result = null;
            try {
                if ("handleQuit".equals(m.getName())) {
                    if (appController != null) {
                        appController.dispose();
                        appController.getView().dispose();
                    }
                } else {
                    result = m.invoke(object, args);
                }
            } catch (Exception ignored) {
                // Ignore
            }
            return result;
        }

    }

}

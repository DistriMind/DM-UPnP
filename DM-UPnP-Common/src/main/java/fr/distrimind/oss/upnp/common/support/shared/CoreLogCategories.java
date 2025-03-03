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

import fr.distrimind.oss.upnp.common.binding.xml.DeviceDescriptorBinder;
import fr.distrimind.oss.upnp.common.binding.xml.ServiceDescriptorBinder;
import fr.distrimind.oss.upnp.common.model.DefaultServiceManager;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.protocol.ProtocolFactory;
import fr.distrimind.oss.upnp.common.protocol.RetrieveRemoteDescriptors;
import fr.distrimind.oss.upnp.common.protocol.sync.*;
import fr.distrimind.oss.upnp.common.registry.Registry;
import fr.distrimind.oss.upnp.common.swing.logging.LogCategory;
import fr.distrimind.oss.flexilogxml.log.Level;
import fr.distrimind.oss.upnp.common.model.message.UpnpHeaders;
import fr.distrimind.oss.upnp.common.transport.Router;
import fr.distrimind.oss.upnp.common.transport.spi.*;

import java.util.ArrayList;

/**
 * @author Christian Bauer
 */
public class CoreLogCategories extends ArrayList<LogCategory> {
    private static final long serialVersionUID = 1L;

    public CoreLogCategories() {
        super(10);

        add(new LogCategory("Network", new LogCategory.Group[]{

                new LogCategory.Group(
                        "UDP communication",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(DatagramIO.class.getName(), Level.DEBUG),
                                new LogCategory.LoggerLevel(MulticastReceiver.class.getName(), Level.DEBUG),
                        }
                ),

                new LogCategory.Group(
                        "UDP datagram processing and content",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(DatagramProcessor.class.getName(), Level.TRACE)
                        }
                ),

                new LogCategory.Group(
                        "TCP communication",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(UpnpStream.class.getName(), Level.TRACE),
                                new LogCategory.LoggerLevel(StreamServer.class.getName(), Level.DEBUG),
                                new LogCategory.LoggerLevel(StreamClient.class.getName(), Level.DEBUG),
                        }
                ),

                new LogCategory.Group(
                        "SOAP action message processing and content",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(SOAPActionProcessor.class.getName(), Level.TRACE)
                        }
                ),

                new LogCategory.Group(
                        "GENA event message processing and content",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(GENAEventProcessor.class.getName(), Level.TRACE)
                        }
                ),

                new LogCategory.Group(
                        "HTTP header processing",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(UpnpHeaders.class.getName(), Level.TRACE)
                        }
                ),
        }));


        add(new LogCategory("UPnP Protocol", new LogCategory.Group[]{

                new LogCategory.Group(
                        "Discovery (Notification & Search)",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(ProtocolFactory.class.getName(), Level.TRACE),
                                new LogCategory.LoggerLevel("fr.distrimind.oss.upnp.common.protocol.async", Level.TRACE)
                        }
                ),

                new LogCategory.Group(
                        "Description",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(ProtocolFactory.class.getName(), Level.TRACE),
                                new LogCategory.LoggerLevel(RetrieveRemoteDescriptors.class.getName(), Level.DEBUG),
                                new LogCategory.LoggerLevel(ReceivingRetrieval.class.getName(), Level.DEBUG),
                                new LogCategory.LoggerLevel(DeviceDescriptorBinder.class.getName(), Level.DEBUG),
                                new LogCategory.LoggerLevel(ServiceDescriptorBinder.class.getName(), Level.DEBUG),
                        }
                ),

                new LogCategory.Group(
                        "Control",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(ProtocolFactory.class.getName(), Level.TRACE),
                                new LogCategory.LoggerLevel(ReceivingAction.class.getName(), Level.TRACE),
                                new LogCategory.LoggerLevel(SendingAction.class.getName(), Level.TRACE),
                        }
                ),

                new LogCategory.Group(
                        "GENA ",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel("fr.distrimind.oss.upnp.common.model.gena", Level.TRACE),
                                new LogCategory.LoggerLevel(ProtocolFactory.class.getName(), Level.TRACE),
                                new LogCategory.LoggerLevel(ReceivingEvent.class.getName(), Level.TRACE),
                                new LogCategory.LoggerLevel(ReceivingSubscribe.class.getName(), Level.TRACE),
                                new LogCategory.LoggerLevel(ReceivingUnsubscribe.class.getName(), Level.TRACE),
                                new LogCategory.LoggerLevel(SendingEvent.class.getName(), Level.TRACE),
                                new LogCategory.LoggerLevel(SendingSubscribe.class.getName(), Level.TRACE),
                                new LogCategory.LoggerLevel(SendingUnsubscribe.class.getName(), Level.TRACE),
                                new LogCategory.LoggerLevel(SendingRenewal.class.getName(), Level.TRACE),
                        }
                ),
        }));

        add(new LogCategory("Core", new LogCategory.Group[]{

                new LogCategory.Group(
                        "Router",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(Router.class.getName(), Level.TRACE)
                        }
                ),

                new LogCategory.Group(
                        "Registry",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(Registry.class.getName(), Level.TRACE),
                        }
                ),

                new LogCategory.Group(
                        "Local service binding & invocation",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel("fr.distrimind.oss.upnp.common.binding.annotations", Level.TRACE),
                                new LogCategory.LoggerLevel(LocalService.class.getName(), Level.TRACE),
                                new LogCategory.LoggerLevel("fr.distrimind.oss.upnp.common.model.action", Level.TRACE),
                                new LogCategory.LoggerLevel("fr.distrimind.oss.upnp.common.model.state", Level.TRACE),
                                new LogCategory.LoggerLevel(DefaultServiceManager.class.getName(), Level.TRACE)
                        }
                ),

                new LogCategory.Group(
                        "Control Point interaction",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel("fr.distrimind.oss.upnp.common.controlpoint", Level.TRACE),
                        }
                ),
        }));

    }

}

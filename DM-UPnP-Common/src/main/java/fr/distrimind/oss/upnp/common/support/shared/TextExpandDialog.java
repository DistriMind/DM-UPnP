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

import fr.distrimind.oss.upnp.common.Log;
import fr.distrimind.oss.upnp.common.model.ModelUtil;
import fr.distrimind.oss.upnp.common.model.XMLUtil;
import fr.distrimind.oss.upnp.common.swing.Application;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.flexilogxml.common.xml.IXmlReader;
import fr.distrimind.oss.flexilogxml.common.xml.IXmlWriter;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Christian Bauer
 */
public class TextExpandDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    // TODO: Make this a plugin SPI and let the plugin impl decide how text should be detected and rendered

    final private static DMLogger log = Log.getLogger(TextExpandDialog.class);

    public TextExpandDialog(Frame frame, String text) {
        super(frame);
        setResizable(true);

        JTextArea textArea = new JTextArea();
        JScrollPane textPane = new JScrollPane(textArea);
        textPane.setPreferredSize(new Dimension(500, 400));
        add(textPane);

        String pretty;
        if (text.startsWith("<") && text.endsWith(">")) {


            try {
                IXmlReader reader=XMLUtil.getXMLReader(text);
                try(ByteArrayOutputStream out=new ByteArrayOutputStream()) {
                    IXmlWriter writer = XMLUtil.getXMLWriter(true, out);
                    reader.transferTo(writer);
                    writer.close();
                    reader.close();
                    out.flush();
                    pretty=new String(out.toByteArray(), StandardCharsets.UTF_8);
                }
            } catch (Exception ex) {
                log.error(() -> "Error pretty printing XML: " + ex);
                pretty = text;
            }
        } else if (text.startsWith("http-get")) {
            pretty = ModelUtil.commaToNewline(text);
        } else {
            pretty = text;
        }

        textArea.setEditable(false);
        textArea.setText(pretty);

        pack();
        Application.center(this, getOwner());
        setVisible(true);
    }
}

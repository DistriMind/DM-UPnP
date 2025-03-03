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

package fr.distrimind.oss.upnp.common.transport.impl;

import java.util.*;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

import fr.distrimind.oss.upnp.common.model.action.ActionArgumentValue;
import fr.distrimind.oss.upnp.common.model.action.ActionException;
import fr.distrimind.oss.upnp.common.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.common.model.message.control.ActionRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.control.ActionResponseMessage;
import fr.distrimind.oss.upnp.common.model.meta.ActionArgument;
import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.model.types.ErrorCode;
import fr.distrimind.oss.upnp.common.transport.spi.SOAPActionProcessor;
import fr.distrimind.oss.upnp.common.model.UnsupportedDataException;
import fr.distrimind.oss.upnp.common.xml.XmlPullParserUtils;

import jakarta.enterprise.inject.Alternative;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation based on the <em>Xml Pull Parser</em> XML processing API.
 * <p>
 * This processor is more lenient with parsing, looking only for the required XML tags.
 * </p>
 * <p>
 * To use this parser you need to install an implementation of the
 * <a href="http://www.xmlpull.org/impls.shtml">XMLPull API</a>.
 * </p>
 *
 * @author Michael Pujos
 */
@Alternative
public class PullSOAPActionProcessorImpl extends SOAPActionProcessorImpl {

    final private static DMLogger log = Log.getLogger(SOAPActionProcessor.class);

    @Override
	public <S extends Service<?, ?, ?>> void readBody(ActionRequestMessage requestMessage, ActionInvocation<S> actionInvocation) throws UnsupportedDataException {
        String body = getMessageBody(requestMessage);
        try {
            Document doc= Jsoup.parse(body, "", Parser.xmlParser());
            readBodyRequest(doc, actionInvocation);
        } catch (Exception ex) {
            throw new UnsupportedDataException("Can't transform message payload: " + ex, ex, body);
        }
    }

    @Override
	public <S extends Service<?, ?, ?>> void readBody(ActionResponseMessage responseMsg, ActionInvocation<S> actionInvocation) throws UnsupportedDataException {
        String body = getMessageBody(responseMsg);
        try {
            Document doc= Jsoup.parse(body, "", Parser.xmlParser());
            readBodyElement(doc);
            readBodyResponse(doc, actionInvocation);
        }
        catch (UnsupportedDataException e)
        {
            throw e;
        }
        catch (Exception ex) {
            throw new UnsupportedDataException("Can't transform message payload: " + ex, ex, body);
        }
    }

    protected Element readBodyElement(Document d) {
        Element envelopeElement;
        if (d.childrenSize()==0 || !XmlPullParserUtils.tagsEquals((envelopeElement = d.child(0)).tagName(), "Envelope"))
            throw new RuntimeException("Response root element was not 'Envelope'");

        for (Element envelopeChild : envelopeElement.children()) {
            if (XmlPullParserUtils.tagsEquals(envelopeChild.tagName(), "Body")) {
                return envelopeChild;
            }
        }

        throw new RuntimeException("Response envelope did not contain 'Body' child element");
    }

    protected <S extends Service<?, ?, ?>> void readBodyRequest(Document doc, ActionInvocation<S> actionInvocation) throws Exception {
        XmlPullParserUtils.searchTag(doc, actionInvocation.getAction().getName());
        readActionInputArguments(doc, actionInvocation);
    }

    @SuppressWarnings("PMD.LooseCoupling")
    protected <S extends Service<?, ?, ?>> boolean readBodyResponse(Element element,
                                                                           ActionInvocation<S> actionInvocation) throws Exception {
        Elements children = element.children();
        for (Element child : children) {
            if (XmlPullParserUtils.tagsEquals(child.tagName(), "Fault")) {
                ActionException e = readFaultElement(child);
                actionInvocation.setFailure(e);
                return true;
            } else if (XmlPullParserUtils.tagsEquals(child.tagName(), actionInvocation.getAction().getName() + "Response")) {
                readActionOutputArguments(element, actionInvocation);
                return true;
            } else if (readBodyResponse(child, actionInvocation)) {
                return true;
            }
        }
        throw new ActionException(
                ErrorCode.ACTION_FAILED,
                String.format("Action SOAP response do not contain %s element",
                        actionInvocation.getAction().getName() + "Response"
                )
        );
    }

    protected <S extends Service<?, ?, ?>> void readActionInputArguments(Document doc, ActionInvocation<S> actionInvocation) throws Exception {
        actionInvocation.setInput(readArgumentValues(doc, actionInvocation.getAction().getInputArguments()));
    }
    protected <S extends Service<?, ?, ?>> void readActionOutputArguments(Element element,
                                                                          ActionInvocation<S> actionInvocation) throws ActionException {

        actionInvocation.setOutput(
                readArgumentValues(
                        element,
                        actionInvocation.getAction().getOutputArguments()
                )
        );
    }

    @SuppressWarnings("PMD.LooseCoupling")
    private static void getMatchingNodes(Element element, List<String> names, Map<String, String> matches) {
        Elements children = element.children();
        for (Element child : children) {
            String name = child.nodeName();
            if (names.stream().anyMatch(n -> XmlPullParserUtils.tagsEquals(name, n)))
                matches.put(name, child.text());
            getMatchingNodes(child, names, matches);
        }
    }
    private void getMatchingNodes(Node node, List<String> names, Map<String, String> matches) {
        NodeList nl=node.getChildNodes();
        for (int i=0;i<nl.getLength();i++)
        {
            Node n=nl.item(i);
            String name=n.getNodeName().toUpperCase(Locale.ROOT);
            if (names.stream().anyMatch(nm -> XmlPullParserUtils.tagsEquals(name, nm)))
                matches.put(name, n.getTextContent());
            getMatchingNodes(n, names, matches);
        }
    }
    protected <S extends Service<?, ?, ?>> Map<String, String> getMatchingNodes(Node node, List<ActionArgument<S>> args) throws ActionException {

        List<String> names = getNames(args);

        Map<String, String> matches = new HashMap<>();
        getMatchingNodes(node, names, matches);

        if (matches.size() < args.size()) {
            throw new ActionException(
                    ErrorCode.ARGUMENT_VALUE_INVALID,
                    "Invalid number of input or output arguments in XML message, expected "
                            + args.size() + " but found " + matches.size()
            );
        }
        return matches;
    }
    protected <S extends Service<?, ?, ?>> Map<String, String> getMatchingNodes(Element node, List<ActionArgument<S>> args) throws Exception {

        List<String> names = getNames(args);

        Map<String, String> matches = new HashMap<>();
        getMatchingNodes(node, names, matches);

        if (matches.size() < args.size()) {
            throw new ActionException(
                ErrorCode.ARGUMENT_VALUE_INVALID,
                "Invalid number of input or output arguments in XML message, expected "
                    + args.size() + " but found " + matches.size()
            );
        }
        return matches;
    }
    protected <S extends Service<?, ?, ?>> List<ActionArgumentValue<S>> readArgumentValues(Element node, List<ActionArgument<S>> args) throws ActionException {
        try {
            // We're in the <ActionName>Response tag
            Map<String, String> matches = getMatchingNodes(node, args);
            List<ActionArgumentValue<S>> values = new ArrayList<>(args.size());


            for (ActionArgument<S> arg : args) {

                String value = findActionArgumentValue(matches, arg);
                if (value == null) {
                    throw new ActionException(
                            ErrorCode.ARGUMENT_VALUE_INVALID,
                            "Could not find argument '" + arg.getName() + "' node");
                }

				if (log.isDebugEnabled()) {
					log.debug("Reading action argument: " + arg.getName());
				}
				values.add(createValue(arg, value));
            }
            return values;
        }
        catch (ActionException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ActionException(-1, "", e);
        }
    }

    protected <S extends Service<?, ?, ?>>  String findActionArgumentValue(Map<String, String> entries, ActionArgument<S> arg) {
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            if (arg.isNameOrAlias(entry.getKey())) return entry.getValue();
        }
        return null;
    }

    @SuppressWarnings("PMD.LooseCoupling")
    private static boolean readFaultElement(Element element, StringBuilder errorCode, StringBuilder errorDescription) {
        Elements children = element.children();
        for (Element child : children) {
            switch (child.nodeName()) {
                case "errorCode":
                    if (errorCode.length() == 0) {
                        errorCode.append(child.text());
                    }
                    break;
                case "errorDescription":
                    if (errorDescription.length() == 0) {
                        errorDescription.append(child.text());
                    }
                    break;
            }
            if (errorCode.length() > 0 && errorDescription.length() > 0) {
                return true;
            }
            if (readFaultElement(child, errorCode, errorDescription)) {
                return true;
            }
        }
        return false;
    }

    protected ActionException readFaultElement(Element node) throws Exception {
        // We're in the "Fault" tag


        XmlPullParserUtils.searchTag(node, "UPnPError");
        final StringBuilder errorCode = new StringBuilder();
        final StringBuilder errorDescription = new StringBuilder();
        readFaultElement(node, errorCode, errorDescription);

        if (errorCode.length() > 0) {
            try {
                int numericCode = Integer.parseInt(errorCode.toString());
                ErrorCode standardErrorCode = ErrorCode.getByCode(numericCode);
                if (standardErrorCode != null) {
					if (log.isDebugEnabled()) {
						log.debug("Reading fault element: " + standardErrorCode.getCode() + " - " + errorDescription);
					}
					return new ActionException(standardErrorCode, errorDescription.toString(), false);
                } else {
					if (log.isDebugEnabled()) {
						log.debug("Reading fault element: " + numericCode + " - " + errorDescription);
					}
					return new ActionException(numericCode, errorDescription.toString());
                }
            } catch (NumberFormatException ex) {
                throw new RuntimeException("Error code was not a number");
            }
        }

        throw new RuntimeException("Received fault element but no error code : "+" : \n");
    }
}

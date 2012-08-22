//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
   Department of Geography, University of Bonn
 and
   lat/lon GmbH

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
----------------------------------------------------------------------------*/

package org.deegree.crs.configuration.resources;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.deegree.crs.configuration.AbstractCRSProvider;
import org.deegree.crs.exceptions.CRSConfigurationException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.i18n.Messages;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * The <code>XMLFileResource</code> class TODO add class documentation here.
 *
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 *
 * @author last edited by: $Author$
 *
 * @version $Revision$, $Date$
 *
 */
public abstract class XMLFileResource extends XMLFragment implements XMLResource {

    private static ILogger LOG = LoggerFactory.getLogger( XMLFileResource.class );

    private AbstractCRSProvider<Element> provider = null;

    /**
     * @param provider
     *            to use for the reverse lookup of coordinate systems, required
     * @param properties
     *            to read the crs configuration file from, required, a property crs.configuration should be present, if
     *            not the crs.default.configuration property is checked, if this is missing as well, a
     *            {@link NullPointerException} will be thrown.
     * @param requiredRootLocalName
     *            check for the root elements localname, may be <code>null</code>
     * @param requiredNamespace
     *            check for the root elements namespace, may be <code>null</code>
     */
    public XMLFileResource( AbstractCRSProvider<Element> provider, Properties properties, String requiredRootLocalName,
                            String requiredNamespace ) {
        if ( properties == null ) {
            throw new IllegalArgumentException( "The properties may not be null" );
        }
        if ( provider == null ) {
            throw new NullPointerException( "The provider is null, this may not be." );
        }
        String fileName = properties.getProperty( "crs.configuration" );
        Reader read = null;
        InputStream is = null;
        try {

            if ( fileName == null || "".equals( fileName ) ) {
                LOG.logDebug( "No configuration file given, trying to load default file" );
                fileName = properties.getProperty( "crs.default.configuration" );
                if ( fileName == null || "".equals( fileName ) ) {
                    throw new NullPointerException(
                                                    "The CRS_FILE property was not set, this resolver can not function without a file. " );
                }
                is = provider.getClass().getResourceAsStream( "/" + fileName );
                if ( is == null ) {
                    is = provider.getClass().getResourceAsStream( fileName );
                } else {
                    LOG.logDebug( "Using the configuration file loaded from root directory instead of org.deegree.crs.configuration" );
                }
                if ( is == null ) {
                    throw new CRSConfigurationException( Messages.getMessage( "CRS_CONFIG_NO_DEFAULT_CONFIG_FOUND" ) );
                }
            } else {
                LOG.logDebug( "Trying to load configuration from file: " + fileName );
                is = new FileInputStream( fileName );
            }
            read = new BufferedReader( new InputStreamReader( is ) );

            load( read, XMLFragment.DEFAULT_URL );
            if ( getRootElement() == null ) {
                throw new NullPointerException( "The file: " + fileName + " does not contain a root element. " );
            }
            if ( requiredRootLocalName != null && !"".equals( requiredRootLocalName ) ) {
                if ( !requiredRootLocalName.equalsIgnoreCase( getRootElement().getLocalName() ) ) {
                    throw new IllegalArgumentException( "The local name of the root element of the given file is not: "
                                                        + requiredRootLocalName + " aborting." );
                }
            }
            if ( requiredNamespace != null ) {
                if ( !requiredNamespace.equals( getRootElement().getNamespaceURI() ) ) {
                    throw new IllegalArgumentException(
                                                        "The root element of the given file is not in the required namespace: "
                                                                                + requiredNamespace + " aborting." );
                }
            }

        } catch ( SAXException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new IllegalArgumentException( "File: " + fileName + " is an invalid xml file resource because: "
                                                + e.getLocalizedMessage() );
        } catch ( IOException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new IllegalArgumentException( "File: " + fileName + " is an invalid xml file resource because: "
                                                + e.getLocalizedMessage() );
        } finally {
            if ( read != null ) {
                try {
                    read.close();
                } catch ( IOException e ) {
                    LOG.logError( e );
                }
            }
            if ( is != null ) {
                try {
                    is.close();
                } catch ( IOException e ) {
                    LOG.logError( e );
                }
            }
        }
        this.provider = provider;
    }

    /**
     * @param provider
     *            to be used for callback.
     * @param rootElement
     */
    public XMLFileResource( AbstractCRSProvider<Element> provider, Element rootElement ) {
        super( rootElement );
        this.provider = provider;
    }

    /**
     * @return the provider used for reversed look ups, will never be <code>null</code>
     */
    public AbstractCRSProvider<Element> getProvider() {
        return provider;
    }
}

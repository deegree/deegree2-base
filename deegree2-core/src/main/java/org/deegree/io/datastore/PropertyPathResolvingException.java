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
package org.deegree.io.datastore;

import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.ogcbase.PropertyPath;

/**
 * Indicates that a {@link PropertyPath} cannot be resolved against a {@link MappedFeatureType}.
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author$
 *
 * @version $Revision$, $Date$
 */
public class PropertyPathResolvingException extends DatastoreException {

    private static final long serialVersionUID = -9138320617620027534L;

    /**
     * Constructs an instance of <code>PropertyPathResolvingException</code> with the specified
     * detail message.
     *
     * @param msg
     *            the detail message
     */
    public PropertyPathResolvingException( String msg ) {
        super( msg );
    }

    /**
     * Constructs an instance of <code>PropertyPathResolvingException</code> with the specified
     * cause.
     *
     * @param cause
     *            the Throwable that caused this PropertyPathResolvingException
     *
     */
    public PropertyPathResolvingException( Throwable cause ) {
        super( cause );
    }

    /**
     * Constructs an instance of <code>PropertyPathResolvingException</code> with the specified
     * detail message and cause.
     *
     * @param msg
     *            the detail message
     * @param cause
     *            the Throwable that caused this PropertyPathResolvingException
     *
     */
    public PropertyPathResolvingException( String msg, Throwable cause ) {
        super( msg, cause );
    }
}

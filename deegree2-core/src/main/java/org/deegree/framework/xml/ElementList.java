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

package org.deegree.framework.xml;

import java.util.ArrayList;

import org.w3c.dom.Element;

/**
 * Convenience class for easy handling of <code>NodeLists<code> containing only objects of
 * type org.w3c.dom.Element.
 *
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 *
 * @author last edited by: $Author$
 *
 * @version 1.0. $Revision$, $Date$
 */
public class ElementList {
    /**
     * The actual elements this list wraps.
     */
    ArrayList<Element> elements = new ArrayList<Element>( 100 );

    /**
     *
     * @param element
     */
    public void addElement( Element element ) {
        elements.add( element );
    }

    /**
     *
     * @return size of the list
     */
    public int getLength() {
        return elements.size();
    }

    /**
     *
     * @param i
     * @return i-th element or <code>null</code> if i is out of the list bounds
     */
    public Element item( int i ) {
        if ( i < 0 || i > elements.size() - 1 )
            return null;
        return elements.get( i );
    }
}

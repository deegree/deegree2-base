// $HeadURL$
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
package org.deegree.ogcwebservices.wcs.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * models a <tt>Resolution</tt> that describes the access to the coverages using named files (with
 * a defined size if it's a grid coverage).
 *
 * @version $Revision$
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 *
 * @version $Revision$, $Date$
 */
public class FileResolution extends AbstractResolution {

    private List<File> files = null;

    /**
     * @param minScale
     * @param maxScale
     * @param range
     * @throws IllegalArgumentException
     */
    public FileResolution( double minScale, double maxScale, Range[] range, File[] files )
                            throws IllegalArgumentException {
        super( minScale, maxScale, range );
        setFiles( files );
    }

    /**
     * sets all file description contained in the <tt>Resolution</tt>
     *
     * @param files
     */
    public void setFiles( File[] files ) {
        this.files = new ArrayList<File>( Arrays.asList( files ) );
    }

    /**
     * returns all file description contained in the <tt>Resolution</tt>
     *
     * @return all file description contained in the <tt>Resolution</tt>
     *
     */
    public File[] getFiles() {
        return files.toArray( new File[files.size()] );
    }

    /**
     * adds a files descrition to the <tt>Resolution</tt>
     *
     * @param file
     */
    public void addFile( File file ) {
        files.add( file );
    }

    /**
     * removes a files descrition from the <tt>Resolution</tt>
     *
     * @param file
     */
    public void removeFile( File file ) {
        files.remove( file );
    }

}

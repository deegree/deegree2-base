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

package org.deegree.crs.components;

import static org.deegree.crs.projections.ProjectionUtils.EPS11;

import java.io.Serializable;

import org.deegree.crs.Identifiable;

/**
 * The <code>PrimeMeridian</code> class saves the longitude to the greenwich meridian.
 *
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 *
 * @author last edited by: $Author$
 *
 * @version $Revision$, $Date$
 *
 */

public class PrimeMeridian extends Identifiable implements Serializable {

    private static final long serialVersionUID = -3719400673666655763L;

    private double longitude;

    private Unit units;

    /**
     * The PrimeMeridian of greenwich with epsg:8901 code and radian units.
     */
    public static final PrimeMeridian GREENWICH = new PrimeMeridian(
                                                                     Unit.DEGREE,
                                                                     0,
                                                                     new String[] {
                                                                                   "EPSG:8901",
                                                                                   "HTTP://WWW.OPENGIS.NET/GML/SRS/EPSG.XML#8901",
                                                                                   "URN:OPENGIS:DEF:CRS:EPSG::8901",
                                                                                   "URN:OGC:DEF:CRS:EPSG::8901" },
                                                                     new String[] { "Greenwich" },
                                                                     new String[] { "1995-06-02" }, null, null );

    /**
     * @param units
     *            Angular units of longitude, normally radians.
     * @param longitude
     *            (in given units) normally radians.
     * @param id
     *            to be cloned
     */
    public PrimeMeridian( Unit units, double longitude, Identifiable id ) {
        super( id );
        this.units = units;
        this.longitude = longitude;
    }

    /**
     * @param units
     *            Angular units of longitude, normally radians.
     * @param longitude
     *            (in given units) normally radians.
     * @param identifiers
     * @param names
     * @param versions
     * @param descriptions
     * @param areasOfUse
     */
    public PrimeMeridian( Unit units, double longitude, String[] identifiers, String[] names, String[] versions,
                          String[] descriptions, String[] areasOfUse ) {
        this( units, longitude, new Identifiable( identifiers, names, versions, descriptions, areasOfUse ) );
    }

    /**
     * @param units
     *            Angular units of longitude.
     * @param longitude
     * @param identifier
     * @param name
     * @param version
     * @param description
     * @param areaOfUse
     */
    public PrimeMeridian( Unit units, double longitude, String identifier, String name, String version,
                          String description, String areaOfUse ) {
        this( units, longitude, new String[] { identifier }, new String[] { name }, new String[] { version },
              new String[] { description }, new String[] { areaOfUse } );
    }

    /**
     * @param units
     *            Angular units of longitude.
     * @param longitude
     * @param identifiers
     */
    public PrimeMeridian( Unit units, double longitude, String[] identifiers ) {
        this( units, longitude, identifiers, null, null, null, null );
    }

    /**
     * @param units
     *            Angular units of longitude.
     * @param longitude
     * @param identifier
     */
    public PrimeMeridian( Unit units, double longitude, String identifier ) {
        this( units, longitude, new String[] { identifier } );
    }

    /**
     * A Prime meredian with 0 degrees longitude from the greenwich meridian.
     *
     * @param units
     *            Angular units of longitude.
     * @param identifiers
     */
    public PrimeMeridian( Unit units, String[] identifiers ) {
        this( units, 0, identifiers );
    }

    /**
     * A Prime meredian with 0 degrees longitude from the greenwich meridian.
     *
     * @param units
     *            Angular units of longitude.
     * @param identifier
     * @param name
     *            human readable name
     */
    public PrimeMeridian( Unit units, String identifier, String name ) {
        this( units, 0, new String[] { identifier }, new String[] { name }, null, null, null );
    }

    /**
     * @return the longitude value relative to the Greenwich Meridian. The longitude is expressed in this objects
     *         angular units.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param targetUnit
     *            The unit in which to express longitude.
     * @return the longitude value relative to the Greenwich Meridian, expressed in the specified units. This
     *         convenience method make easier to obtains longitude in degrees (<code>getLongitude(Unit.DEGREE)</code>),
     *         no matter the underlying angular unit of this prime meridian.
     */
    public double getLongitude( final Unit targetUnit ) {
        return getAngularUnit().convert( getLongitude(), targetUnit );
    }

    /**
     * @return the longitude value relative to the Greenwich Meridian, expressed in the radians.
     */
    public double getLongitudeAsRadian() {
        return getAngularUnit().convert( getLongitude(), Unit.RADIAN );
    }

    /**
     * @return the angular unit.
     */
    public Unit getAngularUnit() {
        return units;
    }

    @Override
    public boolean equals( Object other ) {
        if ( other != null && other instanceof PrimeMeridian ) {
            PrimeMeridian that = (PrimeMeridian) other;
            return ( Math.abs( this.longitude - that.longitude ) < EPS11 )
                   && ( ( this.units != null ) ? this.units.equals( that.units ) : ( that.units == null ) )
                   && super.equals( that );
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder( super.toString() );
        sb.append( ", Units: " ).append( units );
        sb.append( ", longitude: " ).append( longitude );
        return sb.toString();
    }

    /**
     * Implementation as proposed by Joshua Block in Effective Java (Addison-Wesley 2001), which supplies an even
     * distribution and is relatively fast. It is created from field <b>f</b> as follows:
     * <ul>
     * <li>boolean -- code = (f ? 0 : 1)</li>
     * <li>byte, char, short, int -- code = (int)f</li>
     * <li>long -- code = (int)(f ^ (f &gt;&gt;&gt;32))</li>
     * <li>float -- code = Float.floatToIntBits(f);</li>
     * <li>double -- long l = Double.doubleToLongBits(f); code = (int)(l ^ (l &gt;&gt;&gt; 32))</li>
     * <li>all Objects, (where equals(&nbsp;) calls equals(&nbsp;) for this field) -- code = f.hashCode(&nbsp;)</li>
     * <li>Array -- Apply above rules to each element</li>
     * </ul>
     * <p>
     * Combining the hash code(s) computed above: result = 37 * result + code;
     * </p>
     *
     * @return (int) ( result >>> 32 ) ^ (int) result;
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        // the 2.nd million th. prime, :-)
        long code = 32452843;
        long ll = Double.doubleToLongBits( longitude );
        code = code * 37 + (int) ( ll ^ ( ll >>> 32 ) );
        if ( units != null ) {
            code = code * 37 + units.hashCode();
        }
        return (int) ( code >>> 32 ) ^ (int) code;
    }

}

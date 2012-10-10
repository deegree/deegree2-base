//$HeadURL: svn+ssh://lbuesching@svn.wald.intevation.de/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2012 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

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
package org.deegree.crs.transformations;

import javax.vecmath.Point3d;

import org.deegree.crs.coordinatesystems.GeocentricCRS;
import org.deegree.crs.coordinatesystems.GeographicCRS;
import org.deegree.crs.exceptions.TransformationException;
import org.deegree.crs.transformations.coordinate.GeocentricTransform;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CRSTransformationException;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;

/**
 * Tests transformations from and to epsg:900913 and 3857
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GoogleTransformationTest extends TransformationAccuracy {

    public void test4326To900913()
                            throws CRSTransformationException, UnknownCRSException {
        // http://alastaira.wordpress.com/2011/01/23/the-google-maps-bing-maps-spherical-mercator-projection/
        CoordinateSystem sourceCRS = CRSFactory.create( "EPSG:4326" );
        CoordinateSystem targetCRS = CRSFactory.create( "EPSG:900913" );

        Point3d sourcePoint = new Point3d( -5.625, 52.4827802220782, Double.NaN );
        Point3d targetPoint = new Point3d( -626172.1357121646, 6887893.4928337997, Double.NaN );

        doForwardAndInverse( sourceCRS.getCRS(), targetCRS.getCRS(), sourcePoint, targetPoint, epsilon, epsilonDegree );
    }

    public void test4326To3857()
                            throws CRSTransformationException, UnknownCRSException {
        // http://alastaira.wordpress.com/2011/01/23/the-google-maps-bing-maps-spherical-mercator-projection/
        CoordinateSystem sourceCRS = CRSFactory.create( "EPSG:4326" );
        CoordinateSystem targetCRS = CRSFactory.create( "EPSG:3857" );

        Point3d sourcePoint = new Point3d( -5.625, 52.4827802220782, Double.NaN );
        // from http://alastaira.wordpress.com/2011/01/23/the-google-maps-bing-maps-spherical-mercator-projection/
        // (second version)
        // 853955.508199729
        // from gdaltransform -s_srs epsg:4326 -t_srs 'PROJCS["Popular Visualisation CRS / Mercator",
        // GEOGCS["Popular Visualisation CRS",
        // DATUM["WGS84",
        // SPHEROID["WGS84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7059"]],
        // AUTHORITY["EPSG","6055"]],
        // PRIMEM["Greenwich", 0, AUTHORITY["EPSG", "8901"]],
        // UNIT["degree", 0.0174532925199433, AUTHORITY["EPSG", "9102"]],
        // AXIS["E", EAST], AXIS["N", NORTH], AUTHORITY["EPSG","4055"]],
        // PROJECTION["Mercator_1SP"],
        // PARAMETER["semi_minor",6378137],
        // PARAMETER["False_Easting", 0],
        // PARAMETER["False_Northing", 0],
        // PARAMETER["Central_Meridian", 0],
        // PARAMETER["Latitude_of_origin", 0],
        // UNIT["metre", 1, AUTHORITY["EPSG", "9001"]],
        // AXIS["East", EAST], AXIS["North", NORTH],
        // AUTHORITY["EPSG","3785"]]'
        // -626172.135712163 6853979.30081686 0

        Point3d targetPoint = new Point3d( -626172.13571216376, 6853979.30081686, Double.NaN );

        doForwardAndInverse( sourceCRS.getCRS(), targetCRS.getCRS(), sourcePoint, targetPoint, epsilon, epsilonDegree );
    }

    // TODO: fix this with proper reference coordinates 
    public void _test31466To900913()
                            throws CRSTransformationException, UnknownCRSException {
        CoordinateSystem sourceCRS = CRSFactory.create( "EPSG:31466" );
        CoordinateSystem targetCRS = CRSFactory.create( "EPSG:900913" );

        Point3d sourcePoint = new Point3d( 2365253.9171053073, 5838184.758315763, Double.NaN );
        Point3d targetPoint = new Point3d( 446151.3304431711, 6920317.766061135, Double.NaN );

        doForwardAndInverse( sourceCRS.getCRS(), targetCRS.getCRS(), sourcePoint, targetPoint, epsilon, epsilon );
    }

    public void testGeocentricGeographic4314()
                            throws UnknownCRSException, TransformationException {
        GeographicCRS sourceCRS = (GeographicCRS) CRSFactory.create( "EPSG:4314" ).getCRS();
        GeocentricCRS targetCRS = new GeocentricCRS( sourceCRS.getGeodeticDatum(), "test", "geocentric" );

        GeocentricTransform geocentricTransform = new GeocentricTransform( sourceCRS, targetCRS );
        Point3d sourcePoint = new Point3d( 0.0699501014862445, 0.919087024656683, Double.NaN );

        Point3d transformedPoint = geocentricTransform.doTransform( sourcePoint );

        geocentricTransform.inverse();
        Point3d inversedPoint = geocentricTransform.doTransform( transformedPoint );

        assertEquals( sourcePoint.getX(), inversedPoint.getX(), 0.00001 );
        assertEquals( sourcePoint.getY(), inversedPoint.getY(), 0.00001 );
    }

    public void testGeocentricGeographicGoogleMaps()
                            throws UnknownCRSException, TransformationException {
        GeographicCRS sourceCRS = (GeographicCRS) CRSFactory.create( "GOOGLE_MAPS_GEOGRAPHICCRS" ).getCRS();
        GeocentricCRS targetCRS = new GeocentricCRS( sourceCRS.getGeodeticDatum(), "test", "geocentric" );

        GeocentricTransform geocentricTransform = new GeocentricTransform( sourceCRS, targetCRS );
        Point3d sourcePoint = new Point3d( 0.0699501014862445, 0.919087024656683, Double.NaN );

        Point3d transformedPoint = geocentricTransform.doTransform( sourcePoint );

        geocentricTransform.inverse();
        Point3d inversedPoint = geocentricTransform.doTransform( transformedPoint );

        assertEquals( sourcePoint.getX(), inversedPoint.getX(), 0.00001 );
        assertEquals( sourcePoint.getY(), inversedPoint.getY(), 0.00001 );
    }
}

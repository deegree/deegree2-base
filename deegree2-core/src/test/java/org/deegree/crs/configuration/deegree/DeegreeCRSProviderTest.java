//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/test/junit/org/deegree/crs/configuration/deegree/DeegreeCRSProviderTest.java $
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

package org.deegree.crs.configuration.deegree;

import junit.framework.TestCase;

import org.deegree.crs.components.Axis;
import org.deegree.crs.components.Ellipsoid;
import org.deegree.crs.components.GeodeticDatum;
import org.deegree.crs.components.PrimeMeridian;
import org.deegree.crs.components.Unit;
import org.deegree.crs.configuration.CRSConfiguration;
import org.deegree.crs.configuration.CRSProvider;
import org.deegree.crs.coordinatesystems.CoordinateSystem;
import org.deegree.crs.coordinatesystems.GeographicCRS;
import org.deegree.crs.coordinatesystems.ProjectedCRS;
import org.deegree.crs.projections.Projection;
import org.deegree.crs.projections.cylindric.TransverseMercator;
import org.deegree.crs.transformations.helmert.Helmert;

/**
 * <code>GMLCRSProviderTest</code> test the loading of a projected crs as well as the loading of the default
 * configuration.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * 
 * @author last edited by: $Author: rbezema $
 * 
 * @version $Revision: 18325 $, $Date: 2009-07-06 14:25:50 +0200 (Mo, 06 Jul 2009) $
 * 
 */
public class DeegreeCRSProviderTest extends TestCase {

    /**
     * Tries to load the configuration
     */
    public void testLoadingConfiguration() {
        CRSProvider provider = CRSConfiguration.getCRSConfiguration().getProvider();
        assertNotNull( provider );
        assertTrue( provider instanceof DeegreeCRSProvider );
        DeegreeCRSProvider dProvider = (DeegreeCRSProvider) provider;
        assertTrue( dProvider.canExport() );
    }

    /**
     * Tries to create a crs by id.
     */
    public void testCRSByID() {
        CRSProvider provider = CRSConfiguration.getCRSConfiguration().getProvider();
        assertNotNull( provider );
        assertTrue( provider instanceof DeegreeCRSProvider );
        DeegreeCRSProvider dProvider = (DeegreeCRSProvider) provider;
        // try loading the gaus krueger zone 2. (transverse mercator)
        CoordinateSystem testCRS = dProvider.getCRSByID( "EPSG:31466" );
        testCRS_31466( testCRS );
        testCRS = dProvider.getCRSByID( "SOME_DUMMY_CODE" );
        assertTrue( testCRS == null );
        // test mercator reading
        testCRS = dProvider.getCRSByID( "EPSG:3395" );
        assertTrue( testCRS != null );

        // stereographic alternative
        testCRS = dProvider.getCRSByID( "EPSG:2172" );
        assertTrue( testCRS != null );

        // lambertAzimuthal
        testCRS = dProvider.getCRSByID( "EPSG:2163" );
        assertTrue( testCRS != null );

        // lambert conformal conic
        testCRS = dProvider.getCRSByID( "EPSG:2851" );
        assertTrue( testCRS != null );
    }

    private void testCRS_31466( CoordinateSystem testCRS ) {
        assertNotNull( testCRS );
        assertTrue( testCRS instanceof ProjectedCRS );
        ProjectedCRS realCRS = (ProjectedCRS) testCRS;
        assertNotNull( realCRS.getProjection() );
        Projection projection = realCRS.getProjection();
        assertTrue( projection instanceof TransverseMercator );
        // do stuff with projection
        TransverseMercator proj = (TransverseMercator) projection;
        assertEquals( 0.0, proj.getProjectionLatitude() );
        assertEquals( Math.toRadians( 6.0 ), proj.getProjectionLongitude() );
        assertEquals( 1.0, proj.getScale() );
        assertEquals( 2500000.0, proj.getFalseEasting() );
        assertEquals( 0.0, proj.getFalseNorthing() );
        assertTrue( proj.getHemisphere() );

        // test the datum.
        GeodeticDatum datum = realCRS.getGeodeticDatum();
        assertNotNull( datum );
        assertEquals( "EPSG:6314", datum.getIdentifier() );
        assertEquals( PrimeMeridian.GREENWICH, datum.getPrimeMeridian() );

        // test the ellips
        Ellipsoid ellips = datum.getEllipsoid();
        assertNotNull( ellips );
        assertEquals( "EPSG:7004", ellips.getIdentifier() );
        assertEquals( Unit.METRE, ellips.getUnits() );
        assertEquals( 6377397.155, ellips.getSemiMajorAxis() );
        assertEquals( 299.1528128, ellips.getInverseFlattening() );

        // test towgs84 params
        Helmert toWGS = datum.getWGS84Conversion();
        assertNotNull( toWGS );
        assertTrue( toWGS.hasValues() );
        assertEquals( "EPSG:1777", toWGS.getIdentifier() );
        assertEquals( 598.1, toWGS.dx );
        assertEquals( 73.7, toWGS.dy );
        assertEquals( 418.2, toWGS.dz );
        assertEquals( 0.202, toWGS.ex );
        assertEquals( 0.045, toWGS.ey );
        assertEquals( -2.455, toWGS.ez );
        assertEquals( 6.7, toWGS.ppm );

        // test the geographic
        GeographicCRS geographic = realCRS.getGeographicCRS();
        assertNotNull( geographic );
        assertEquals( "EPSG:4314", geographic.getIdentifier() );
        Axis[] ax = geographic.getAxis();
        assertEquals( 2, ax.length );
        assertEquals( Axis.AO_EAST, ax[0].getOrientation() );
        assertEquals( Unit.DEGREE, ax[0].getUnits() );
        assertEquals( Axis.AO_NORTH, ax[1].getOrientation() );
        assertEquals( Unit.DEGREE, ax[1].getUnits() );
    }

    /**
     * Test a cache
     */
    public void testCache() {
        CRSProvider provider = CRSConfiguration.getCRSConfiguration().getProvider();
        assertNotNull( provider );
        assertTrue( provider instanceof DeegreeCRSProvider );
        DeegreeCRSProvider dProvider = (DeegreeCRSProvider) provider;

        CoordinateSystem testCRS = dProvider.getCRSByID( "EPSG:31466" );
        testCRS_31466( testCRS );

        testCRS = dProvider.getCRSByID( "EPSG:31466" );
        testCRS_31466( testCRS );
    }
}

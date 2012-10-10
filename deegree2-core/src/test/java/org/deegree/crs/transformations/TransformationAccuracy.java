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

import junit.framework.TestCase;

import org.deegree.crs.components.Axis;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CRSTransformationException;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public abstract class TransformationAccuracy extends TestCase {

    private static ILogger LOG = LoggerFactory.getLogger( TransformationAccuracy.class );

    protected final static double METER_EPSILON = 0.15;

    protected final static double DEGREE_EPSILON = 0.0000015;

    protected final static Point3d epsilon = new Point3d( METER_EPSILON, METER_EPSILON, 0.4 );

    protected final static Point3d epsilonDegree = new Point3d( DEGREE_EPSILON, DEGREE_EPSILON, 0.4 );

    /**
     * Creates a {@link GeoTransformer} for the given coordinate system.
     * 
     * @param targetCrs
     *            to which incoming coordinates will be transformed.
     * @return the transformer which is able to transform coordinates to the given crs..
     */
    private GeoTransformer getGeotransformer( CoordinateSystem targetCrs ) {
        assertNotNull( targetCrs );
        return new GeoTransformer( targetCrs );
    }

    /**
     * Creates an epsilon string with following layout axis.getName: origPoint - resultPoint = epsilon Unit.getName().
     * 
     * @param sourceCoordinate
     *            on the given axis
     * @param targetCoordinate
     *            on the given axis
     * @param allowedEpsilon
     *            defined by test.
     * @param axis
     *            of the coordinates
     * @return a String representation.
     */
    private String createEpsilonString( boolean failure, double sourceCoordinate, double targetCoordinate,
                                        double allowedEpsilon, Axis axis ) {
        double epsilon = sourceCoordinate - targetCoordinate;
        StringBuilder sb = new StringBuilder( 400 );
        sb.append( axis.getName() ).append( " (result - orig = error [allowedError]): " );
        sb.append( sourceCoordinate ).append( " - " ).append( targetCoordinate );
        sb.append( " = " ).append( epsilon ).append( axis.getUnits() );
        sb.append( " [" ).append( allowedEpsilon ).append( axis.getUnits() ).append( "]" );
        if ( failure ) {
            sb.append( " [FAILURE]" );
        }
        return sb.toString();
    }

    /**
     * Transforms the given coordinates in the sourceCRS to the given targetCRS and checks if they lie within the given
     * epsilon range to the reference point. If successful the transformed will be logged.
     * 
     * @param sourcePoint
     *            to transform
     * @param targetPoint
     *            to which the result shall be checked.
     * @param epsilons
     *            for each axis
     * @param sourceCRS
     *            of the origPoint
     * @param targetCRS
     *            of the targetPoint.
     * @return the string containing the success string.
     * @throws CRSTransformationException
     * @throws AssertionError
     *             if one of the axis of the transformed point do not lie within the given epsilon range.
     */
    private String doAccuracyTest( Point3d sourcePoint, Point3d targetPoint, Point3d epsilons,
                                   CoordinateSystem sourceCRS, CoordinateSystem targetCRS )
                            throws CRSTransformationException {
        assertNotNull( sourceCRS );
        assertNotNull( targetCRS );
        assertNotNull( sourcePoint );
        assertNotNull( targetPoint );
        assertNotNull( epsilons );

        GeoTransformer transformer = getGeotransformer( targetCRS );
        Point point = GeometryFactory.createPoint( sourcePoint.x, sourcePoint.y, sourcePoint.z, sourceCRS );

        Point pp = (Point) transformer.transform( point );
        assertNotNull( pp );
        boolean xFail = Math.abs( pp.getX() - targetPoint.x ) > epsilons.x;
        String xString = createEpsilonString( xFail, pp.getX(), targetPoint.x, epsilons.x,
                                              targetCRS.getCRS().getAxis()[0] );
        boolean yFail = Math.abs( pp.getY() - targetPoint.y ) > epsilons.y;
        String yString = createEpsilonString( yFail, pp.getY(), targetPoint.y, epsilons.y,
                                              targetCRS.getCRS().getAxis()[1] );

        // Z-Axis if available.
        boolean zFail = false;
        String zString = "";
        if ( targetCRS.getDimension() == 3 ) {
            zFail = Math.abs( pp.getZ() - targetPoint.z ) > epsilons.z;
            zString = createEpsilonString( zFail, pp.getZ(), targetPoint.z, epsilons.z, targetCRS.getCRS().getAxis()[2] );
        }
        StringBuilder sb = new StringBuilder();
        if ( xFail || yFail || zFail ) {
            sb.append( "[FAILED] " );
        } else {
            sb.append( "[SUCCESS] " );
        }
        sb.append( "Transformation (" ).append( sourceCRS.getIdentifier() );
        sb.append( " -> " ).append( targetCRS.getIdentifier() ).append( ")\n" );
        sb.append( xString );
        sb.append( "\n" ).append( yString );
        if ( targetCRS.getDimension() == 3 ) {
            sb.append( "\n" ).append( zString );
        }
        if ( xFail || yFail || zFail ) {
            throw new AssertionError( sb.toString() );
        }
        return sb.toString();
    }

    /**
     * Do an forward and inverse accuracy test.
     * 
     * @param sourceCRS
     * @param targetCRS
     * @param source
     * @param target
     * @param forwardEpsilon
     * @param inverseEpsilon
     * @throws CRSTransformationException
     */
    protected void doForwardAndInverse( org.deegree.crs.coordinatesystems.CoordinateSystem sourceCRS,
                                        org.deegree.crs.coordinatesystems.CoordinateSystem targetCRS, Point3d source,
                                        Point3d target, Point3d forwardEpsilon, Point3d inverseEpsilon )
                            throws CRSTransformationException {
        StringBuilder output = new StringBuilder();
        output.append( "Transforming forward crs with id: '" );
        output.append( sourceCRS.getIdentifier() );
        output.append( "' and crs with id: '" );
        output.append( targetCRS.getIdentifier() );
        output.append( "'." );

        // forward transform.
        boolean forwardSuccess = true;
        try {
            output.append( doAccuracyTest( source, target, forwardEpsilon, CRSFactory.create( sourceCRS ),
                                           CRSFactory.create( targetCRS ) ) );
        } catch ( AssertionError ae ) {
            output.append( ae.getLocalizedMessage() );
            forwardSuccess = false;
        }
        if ( !forwardSuccess ) {
            LOG.logError( output.toString() );
        } else {
            LOG.logInfo( output.toString() );
        }

        // inverse transform.
        output = new StringBuilder( "Transforming inverse crs with id: '" );
        output.append( targetCRS.getIdentifier() );
        output.append( "' and crs with id: '" );
        output.append( sourceCRS.getIdentifier() );
        output.append( "'." );
        boolean inverseSuccess = true;
        try {
            output.append( doAccuracyTest( target, source, inverseEpsilon, CRSFactory.create( targetCRS ),
                                           CRSFactory.create( sourceCRS ) ) );
        } catch ( AssertionError ae ) {
            output.append( ae.getLocalizedMessage() );
            inverseSuccess = false;
        }
        if ( !inverseSuccess ) {
            LOG.logError( output.toString() );
        } else {
            LOG.logInfo( output.toString() );
        }

        assertEquals( true, forwardSuccess );
        assertEquals( true, inverseSuccess );

    }

}
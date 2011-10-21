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

package org.deegree.crs.transformations;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3d;

import org.deegree.crs.Identifiable;
import org.deegree.crs.coordinatesystems.CoordinateSystem;
import org.deegree.crs.exceptions.TransformationException;
import org.deegree.crs.transformations.coordinate.ConcatenatedTransform;
import org.deegree.i18n.Messages;

/**
 * The <code>Transformation</code> class supplies the most basic method interface for any given transformation.
 *
 * The change of coordinates from one CRS to another CRS based on different datum is 'currently' only possible via a
 * coordinate <code>Transformation</code>.
 * <p>
 * The derivation of transformation parameters can be done empirically or analytically.
 * <p>
 * The quality (accuracy) of an empirical derivation strongly depends on the chosen reference points, there allocation,
 * and their number. Therefore different realizations for transformations from one datum to another exist. *
 * </p>
 * <p>
 * An analytic derivation is precise but mostly too complex to evaluate.
 * </p>
 *
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 *
 * @author last edited by: $Author$
 *
 * @version $Revision$, $Date$
 *
 */
public abstract class Transformation extends org.deegree.crs.Identifiable implements Serializable {

    private static final long serialVersionUID = -8504028776871895959L;

    private CoordinateSystem sourceCRS;

    private CoordinateSystem targetCRS;

    /**
     * Signaling this transformation as inverse
     */
    private boolean isInverse;

    /**
     * @param sourceCRS
     * @param targetCRS
     * @param id
     *            an identifiable instance containing information about this transformation
     */
    public Transformation( CoordinateSystem sourceCRS, CoordinateSystem targetCRS, Identifiable id ) {
        super( id );
        checkForNullObject( targetCRS, "Transformation", "targetCRS" );
        // checkForNullObject( sourceCRS, "Transformation", "sourceCRS" );
        this.sourceCRS = sourceCRS;
        this.targetCRS = targetCRS;
        isInverse = false;
    }

    /**
     * @return the name of the transformation.
     */
    public abstract String getImplementationName();

    /**
     * Do a transformation, e.g. the incoming data will be transformed into other coordinates.
     *
     * @param srcPts
     *            the points which must be transformed, expected are following values either, long_1, lat_1, height_1,
     *            long_2, lat_2, height_2. or long_1, lat_1, long_2, lat_2
     * @return the transformed points
     * @throws TransformationException
     *             if a transform could not be calculated.
     */
    public abstract List<Point3d> doTransform( final List<Point3d> srcPts )
                            throws TransformationException;

    /**
     * @return true if this transformation doesn't transform the incoming points. (e.g. is the id. matrix)
     */
    public abstract boolean isIdentity();

    /**
     * Little helper function to create a temporary id or name.
     *
     * @param source
     *            containing the value (id or name) of the 'src' coourdinateSystem
     * @param dest
     *            containing the value (id or name) of the 'dest' coourdinateSystem
     * @return a following string "_SRC_fromValue_DEST_toValue".
     */
    public static String createFromTo( String source, String dest ) {
        return new StringBuilder( "_SRC_" ).append( source ).append( "_DEST_" ).append( dest ).toString();
    }

    /**
     * Wraps the incoming coordinates into a List<Point3d> and calls the {@link #doTransform(List)}. The source array
     * will be read according to the dimension of the source CRS {@link #getSourceDimension()} and the target
     * coordinates will be put according to the dimension of the targetCRS {@link #getTargetDimension()}. If the
     * sourceDim &lt; 2 or &gt; 3 a transformation exception will be thrown.
     *
     * @param srcCoords
     *            the array holding the source ('original') coordinates.
     * @param startPositionSrc
     *            the position to start reading the coordinates from the source array (0 is the first).
     * @param destCoords
     *            the array which will receive the transformed coordinates.
     * @param startPositionDest
     *            the index of the destCoords array to put the results, if the result will exceed the array.length, the
     *            array will be enlarged to hold the transformed coordinates.
     * @param lastCoord
     *            the index of the last coordinate (normally length-1)
     * @throws TransformationException
     *             If the sourceDim &lt; 2 or soureDim &gt 3;
     * @throws IllegalArgumentException
     *             if
     *             <ul>
     *             <li>the srcCoords is null</li>
     *             <li>the startPositionSrc &gt; srcCoords.length</li>
     *             <li>the lastCoord &gt; startPositionSrc</li>
     *             <li>the number of source coordinates are not congruent with the source dimension</li>
     *             <li>the lastCoord &lt; startCoordSrc</li>
     *             <li>the source or target dimension &lt; 2 or &gt; 3</li>
     *             </ul>
     */
    public void doTransform( double[] srcCoords, int startPositionSrc, double[] destCoords, int startPositionDest,
                             int lastCoord )
                            throws TransformationException {
        if ( startPositionSrc < 0 ) {
            startPositionSrc = 0;
        }
        if ( srcCoords == null ) {
            throw new IllegalArgumentException( Messages.getMessage( "CRS_PARAMETER_NOT_NULL",
                                                                     "doTransform(double[],int,double[],int,int)",
                                                                     "srcCoords" ) );
        }
        if ( startPositionSrc > srcCoords.length ) {
            throw new IllegalArgumentException( Messages.getMessage( "CRS_TRANSFORM_START_GT_LENGTH" ) );
        }
        if ( lastCoord > srcCoords.length ) {
            throw new IllegalArgumentException( Messages.getMessage( "CRS_TRANSFORM_END_GT_LENGTH" ) );
        }
        if ( ( lastCoord - startPositionSrc ) % getSourceDimension() != 0 ) {
            throw new IllegalArgumentException( Messages.getMessage( "CRS_TRANSFORM_SRC_WRONG_DIM" ) );
        }
        int listSize = ( lastCoord - startPositionSrc ) / getSourceDimension();
        if ( listSize < 0 ) {
            throw new IllegalArgumentException( Messages.getMessage( "CRS_TRANSFORM_LAST_LT_START" ) );
        }

        List<Point3d> sourceCoords = new LinkedList<Point3d>();
        final int dim = getSourceDimension();
        if ( dim > 3 || dim < 2 ) {
            throw new TransformationException( Messages.getMessage( "CRS_TRANSFORM_WRONG_CRS_DIM", "source" ) );
        }
        for ( int i = startPositionSrc; i < lastCoord && ( i + ( dim - 1 ) ) < lastCoord; i += dim ) {
            sourceCoords.add( new Point3d( srcCoords[i], srcCoords[i + 1], ( dim == 3 ) ? srcCoords[i + 2] : 0 ) );
        }
        List<Point3d> result = doTransform( sourceCoords );
        if ( startPositionDest < 0 ) {
            startPositionDest = 0;
        }
        final int requiredSpace = result.size() * getTargetDimension();
        if ( destCoords == null ) {
            startPositionDest = 0;
            destCoords = new double[requiredSpace];
        }
        final int requiredSize = startPositionDest + requiredSpace;
        if ( requiredSize > destCoords.length ) {
            double[] tmp = new double[requiredSize];
            System.arraycopy( destCoords, 0, tmp, 0, startPositionDest );
            destCoords = tmp;
        }
        final int dimDest = getTargetDimension();
        if ( dimDest > 3 || dimDest < 2 ) {
            throw new TransformationException( Messages.getMessage( "CRS_TRANSFORM_WRONG_CRS_DIM", "target" ) );
        }
        int arrayPos = startPositionDest;
        for ( Point3d coord : result ) {
            destCoords[arrayPos++] = coord.x;
            destCoords[arrayPos++] = coord.y;
            if ( dimDest == 3 ) {
                destCoords[arrayPos++] = coord.z;
            }
        }

    }

    /**
     * Transforms a single point3d (by calling the doTransform( List<Point3d>).
     *
     * @param coordinate
     *            to transform, if <code>null</code> null will be returned.
     * @return the transformed coordinate.
     * @throws TransformationException
     *             if the coordinate could not be transformed from the sourceCRS to the targetCRS.
     */
    public Point3d doTransform( Point3d coordinate )
                            throws TransformationException {
        if ( coordinate == null ) {
            return null;
        }
        List<Point3d> coord = new LinkedList<Point3d>();
        coord.add( coordinate );
        return doTransform( coord ).get( 0 );
    }

    /**
     * @return true if the doInverseTransform method should be called, false otherwise.
     */
    public boolean isInverseTransform() {
        return isInverse;
    }

    /**
     * This method flags the transformation about it's state. If this transformation was inverse calling this method
     * will result in a forward transformation and vice versa.
     */
    public void inverse() {
        isInverse = !isInverse;
    }

    /**
     * @return a representation of this transformations name, including the 'Forward' or 'Inverse' modifier.
     */
    public String getTransformationName() {
        StringBuilder result = new StringBuilder( isInverse ? "Inverse " : "Forward " );
        result.append( getImplementationName() );
        return result.toString();
    }

    /**
     * @return the sourceCRS.
     */
    public final CoordinateSystem getSourceCRS() {
        return isInverse ? targetCRS : sourceCRS;
    }

    /**
     * @return the targetCRS.
     */
    public final CoordinateSystem getTargetCRS() {
        return isInverse ? sourceCRS : targetCRS;
    }

    /**
     * @return the dimension of the source coordinateSystem.
     */
    public int getSourceDimension() {
        return getSourceCRS().getDimension();
    }

    /**
     * @return the dimension of the target coordinateSystem.
     */
    public int getTargetDimension() {
        return getTargetCRS().getDimension();
    }

    /**
     * Checks if this transformation is the inverse of the other transformation, which means, this.sourceCRS equals
     * other.targetCRS && this.targetCRS == other.sourceCRS. If Both transformations are identity this method also
     * returns true.
     *
     * @param other
     *            the transformation to check
     * @return true if this and the other transformation are each others inverse.
     */
    public boolean areInverse( Transformation other ) {
        return ( other == null ) ? false
                                : ( this.isIdentity() && other.isIdentity() )
                                  || ( ( this.getSourceCRS().equals( other.getTargetCRS() ) && this.getTargetCRS().equals(
                                                                                                                           other.getSourceCRS() ) ) );
    }

    /**
     * @param sb
     *            to add the transformation chain to, if <code>null</code> a new StringBuilder will be created.
     * @return the given StringBuilder (or a new instance) with the appended transformation steps.
     */
    public final StringBuilder getTransformationPath( StringBuilder sb ) {
        if ( sb == null ) {
            sb = new StringBuilder();
        }
        outputTransform( 0, sb, this );
        return sb;
    }

    private int outputTransform( int level, StringBuilder sb, Transformation t ) {
        if ( t instanceof ConcatenatedTransform ) {
            level = outputTransform( level, sb, ( (ConcatenatedTransform) t ).getFirstTransform() );
            level = outputTransform( level, sb, ( (ConcatenatedTransform) t ).getSecondTransform() );
        } else {
            if ( level != 0 ) {
                sb.append( "->" );
            }
            sb.append( "(" ).append( level ).append( ")" ).append( t.getTransformationName() );
            return ++level;
        }
        return level;
    }

    /**
     * @param newSource
     *            to be used as the new source coordinate system.
     */
    public void setSourceCRS( CoordinateSystem newSource ) {
        this.sourceCRS = newSource;
    }
}

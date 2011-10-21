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
package org.deegree.graphics.displayelements;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.PointSymbolizer;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.model.feature.Feature;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;

/**
 * DisplayElement that encapsulates a point geometry (<tt>GM_Point</tt>) and a <tt>PointSymbolizer</tt>.
 * <p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision$ $Date$
 */
class PointDisplayElement extends GeometryDisplayElement {

    private static ILogger LOG = LoggerFactory.getLogger( PointDisplayElement.class );

    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = -2979559276151855757L;

    private transient static Image defaultImg = new BufferedImage( 7, 7, BufferedImage.TYPE_INT_ARGB );

    static {
        Graphics g = defaultImg.getGraphics();
        g.setColor( Color.LIGHT_GRAY );
        g.fillRect( 0, 0, 9, 9 );
        g.dispose();
    }

    /**
     * Creates a new PointDisplayElement_Impl object.
     * 
     * @param feature
     * @param geometry
     */
    public PointDisplayElement( Feature feature, Point geometry ) {
        super( feature, geometry, null );

        Symbolizer defaultSymbolizer = new PointSymbolizer();
        this.setSymbolizer( defaultSymbolizer );
    }

    /**
     * Creates a new PointDisplayElement_Impl object.
     * 
     * @param feature
     * @param geometry
     * @param symbolizer
     */
    public PointDisplayElement( Feature feature, Point geometry, PointSymbolizer symbolizer ) {
        super( feature, geometry, symbolizer );
    }

    /**
     * Creates a new PointDisplayElement object.
     * 
     * @param feature
     * @param geometry
     */
    public PointDisplayElement( Feature feature, MultiPoint geometry ) {
        super( feature, geometry, null );

        Symbolizer defaultSymbolizer = new PointSymbolizer();
        this.setSymbolizer( defaultSymbolizer );
    }

    /**
     * Creates a new PointDisplayElement object.
     * 
     * @param feature
     * @param geometry
     * @param symbolizer
     */
    public PointDisplayElement( Feature feature, MultiPoint geometry, PointSymbolizer symbolizer ) {
        super( feature, geometry, symbolizer );
    }

    /**
     * renders the DisplayElement to the submitted graphic context
     */
    public void paint( Graphics g, GeoTransform projection, double scale ) {
        synchronized ( symbolizer ) {
            ( (ScaledFeature) feature ).setScale( scale );
            try {
                Image image = defaultImg;

                if ( ( (PointSymbolizer) symbolizer ).getGraphic() != null ) {
                    image = ( (PointSymbolizer) symbolizer ).getGraphic().getAsImage( feature );
                }
                Graphics2D g2D = (Graphics2D) g;

                double[] dis = ( (PointSymbolizer) symbolizer ).getGraphic().getDisplacement( feature );
                if ( geometry instanceof Point ) {
                    drawPoint( g2D, (Point) geometry, projection, image, dis );
                } else {
                    MultiPoint mp = (MultiPoint) geometry;

                    for ( int i = 0; i < mp.getSize(); i++ ) {
                        drawPoint( g2D, mp.getPointAt( i ), projection, image, dis );
                    }
                }
            } catch ( FilterEvaluationException e ) {
                LOG.logError( "Exception caught evaluating an Expression!", e );
            }
        }
    }

    /**
     * renders one point to the submitted graphic context considering the also submitted projection
     * 
     * @param g
     * @param point
     * @param projection
     * @param image
     * @param dis displacement
     */
    private void drawPoint( Graphics2D g, Point point, GeoTransform projection, Image image, double[] dis ) {
        Envelope destSize = projection.getDestRect();
        Position source = point.getPosition();
        int x = (int) Math.round( projection.getDestX( source.getX() ) + 0.5 + dis[0] );
        int y = (int) Math.round( projection.getDestY( source.getY() ) + 0.5 + dis[1] );

        int x_ = x - ( image.getWidth( null ) >> 1 );
        int y_ = y - ( image.getHeight( null ) >> 1 );

        int dx = Math.min( image.getWidth( null ), (int) destSize.getWidth() - x_ );
        int dy = Math.min( image.getHeight( null ), (int) destSize.getHeight() - y_ );
        int tx = Math.min( (int) destSize.getWidth(), x_ + image.getWidth( null ) );
        int ty = Math.min( (int) destSize.getHeight(), y_ + image.getHeight( null ) );

        g.drawImage( image, x_, y_, tx, ty, 0, 0, dx, dy, null );
    }
}

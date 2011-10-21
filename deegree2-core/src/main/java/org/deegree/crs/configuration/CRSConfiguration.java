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

package org.deegree.crs.configuration;

import static org.deegree.crs.projections.ProjectionUtils.EPS11;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.deegree.crs.configuration.deegree.DeegreeCRSProvider;
import org.deegree.crs.configuration.proj4.PROJ4CRSProvider;
import org.deegree.crs.coordinatesystems.CoordinateSystem;
import org.deegree.crs.coordinatesystems.ProjectedCRS;
import org.deegree.crs.exceptions.CRSConfigurationException;
import org.deegree.crs.projections.Projection;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.i18n.Messages;

/**
 * The <code>CRSConfiguration</code> creates, instantiates and supplies a configured CRS-Provider. Because only one
 * crs-configuration is needed inside the JVM, this implementation uses a singleton pattern.
 * <p>
 * The configuration will try to read the file: crs_providers.properties. It uses following strategie to load this file,
 * first the root directory (e.g. '/' or WEB-INF/classes ) will be searched. If no file was found there, it will try to
 * load from the package. The properties file must denote a property with name 'CRS_PROVIDER' followed by a '=' and a
 * fully qualified name denoting the class (an instance of CRSProvider) which should be available in the classpath. This
 * class must have an empty constructor.
 * </p>
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */

public class CRSConfiguration {
    private static ILogger LOG = LoggerFactory.getLogger( CRSConfiguration.class );

    private CRSProvider provider;

    /**
     * public so servlets can clear it, to avoid memory leaks...
     */
    public static final Map<String, CRSConfiguration> DEFINED_CONFIGURATIONS = new HashMap<String, CRSConfiguration>();

    private static final String DEFAULT_PROVIDER_CLASS = "org.deegree.crs.configuration.DeegreeCRSProvider";

    private static String CONFIGURED_DEFAULT_PROVIDER_CLASS = DEFAULT_PROVIDER_CLASS;

    //
    private final static String PROVIDER_CONFIG = "crs_providers.properties";

    private static Properties configuredProperties = null;

    static {
        configuredProperties = new Properties();
        LOG.logDebug( "Trying to load configured CRS provider from configuration (/crs_providers.properties)." );
        InputStream is = CRSConfiguration.class.getResourceAsStream( "/" + PROVIDER_CONFIG );
        if ( is == null ) {
            LOG.logDebug( "Trying to load configured CRS provider from configuration (org.deegree.crs.configuration.crs_providers.properties)." );
            is = CRSConfiguration.class.getResourceAsStream( PROVIDER_CONFIG );
        }
        if ( is == null ) {
            LOG.logWarning( Messages.getMessage( "CRS_CONFIG_NO_PROVIDER_DEFS_FOUND", PROVIDER_CONFIG ) );
        } else {
            try {
                configuredProperties.load( is );
                configuredProperties.put( "crs.default.configuration", "deegree-crs-configuration.xml" );
                String tmp = configuredProperties.getProperty( "CRS_PROVIDER" );
                if ( tmp != null && !"".equals( tmp.trim() ) ) {
                    CONFIGURED_DEFAULT_PROVIDER_CLASS = tmp;
                    String crs_configuration = System.getProperty( "crs.configuration" );
                    if ( crs_configuration != null && !"".equals( crs_configuration ) ) {
                        LOG.logInfo( "Using the supplied crs.configuration property for the crs_configuration location." );
                        configuredProperties.put( "crs.configuration", crs_configuration );
                    }
                }

            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            } finally {
                try {
                    is.close();
                } catch ( IOException e ) {
                    // no output if the stream can't be closed, just leave it as it is.
                }
            }
        }

    }

    // private static CRSConfiguration CONFIG = null;

    /**
     * @param provider
     *            to get the CRS's from.
     */
    private CRSConfiguration( CRSProvider provider ) {
        this.provider = provider;
    }

    /**
     * Creates or returns an instance of the CRSConfiguration by trying to instantiate the given provider class. If the
     * name is null or "" the Provider configured in the 'crs_providers.properties' will be returned. If the
     * instantiation of this class fails a {@link org.deegree.crs.configuration.deegree.DeegreeCRSProvider} will be
     * returned.
     * 
     * @param providerName
     *            the canonical name of the class, e.g. org.deegree.crs.MyProvider
     * @return an instance of a CRS-Configuration with the configured CRSProvider.
     * @throws CRSConfigurationException
     *             if --anything-- went wrong while instantiating the CRSProvider.
     */
    public synchronized static CRSConfiguration getCRSConfiguration( String providerName ) {
        String provName = null;
        if ( providerName == null || "".equals( providerName.trim() ) ) {
            provName = CONFIGURED_DEFAULT_PROVIDER_CLASS;
        } else {
            provName = providerName.trim();
        }
        LOG.logDebug( "Trying to find a provider for class: " + provName );
        if ( DEFINED_CONFIGURATIONS.containsKey( provName ) && DEFINED_CONFIGURATIONS.get( provName ) != null ) {
            LOG.logDebug( "Found a cached provider for class: " + provName );
            return DEFINED_CONFIGURATIONS.get( provName );
        }
        CRSProvider provider = null;

        if ( CONFIGURED_DEFAULT_PROVIDER_CLASS.equals( provName )
             && CONFIGURED_DEFAULT_PROVIDER_CLASS.equals( DEFAULT_PROVIDER_CLASS ) ) {
            provider = new DeegreeCRSProvider( new Properties( configuredProperties ) );
        } else {
            try {
                // use reflection to instantiate the configured provider.
                Class<?> t = Class.forName( provName );
                t.asSubclass( CRSProvider.class );
                LOG.logDebug( "Trying to load configured CRS provider from classname: " + provName );
                Constructor<?> constructor = t.getConstructor( Properties.class );
                if ( constructor != null ) {
                    provider = (CRSProvider) constructor.newInstance( configuredProperties );
                }
            } catch ( InstantiationException e ) {
                LOG.logError( Messages.getMessage( "CRS_CONFIG_INSTANTIATION_ERROR", provName, e.getMessage() ), e );
            } catch ( IllegalAccessException e ) {
                LOG.logError( Messages.getMessage( "CRS_CONFIG_INSTANTIATION_ERROR", provName, e.getMessage() ), e );
            } catch ( ClassNotFoundException e ) {
                LOG.logError( Messages.getMessage( "CRS_CONFIG_INSTANTIATION_ERROR", provName, e.getMessage() ), e );
            } catch ( SecurityException e ) {
                LOG.logError( Messages.getMessage( "CRS_CONFIG_INSTANTIATION_ERROR", provName, e.getMessage() ), e );
            } catch ( NoSuchMethodException e ) {
                LOG.logError( Messages.getMessage( "CRS_CONFIG_INSTANTIATION_ERROR", provName, e.getMessage() ), e );
            } catch ( IllegalArgumentException e ) {
                LOG.logError( Messages.getMessage( "CRS_CONFIG_INSTANTIATION_ERROR", provName, e.getMessage() ), e );
            } catch ( InvocationTargetException e ) {
                LOG.logError( Messages.getMessage( "CRS_CONFIG_INSTANTIATION_ERROR", provName, e.getMessage() ), e );
            } catch ( Throwable t ) {
                LOG.logError( Messages.getMessage( "CRS_CONFIG_INSTANTIATION_ERROR", provName, t.getMessage() ), t );
            } finally {
                if ( provider == null ) {
                    LOG.logInfo( "The configured class: " + provName
                                 + " was not created. Trying to create a deegree-crs-provider" );
                    provider = new DeegreeCRSProvider( new Properties( configuredProperties ) );
                }
            }
        }
        CRSConfiguration config = new CRSConfiguration( provider );
        DEFINED_CONFIGURATIONS.put( provName, config );
        LOG.logDebug( "Instantiated a new CRSConfiguration :" + config );
        return config;
    }

    /**
     * Creates or returns an instance of the CRSConfiguration by reading the DEFAULT property configured in the
     * 'crs_providers.properties'. If no key is given (or no string could be loaded), the {@link DeegreeCRSProvider}
     * will be used.
     * 
     * @return an instance of a CRS-Configuration with the configured CRSProvider.
     * @throws CRSConfigurationException
     *             if --anything-- went wrong while instantiating the CRSProvider.
     */
    public synchronized static CRSConfiguration getCRSConfiguration()
                            throws CRSConfigurationException {
        return getCRSConfiguration( null );
    }

    /**
     * Overwrites the crs.configuration property with the given value.
     * 
     * @param fileName
     *            to set the crs.configuration property to.
     * 
     * @return the old crs.configuration propert (if any)
     * @throws CRSConfigurationException
     *             if --anything-- went wrong while instantiating the CRSProvider.
     */
    public synchronized static String setDefaultFileProperty( String fileName ) {
        return (String) configuredProperties.setProperty( "crs.configuration", fileName );
    }

    /**
     * export the given file to the deegree-crs format.
     * 
     * @param args
     * @throws Exception
     */
    public static void main( String[] args )
                            throws Exception {
        if ( args.length == 0 ) {
            outputHelp();
        }
        Map<String, String> params = new HashMap<String, String>( 5 );
        for ( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            if ( arg != null && !"".equals( arg.trim() ) ) {
                arg = arg.trim();
                if ( arg.equalsIgnoreCase( "-?" ) || arg.equalsIgnoreCase( "-h" ) ) {
                    outputHelp();
                } else {
                    if ( i + 1 < args.length ) {
                        String val = args[++i];
                        if ( val != null && !"".equals( val.trim() ) ) {
                            params.put( arg, val.trim() );
                        } else {
                            System.out.println( "Invalid value for parameter: " + arg );
                        }
                    } else {
                        System.out.println( "No value for parameter: " + arg );
                    }
                }
            }
        }
        String inFormat = params.get( "-inFormat" );
        if ( inFormat == null || "".equals( inFormat.trim() ) ) {
            System.out.println( "No input format (inFormat) defined, setting to proj4" );
            inFormat = "proj4";
        }
        String inFile = params.get( "-inFile" );
        if ( inFile == null || "".equals( inFile.trim() ) ) {
            System.out.println( "No input file set, exiting\n" );
            outputHelp();
            throw new Exception( "No input file set, exiting" );
        }
        // File inputFile = new File( inFile );

        String outFile = params.get( "-outFile" );
        String outFormat = params.get( "-outFormat" );
        if ( outFormat == null || "".equals( outFormat.trim() ) ) {
            System.out.println( "No output format (outFormat) defined, setting to deegree" );
            outFormat = "deegree";
        }

        String veri = params.get( "-verify" );
        boolean verify = ( veri != null && !"".equals( inFile.trim() ) );

        Properties inProps = new Properties();
        inProps.put( "crs.configuration", inFile );
        CRSProvider in = new PROJ4CRSProvider( inProps );
        if ( "deegree".equalsIgnoreCase( inFormat ) ) {
            try {
                in = new DeegreeCRSProvider( new Properties( configuredProperties ) );
            } catch ( CRSConfigurationException e ) {
                e.printStackTrace();
            }
        }

        CRSProvider out = new DeegreeCRSProvider( new Properties( configuredProperties ) );
        if ( "proj4".equalsIgnoreCase( outFormat ) ) {
            out = new PROJ4CRSProvider();
        }

        try {
            List<CoordinateSystem> allSystems = in.getAvailableCRSs();
            if ( verify ) {
                out = new DeegreeCRSProvider( null );
                List<CoordinateSystem> notExported = new LinkedList<CoordinateSystem>();
                for ( CoordinateSystem inCRS : allSystems ) {
                    if ( inCRS.getType() == CoordinateSystem.PROJECTED_CRS ) {
                        String id = inCRS.getIdentifier();
                        CoordinateSystem outCRS = out.getCRSByID( id );
                        // System.out.print( "Getting crs: " + id + " and projection: " +
                        // ((ProjectedCRS)inCRS).getProjection().getDeegreeSpecificName() );
                        if ( outCRS != null && outCRS.getType() == CoordinateSystem.PROJECTED_CRS ) {
                            // System.out.println( "... [SUCCESS] to retrieve from deegree-config
                            // with projection: " +
                            // ((ProjectedCRS)outCRS).getProjection().getDeegreeSpecificName() );
                            Projection inProj = ( (ProjectedCRS) inCRS ).getProjection();
                            Projection outProj = ( (ProjectedCRS) outCRS ).getProjection();
                            if ( Math.abs( inProj.getProjectionLatitude() - outProj.getProjectionLatitude() ) > EPS11 ) {
                                System.out.println( "For the projection with id: " + id
                                                    + " the projectionLatitude differs:\n in ("
                                                    + ( (ProjectedCRS) inCRS ).getProjection().getImplementationName()
                                                    + "): " + Math.toDegrees( inProj.getProjectionLatitude() )
                                                    + "\nout("
                                                    + ( (ProjectedCRS) outCRS ).getProjection().getImplementationName()
                                                    + " with id: "
                                                    + ( (ProjectedCRS) outCRS ).getProjection().getImplementationName()
                                                    + "): " + Math.toDegrees( outProj.getProjectionLatitude() ) );
                            }
                        } else {
                            notExported.add( inCRS );
                            System.out.println( id + " [FAILED] to retrieve from deegree-config." );
                        }
                    }
                }
                if ( notExported.size() > 0 ) {
                    StringBuilder sb = new StringBuilder( notExported.size() * 2000 );
                    out.export( sb, allSystems );
                    if ( outFile != null && !"".equals( outFile.trim() ) ) {
                        File outputFile = new File( outFile );
                        BufferedWriter writer = new BufferedWriter( new FileWriter( outputFile ) );
                        writer.write( sb.toString() );
                        writer.flush();
                        writer.close();
                    } else {
                        System.out.println( sb.toString() );
                    }
                }
            } else {

                StringBuilder sb = new StringBuilder( allSystems.size() * 2000 );
                out.export( sb, allSystems );
                if ( outFile != null && !"".equals( outFile.trim() ) ) {
                    File outputFile = new File( outFile );
                    BufferedWriter writer = new BufferedWriter( new FileWriter( outputFile ) );
                    writer.write( sb.toString() );
                    writer.flush();
                    writer.close();
                } else {
                    System.out.println( sb.toString() );
                }
            }
        } catch ( CRSConfigurationException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        // CRSConfiguration config = new CRSConfiguration(
    }

    private static void outputHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append( "The CRSConfiguration program can be used to create a deegree-crs-configuration, from other crs definition-formats. Following parameters are supported:\n" );
        sb.append( "-inFile the /path/to/crs-definitions-file\n" );
        sb.append( "-inFormat the format of the input file, valid values are proj4(default),deegree \n" );
        sb.append( "-outFormat the format of the output file, valid values are deegree (default)\n" );
        sb.append( "-outFile the /path/to/the/output/file or standard output if not supplied.\n" );
        sb.append( "[-verify] checks the projection parameters of the inFormat against the deegree configuration.\n" );
        sb.append( "-?|-h output this text\n" );
        sb.append( "example usage: java -cp deegree.jar org.deegree.crs.configuration.CRSConfiguration -inFormat 'proj4' -inFile '/home/proj4/nad/epsg' -outFormat 'deegree' -outFile '/home/deegree/crs-definitions.xml'\n" );
        System.out.println( sb.toString() );
        System.exit( 1 );
    }

    /**
     * @return the crs provider.
     */
    public final CRSProvider getProvider() {
        return provider;
    }

    @Override
    public String toString() {
        return "CRSConfiguration is using "
               + ( ( provider == null ) ? "no crs provider, this is strange."
                                       : "crs provider: " + provider.getClass().getCanonicalName() );
    }
}


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Decoders {

    private Decoders() {
    }

    public static final Decoder<Position> POSITION =
            ( reporter, json ) -> {
        double[] pair = new JsonTool( reporter ).asNumericArray( json, 2 );
        return pair == null ? null : new Position( pair[ 0 ], pair[ 1 ] );
    };

    public static final Decoder<Position[]> POSITIONS =
        createArrayDecoder( POSITION, Position.class );

    public static final Decoder<Position[]> LINE_STRING =
            ( reporter, json ) -> {
        Position[] positions = POSITIONS.decode( reporter, json );
        if ( positions == null ) {
            return null;
        }
        else {
            int np = positions.length;
            if ( np < 2 ) {
                reporter.report( "Too few positions (" + np + "<2)" );
                return null;
            }
            else {
                return positions;
            }
        }
    };

    public static final Decoder<Position[][]> LINE_STRINGS =
        createArrayDecoder( LINE_STRING, Position[].class );

    public static final Decoder<LinearRing> LINEAR_RING =
            ( reporter, json ) -> {
        Position[] allPositions = POSITIONS.decode( reporter, json );
        if ( allPositions == null ) {
            return null;
        }
        else {
            int np1 = allPositions.length;
            if ( np1 < 4 ) {
                reporter.report( "too few positions for linear ring"
                               + " (" + np1 + "<4)" );
                return null;
            }
            else if ( !allPositions[ 0 ].equals( allPositions[ np1 - 1 ] ) ) {
                reporter.report( "first and last positions not identical"
                               + " for linear ring" );
                return null;
            }
            else {
                Position[] distinctPositions = new Position[ np1 - 1 ];
                System.arraycopy( allPositions, 0,
                                  distinctPositions, 0, np1 - 1 );
                return new LinearRing( distinctPositions );
            }
        }
    };

    public static final Decoder<LinearRing[]> LINEAR_RINGS =
        createArrayDecoder( LINEAR_RING, LinearRing.class );

    public static final Decoder<LinearRing[][]> LINEAR_RINGS_ARRAY =
        createArrayDecoder( LINEAR_RINGS, LinearRing[].class );

    public static final Decoder<Bbox> BBOX =
            ( reporter, json ) -> {
        double[] bounds = new JsonTool( reporter ).asNumericArray( json, 4 );
        if ( bounds == null ) {
            return null;
        }
        else if ( bounds[ 0 ] <= bounds[ 2 ] &&
                  bounds[ 1 ] <= bounds[ 3 ] ) {
            return new Bbox( bounds[ 0 ], bounds[ 1 ],
                             bounds[ 2 ], bounds[ 3 ] );
        }
        else {
            reporter.report( "bbox bounds out of sequence: "
                           + Arrays.toString( bounds ) );
            return null;
        }
    };

    public static final Decoder<Datatype<?>> DATATYPE =
            ( reporter, json ) -> {
        String txt = new JsonTool( reporter ).asString( json, false );
        if ( txt == null ) {
            reporter.report( "no declared datatype, treat as string" );
            return Datatype.STRING;
        }
        Datatype datatype = Datatype.forName( txt );
        if ( datatype == null ) {
            reporter.report( "Unknown datatype \"" + txt + "\", "
                           + "treat as string" );
            return Datatype.STRING;
        }
        else {
            return datatype;
        }
    };

    public static final Decoder<Field[]> FIELDS =
            ( reporter, json ) -> {
        List<Field> fieldList = new ArrayList<>();
        JSONObject jobj = new JsonTool( reporter ).asJSONObject( json );
        if ( jobj != null ) {
            for ( String key : jobj.keySet() ) {
                Reporter fieldReporter = reporter.createReporter( key );
                JSONObject fieldObj = new JsonTool( fieldReporter ) 
                                     .asJSONObject( jobj.get( key ) );
                if ( fieldObj != null ) {
                    String info =
                        new JsonTool( fieldReporter.createReporter( "info" ) )
                       .asString( fieldObj.opt( "info" ), false );
                    String ucd =
                        new JsonTool( fieldReporter.createReporter( "ucd" ) )
                       .asString( fieldObj.opt( "ucd" ), false );
                    String unit =
                        new JsonTool( fieldReporter.createReporter( "unit" ) )
                       .asString( fieldObj.opt( "unit" ), false );
                    Datatype<?> datatype =
                        DATATYPE.decode( fieldReporter
                                        .createReporter( "datatype" ),
                                         fieldObj.opt( "datatype" ) );
                    fieldList.add( new Field() {
                        public String getName() {
                            return key;
                        }
                        public String getInfo() {
                            return info;
                        }
                        public String getUcd() {
                            return ucd;
                        }
                        public String getUnit() {
                            return unit;
                        }
                        public Datatype<?> getDatatype() {
                            return datatype; 
                        }
                    } );
                }
            }
        }
        return fieldList.toArray( new Field[ 0 ] );
    };

    private static final Map<String,Decoder<?>> shapeDecoders_ =
        createShapeDecoders();

    public static final Decoder<Geometry<?>> GEOMETRY =
            ( reporter, json ) -> {
        JsonTool jtool = new JsonTool( reporter );
        JSONObject jobj = jtool.asJSONObject( json );
        if ( jobj == null ) {
            return null;
        }
        String type = new JsonTool( reporter.createReporter( "type" ) )
                     .asString( jobj.opt( "type" ), true );
        Object bboxJson = jobj.opt( "bbox" );
        Bbox bbox = bboxJson == null
                  ? null
                  : Decoders.BBOX
                   .decode( reporter.createReporter( "bbox" ), bboxJson );
        if ( type == null ) {
            return null;
        }
        else if ( type.equals( "GeometryCollection" ) ) {
            Reporter geomReporter = reporter.createReporter( "geometries" );
            JSONArray jarray = new JsonTool( geomReporter )
                              .asJSONArray( jobj.opt( "geometries" ) );
            if ( jarray == null ) {
                return null;
            }
            else {
                List<Geometry<?>> geomList = new ArrayList<>();
                for ( int ig = 0; ig < jarray.length(); ig++ ) {
                    Geometry<?> geom =
                        GEOMETRY.decode( geomReporter.createReporter( ig ),
                                         jarray.get( ig ) );
                    geomList.add( geom );
                }
                return new Geometry<List<Geometry<?>>>( jobj, type, bbox,
                                                        geomList );
            }
        }
        Decoder<?> shapeDecoder = shapeDecoders_.get( type );
        if ( shapeDecoder == null ) {
            reporter.report( "Unknown geometry type \"" + type + "\"" );
            return null;
        }
        else {
            jtool.requireAbsent( jobj, "geometry" );   // RFC7946 sec 7.1
            jtool.requireAbsent( jobj, "properties" );
            jtool.requireAbsent( jobj, "features" );
            return createGeometry( reporter, jobj, type, bbox, shapeDecoder );
        }
    };

    public static final Decoder<TimeCoords> TIME_COORDS =
            ( reporter, json ) -> {
        JSONObject jobj = new JsonTool( reporter ).asJSONObject( json );
        if ( jobj == null ) {
            return null;
        }
        String id = new JsonTool( reporter.createReporter( "id" ) )
                   .asString( jobj.opt( "id" ), false );
        String name = new JsonTool( reporter.createReporter( "name" ) )
                     .asString( jobj.opt( "name" ), true );
        String unit = new JsonTool( reporter.createReporter( "unit" ) )
                     .asString( jobj.opt( "unit" ), true );
        String timeOrigin = new JsonTool( reporter
                                         .createReporter( "time_origin" ) )
                                        
                           .asString( jobj.opt( "time_origin" ), true );
        String timeScale = new JsonTool( reporter
                                        .createReporter( "time_scale" ) )
                          .asString( jobj.opt( "time_scale" ), true );
        return new TimeCoords() {
            public String getId() {
                return id;
            }
            public String getName() {
                return name;
            }
            public String getUnit() {
                return unit;
            }
            public String getTimeOrigin() {
                return timeOrigin;
            }
            public String getTimeScale() {
                return timeScale;
            }
        };
    };

    public static final Decoder<SpectralCoords> SPECTRAL_COORDS =
            ( reporter, json ) -> {
        JSONObject jobj = new JsonTool( reporter ).asJSONObject( json );
        if ( jobj == null ) {
            return null;
        }
        String name = new JsonTool( reporter.createReporter( "name" ) )
                     .asString( jobj.opt( "name" ), true );
        String unit = new JsonTool( reporter.createReporter( "unit" ) )
                     .asString( jobj.opt( "unit" ), true );
        return new SpectralCoords() {
            public String getName() {
                return name;
            }
            public String getUnit() {
                return unit;
            }
        };
    };

    public static final Decoder<RefPosition> REF_POSITION =
            ( reporter, json ) -> {
        JSONObject jobj = new JsonTool( reporter ).asJSONObject( json );
        if ( jobj == null ) {
            return null;
        }
        String id = new JsonTool( reporter.createReporter( "id" ) )
                   .asString( jobj.opt( "id" ), true );
        return new RefPosition() {
            public String getId() {
                return id;
            }
        };
    };

    public static final Decoder<Crs> CRS =
            ( reporter, json ) -> {
        JSONObject jobj = new JsonTool( reporter ).asJSONObject( json );
        if ( jobj == null ) {
            return null;
        }
        Reporter typeReporter = reporter.createReporter( "type" );
        String crsType = new JsonTool( typeReporter )
                        .asString( jobj.opt( "type" ), true );
        if ( crsType != null &&
             ! Crs.CRS_TYPES.contains( crsType ) ) {
            typeReporter.report( "disallowed value \"" + crsType + "\""
                               + " (not in " + Crs.CRS_TYPES + ")" );
        }
        Reporter propsReporter = reporter.createReporter( "properties" );
        JSONObject crsProps = new JsonTool( propsReporter )
                             .asJSONObject( jobj.opt( "properties" ) );
        final TimeCoords timeCoords;
        final SpectralCoords spectralCoords;
        final RefPosition refPosition;
        if ( crsProps != null ) {
            timeCoords =
                TIME_COORDS
               .decode( propsReporter.createReporter( "time_coords" ),
                        crsProps.opt( "time_coords" ) );
            spectralCoords =
                SPECTRAL_COORDS
               .decode( propsReporter.createReporter( "spectral_coords" ),
                        crsProps.opt( "spectral_coords" ) );
            refPosition =
                REF_POSITION
               .decode( propsReporter.createReporter( "ref_position" ),
                        crsProps.opt( "ref_position" ) );
        }
        else {
            timeCoords = null;
            spectralCoords = null;
            refPosition = null;
        }
        return new Crs( crsType, timeCoords, spectralCoords, refPosition );
    };

    public static final Decoder<Feature> FEATURE =
            ( reporter, json ) -> {
        JsonTool jtool = new JsonTool( reporter );
        JSONObject jobj = jtool.asJSONObject( json );
        if ( jobj == null ) {
            return null;
        }
        String type = new JsonTool( reporter.createReporter( "type" ) )
                     .asString( jobj.opt( "type" ), true );
        if ( type == null ) {
            return null;
        }
        else if ( type.equals( "Feature" ) ) {
            Geometry<?> geometry =
                GEOMETRY.decode( reporter.createReporter( "geometry" ),
                                 jobj.opt( "geometry" ) );
            if ( geometry == null ) {
                return null;
            }
            Object bboxJson = jobj.opt( "bbox" );
            Bbox bbox = bboxJson == null
                  ? null
                  : Decoders.BBOX
                   .decode( reporter.createReporter( "bbox" ), bboxJson );
            String id = new JsonTool( reporter.createReporter( "id" ) )
                       .asStringOrNumber( jobj.opt( "id" ), false );
            Object propsJson = jobj.opt( "properties" );
            JSONObject properties =
                  propsJson == null
                ? null
                : new JsonTool( reporter.createReporter( "properties" ) )
                 .asJSONObject( propsJson );
            jtool.requireAbsent( jobj, "features" );   // RFC7946 sec 7.1
            return new Feature( jobj, bbox, geometry, id, properties );
        }
        else {
            reporter.report( "type is \"" + type + "\" not \"Feature\"" );
            return null;
        }
    };

    public static final Decoder<FeatureCollection> FEATURE_COLLECTION =
            ( reporter, json ) -> {
        JsonTool jtool = new JsonTool( reporter );
        JSONObject jobj = jtool.asJSONObject( json );
        if ( jobj == null ) {
            return null;
        }
        String type = new JsonTool( reporter.createReporter( "type" ) )
                     .asString( jobj.opt( "type" ), true );
        if ( type == null ) {
            return null;
        }
        else if ( type.equals( "FeatureCollection" ) ) {
            jtool.requireAbsent( jobj, "geometry" );   // RFC7946 sec 7.1
            jtool.requireAbsent( jobj, "properties" );
            Field[] fields = FIELDS.decode( reporter.createReporter( "fields" ),
                                            jobj.opt( "fields" ) );
            Map<String,Field> fieldMap = new HashMap<>();
            for ( Field field : fields ) {
                fieldMap.put( field.getName(), field );
            }
            Feature[] features = createArrayDecoder( FEATURE, Feature.class )
                                .decode( reporter.createReporter( "features" ),
                                         jobj.opt( "features" ) );
            if ( features == null ) {
                return null;
            }
            for ( int ifeat = 0; ifeat < features.length; ifeat++ ) {
                Feature feat = features[ ifeat ];
                JSONObject props = feat.getProperties();
                Reporter propsReporter =
                    reporter.createReporter( ifeat )
                            .createReporter( "properties" );
                if ( props != null ) {
                    checkProperties( propsReporter, props, fieldMap );
                }
            }
            Object bboxJson = jobj.opt( "bbox" );
            Bbox bbox = bboxJson == null
                      ? null
                      : Decoders.BBOX
                       .decode( reporter.createReporter( "bbox" ), bboxJson );
            Crs crs = CRS.decode( reporter.createReporter( "crs" ),
                                  jobj.opt( "crs" ) );
            return new FeatureCollection( jobj, bbox, crs, fieldMap, features );
        }
        else {
            reporter.report( "type is \"" + type
                           + "\" not \"FeatureCollection\"" );
            return null;
        }
    };

    private static void checkProperties( Reporter reporter,
                                         JSONObject properties,
                                         Map<String,Field> fields ) {
        for ( String key : properties.keySet() ) {
            Field field = fields.get( key );
            Reporter propReporter = reporter.createReporter( key );
            if ( field == null ) {
                propReporter.report( "no corresponding field for property" );
            }
            else {
                Object value = properties.get( key );
                if ( !JsonTool.isNull( value ) &&
                     ( value instanceof Number || value instanceof String ) ) {
                    Datatype<?> datatype = field.getDatatype();
                    if ( ! datatype.isType( value.toString() ) ) {
                        propReporter.report( "bad " + datatype + " syntax"
                                           + " \"" + value + "\"" );
                    }
                }
            }
        }
    }

    private static final Map<String,Decoder<? extends TfcatObject>>
                         tfcatDecoders_ =
        createTfcatDecoders();

    public static final Decoder<TfcatObject> TFCAT =
            ( reporter, json ) -> {
        JsonTool jtool = new JsonTool( reporter );
        JSONObject jobj = jtool.asJSONObject( json );
        if ( jobj == null ) {
            return null;
        }
        String type = new JsonTool( reporter.createReporter( "type" ) )
                     .asString( jobj.opt( "type" ), true );
        if ( type == null ) {
            return null;
        }
        Decoder<? extends TfcatObject> decoder = tfcatDecoders_.get( type );
        if ( decoder == null ) {
            reporter.createReporter( "type" )
                    .report( "unknown TFCat/GeoJSON type \"" + type + "\"" );
            return null;
        }
        else {
            return decoder.decode( reporter, jobj );
        }
    };

    private static <T> Geometry<T>
            createGeometry( Reporter reporter, JSONObject json, String type,
                            Bbox bbox, Decoder<T> coordDecoder ) {
        Object coords = json.opt( "coordinates" );
        if ( coords == null ) {
            reporter.report( "no coordinates" );
            return null;
        }
        T shape = coordDecoder.decode( reporter.createReporter( "coordinates" ),
                                       coords );
        return shape == null ? null
                             : new Geometry<T>( json, type, bbox, shape );
    }

    private static Map<String,Decoder<?>> createShapeDecoders() {
        Map<String,Decoder<?>> map = new LinkedHashMap<>();
        map.put( "Point", POSITION );
        map.put( "MultiPoint", POSITIONS );
        map.put( "LineString", LINE_STRING );
        map.put( "MultiLineString", LINE_STRINGS );
        map.put( "Polygon", LINEAR_RINGS );
        map.put( "MultiPolygon", LINEAR_RINGS_ARRAY );
        return map;
    }

    private static Map<String,Decoder<? extends TfcatObject>>
            createTfcatDecoders() {
        Map<String,Decoder<? extends TfcatObject>> map = new LinkedHashMap<>();
        map.put( "Feature", FEATURE );
        map.put( "FeatureCollection", FEATURE_COLLECTION );
        for ( String geomType : createShapeDecoders().keySet() ) {
            map.put( geomType, GEOMETRY );
        }
        return map;
    }

    private static <T> Decoder<T[]>
            createArrayDecoder( Decoder<T> scalarDecoder,
                                Class<T> scalarClazz ) {
        return ( reporter, json ) -> {
            JSONArray jarray = new JsonTool( reporter ).asJSONArray( json );
            if ( jarray == null ) {
                return null;
            }
            int n = jarray.length();
            @SuppressWarnings("unchecked")
            T[] array = (T[]) Array.newInstance( scalarClazz, n );
            for ( int i = 0; i < n; i++ ) {
                T item = scalarDecoder.decode( reporter.createReporter( i ),
                                               jarray.get( i ) );
                if ( item == null ) {
                    return null;
                }
                array[ i ] = item;
            }
            return array;
        };
    }
}

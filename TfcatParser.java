
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class TfcatParser {

    private static Map<String,Decoder<?>> shapeFactories_ =
            createShapeFactories();

    public Geometry<?> parseGeometry( Reporter reporter, Object obj ) {
        JSONObject json = new JsonTool( reporter ).asJSONObject( obj );
        String type = new JsonTool( reporter.createReporter( "type" ) )
                     .asString( json.opt( "type" ), true );
        Object bboxJson = json.opt( "bbox" );
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
                              .asJSONArray( json.opt( "geometries" ) );
            if ( jarray == null ) {
                return null;
            }
            else {
                List<Geometry<?>> geomList = new ArrayList<>();
                for ( int ig = 0; ig < jarray.length(); ig++ ) {
                    Geometry<?> geom =
                        parseGeometry( geomReporter, jarray.get( ig ) );
                    if ( geom == null ) {
                        return null;
                    }
                    geomList.add( geom );
                }
                return new Geometry<List<Geometry<?>>>( json, type, bbox,
                                                        geomList );
            }
        }
        Decoder<?> shapeFact = shapeFactories_.get( type );
        if ( shapeFact == null ) {
            reporter.report( "Unknown geometry type \"" + type + "\"" );
            return null;
        }
        else {
            return createGeometry( reporter, json, type, bbox, shapeFact );
        }
    }

    private <T> Geometry<T> createGeometry( Reporter reporter, JSONObject json,
                                            String type, Bbox bbox,
                                            Decoder<T> coordDecoder ) {
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

    private static Map<String,Decoder<?>> createShapeFactories() {
        Map<String,Decoder<?>> map = new LinkedHashMap<>();
        map.put( "Point", Decoders.POSITION );
        map.put( "MultiPoint", Decoders.POSITIONS );
        map.put( "LineString", Decoders.LINE_STRING );
        map.put( "MultiLineString", Decoders.LINE_STRINGS );
        map.put( "Polygon", Decoders.LINEAR_RINGS );
        map.put( "MultiPolygon", Decoders.LINEAR_RINGS_ARRAY );
        return map;
    }

    public static void main( String[] args ) throws IOException {
        String inFile = args[ 0 ];
        try ( InputStream in = "-".equals( inFile )
                             ? System.in
                             : new FileInputStream( inFile ) ) {
            JSONObject json = new JSONObject( new JSONTokener( in ) );
            BasicReporter reporter = new BasicReporter();
            new TfcatParser().parseGeometry( reporter, json );
            List<String> msgs = reporter.getMessages();
            if ( msgs.size() == 0 ) {
                System.out.println( "OK" );
                return;
            }
            else {
                System.out.println( "FAIL (" + msgs.size() + ")" );
                for ( String msg : msgs ) {
                    System.out.println( "    " + msg );
                }
                System.exit( 1 );
            }
        }
    }
}

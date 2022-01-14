
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class TfcatParser {

    private final Consumer<Report> reporter_;
    private final JsonTool jsonTool_;

    private static final Map<String,TfcatFactory<?>> factoryMap_ =
        createFactoryMap();

    public TfcatParser( Consumer<Report> reporter ) {
        reporter_ = reporter == null ? r -> {} : reporter;
        jsonTool_ = new JsonTool( reporter );
    }

    public TfcatObject parseTfcat( JSONObject json ) {
        String type = jsonTool_.asString( json.opt( "type" ), "type", true );
        TfcatFactory<?> factory = factoryMap_.get( type );
        if ( factory != null ) {
            return factory.createTfcat( reporter_, json,
                                        getBboxMember( json ) );
        }
        else if ( isNull( type ) ) {
            return null;
        }
        else {
            jsonTool_.report( Level.ERROR, "UKTP",
                              "Unknown TFCat Type \"" + type + "\"" );
            return null;
        }
    }

    private Bbox getBboxMember( JSONObject json ) {
        double[] bboxArray =
            jsonTool_.asNumericArray( json.opt( "bbox" ), "bbox", false, 4 );
        if ( bboxArray == null ) {
            return null;
        }
        else {
            if ( bboxArray[ 0 ] <= bboxArray[ 2 ] &&
                 bboxArray[ 1 ] <= bboxArray[ 3 ] ) {
                return new Bbox( bboxArray[ 0 ], bboxArray[ 1 ],
                                 bboxArray[ 2 ], bboxArray[ 2 ] );
            }
            else {
                jsonTool_.report( Level.ERROR, "BBMM",
                                  "bbox array values out of sequence: "
                                + Arrays.toString( bboxArray ) );
                return null;
            }
        }
    }

    private static boolean isNull( Object obj ) {
        return obj == null || JSONObject.NULL.equals( obj );
    }

    private static Map<String,TfcatFactory<?>> createFactoryMap() {
        Map<String,TfcatFactory<?>> map = new LinkedHashMap<>();
        map.put( "Point", Point::createTfcat );
        map.put( "MultiPoint", MultiPoint::createTfcat );
        map.put( "LineString", LineString::createTfcat );
        map.put( "MultiLineString", MultiLineString::createTfcat );
        map.put( "Polygon", Polygon::createTfcat );
        return map;
    }

    public static void main( String[] args ) throws IOException {
        String inFile = args[ 0 ];
        try ( InputStream in = "-".equals( inFile )
                             ? System.in
                             : new FileInputStream( inFile ) ) {
            JSONObject json = new JSONObject( new JSONTokener( in ) );
            BasicReporter reporter = new BasicReporter();
            new TfcatParser( reporter ).parseTfcat( json );
            List<Report> reports = reporter.getReports();
            if ( reports.size() == 0 ) {
                System.out.println( "OK" );
                return;
            }
            else {
                System.out.println( "FAIL (" + reports.size() + ")" );
                for ( Report report : reports ) {
                    System.out.println( "    " + report );
                }
                System.exit( 1 );
            }
        }
    }
}

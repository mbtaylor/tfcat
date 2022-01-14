
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

    private static final Map<String,ShapeFactory<?>> factoryMap_ =
        createFactoryMap();

    public TfcatParser( Consumer<Report> reporter ) {
        reporter_ = reporter == null ? r -> {} : reporter;
        jsonTool_ = new JsonTool( reporter );
    }

    public Geometry<?> parseGeometry( JSONObject json ) {
        String type = jsonTool_.asString( json.opt( "type" ), "type", true );
        Bbox bbox = getBboxMember( json );
        ShapeFactory<?> shapeFact = getShapeFactory( type );
        if ( shapeFact != null ) {
            return createGeometry( json, type, bbox, shapeFact );
        }
        else {
            jsonTool_.report( Level.ERROR, "UKTP",
                              "Unknown TFCat Type \"" + type + "\"" );
            return null;
        }
    }

    private <S> Geometry<S> createGeometry( JSONObject json, String type,
                                            Bbox bbox,
                                            ShapeFactory<S> shapeFact ) {
        Object coords = json.opt( "coordinates" );
        if ( coords == null ) {
            jsonTool_.report( Level.ERROR, Report.toCode( "NC", "type" ),
                              type + ": no coordinates" );
            return null;
        }
        S shape = shapeFact.createShape( reporter, coords );
        return shape == null ? null
                             : new Geometry<S>( json, type, bbox, shape );
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

    private static Map<String,ShapeFactory<?>> createFactoryMap() {
        Map<String,TfcatFactory<?>> map = new LinkedHashMap<>();
        map.put( "Point", ShapeFactory.POINT );
        map.put( "MultiPoint", ShapeFactory.MULTI_POINT );
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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.json.JSONArray;
import org.json.JSONObject;

public class TfcatParser {

    private final Consumer<Report> reporter_;

    private static final Map<String,TfcatFactory<?>> factoryMap_ =
        createFactoryMap();

    public TfcatParser( Consumer<Report> reporter ) {
        reporter_ = reporter == null ? r -> {} : reporter;
    }

    public TfcatObject parseTfcat( JSONObject json ) {
        String type = getRequiredString( json, "type" );
        TfcatFactory<?> factory = factoryMap_.get( type );
        if ( factory != null ) {
            return factory.createTfcat( reporter_, json, getBbox( json ) );
        }
        else if ( isNull( type ) ) {
            return null;
        }
        else {
            report( Level.ERROR, "UKTP",
                    "Unknown TFCat Type \"" + type + "\"" );
            return null;
        }
    }

    private Bbox getBbox( JSONObject json ) {
        double[] bboxArray = getNumericArray( reporter_, json, "bbox" );
        if ( bboxArray == null ) {
            return null;
        }
        else {
            if ( bboxArray.length != 4 ) {
                report( Level.ERROR, "BBN4",
                        "bbox array wrong length"
                      + " (" + bboxArray.length + " != 4)" );
                return null;
            }
            else if ( bboxArray[ 0 ] <= bboxArray[ 2 ] &&
                      bboxArray[ 1 ] <= bboxArray[ 3 ] ) {
                return new Bbox( bboxArray[ 0 ], bboxArray[ 1 ],
                                 bboxArray[ 2 ], bboxArray[ 2 ] );
            }
            else {
                report( Level.ERROR, "BBMM",
                        "bbox array values out of sequence: "
                      + Arrays.toString( bboxArray ) );
                return null;
            }
        }
    }

    private String getRequiredString( JSONObject json, String key ) {
        if ( json.has( key ) ) {
            return getString( json, key );
        }
        else {
            report( Level.ERROR, Report.toCode( "SV", key ),
                    "missing member \"" + key + "\"" );
            return null;
        }
    }

    private String getString( JSONObject json, String key ) {
        if ( json.has( key ) ) {
            Object value = json.get( key );
            if ( value instanceof String ) {
                return (String) value;
            }
            else {
                report( Level.ERROR, Report.toCode( "SV", key ),
                        "non-string value for \"" + key + "\"" );
                return null;
            }
        }
        else {
            return null;
        }
    }

    static double[] getNumericArray( Consumer<Report> reporter,
                                     JSONObject json, String key ) {
        if ( json.has( key ) ) {
            Object value = json.get( key );
            if ( value instanceof JSONArray ) {
                JSONArray jarray = (JSONArray) value;
                int n = jarray.length();
                double[] darray = new double[ n ];
                for ( int i = 0; i < n; i++ ) {
                    Object item = jarray.get( i );
                    final double dval;
                    if ( isNull( item ) ) {
                        dval = Double.NaN;
                    }
                    else if ( item instanceof Number ) {
                        dval = ((Number) item).doubleValue();
                    }
                    else {
                        reporter
                       .accept( new Report( Level.ERROR,
                                            Report.toCode( "NN", key ),
                                            "Non-numeric-array value"
                                          + " for \"" + key + "\"" ) );
                        return null;
                    }
                    darray[ i ] = dval;
                }
                return darray;
            }
            else {
                reporter.accept( new Report( Level.ERROR,
                                             Report.toCode( "NA", key ),
                                             "Non-array value"
                                           + " for \"" + key + "\"" ) );
                return null;
            }
        }
        else {
            return null;
        }
    }

    private void report( Level level, String code, String msg ) {
        reporter_.accept( new Report( level, code, msg ) );
    }

    private static boolean isNull( Object obj ) {
        return obj == null || JSONObject.NULL.equals( obj );
    }

    private static Map<String,TfcatFactory<?>> createFactoryMap() {
        Map<String,TfcatFactory<?>> map = new LinkedHashMap<>();
        map.put( "Point", Point::createTfcat );
        return map;
    }
}

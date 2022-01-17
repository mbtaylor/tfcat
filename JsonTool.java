
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonTool {

    private final Reporter reporter_;

    public JsonTool( Reporter reporter ) {
        reporter_ = reporter;
    }

    public String asString( Object json, boolean isRequired ) {
        if ( json instanceof String ) {
            return (String) json;
        }
        else if ( isNull( json ) ) {
            if ( isRequired ) {
                reporter_.report( "missing value" );
            }
            return null;
        }
        else {
            reporter_.report( "non-string value" );
            return null;
        }
    }

    public String asStringOrNumber( Object json, boolean isRequired ) {
        if ( json instanceof String || json instanceof Number ) {
            return json.toString();
        }
        else if ( isNull( json ) ) {
            if ( isRequired ) {
                reporter_.report( "missing value" );
            }
            return null;
        }
        else {
            reporter_.report( "non-string/numeric value" );
            return null;
        }
    }

    public double[] asNumericArray( Object json, int nreq ) {
        boolean isNanPermitted = false;
        if ( json instanceof JSONArray ) {
            JSONArray jarray = (JSONArray) json;
            int nd = jarray.length();
            if ( nreq > 0 && nreq != nd ) {
                reporter_.report( "wrong array length"
                                + " (" + nd + " != " + nreq + ")" );
                return null;
            }
            double[] darray = new double[ nd ];
            for ( int id = 0; id < nd; id++ ) {
                Object item = jarray.get( id );
                final double dval;
                if ( isNull( item ) && isNanPermitted ) {
                    dval = Double.NaN;
                }
                else if ( item instanceof Number ) {
                    dval = ((Number) item).doubleValue();
                }
                else {
                    reporter_.createReporter( "[" + id + "]" )
                             .report( "non-numeric value (" + item + ")" );
                    return null;
                }
            }
            return darray;
        }
        else if ( isNull( json ) ) {
            reporter_.report( "missing array" );
            return null;
        }
        else {
            reporter_.report( "non-array value" );
            return null;
        }
    }

    public JSONObject asJSONObject( Object json ) {
        if ( json instanceof JSONObject ) {
            return (JSONObject) json;
        }
        else if ( isNull( json ) ) {
            reporter_.report( "missing object" );
            return null;
        }
        else {
            reporter_.report( "non-object value" );
            return null;
        }
    }

    public JSONArray asJSONArray( Object json ) {
        if ( json instanceof JSONArray ) {
            return (JSONArray) json;
        }
        else if ( isNull( json ) ) {
            reporter_.report( "missing array" );
            return null;
        }
        else {
            reporter_.report( "non-array value" );
            return null;
        }
    }

    public boolean isNull( Object json ) {
        return json == null || JSONObject.NULL.equals( json );
    }
}

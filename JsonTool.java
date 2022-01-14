
import java.util.function.Consumer;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonTool {
    private final Consumer<Report> reporter_;

    public JsonTool( Consumer<Report> reporter ) {
        reporter_ = reporter;
    }

    public void report( Level level, String code, String msg ) {
        reporter_.accept( new Report( level, code, msg ) );
    }

    public String asString( Object json, String context, boolean isRequired ) {
        if ( json instanceof String ) {
            return (String) json;
        }
        else if ( isNull( json ) ) {
            if ( isRequired ) {
                report( Level.ERROR, Report.toCode( "SV", context ),
                        context + ": missing value" );
            }
            return null;
        }
        else {
            report( Level.ERROR, Report.toCode( "NS", context ),
                    context + ": non-string value" );
            return null;
        }
    }

  // check for NaNs?
    public double[] asNumericArray( Object json, String context,
                                    boolean isRequired, int nreq ) {
        if ( json instanceof JSONArray ) {
            JSONArray jarray = (JSONArray) json;
            int nd = jarray.length();
            if ( nreq > 0 && nreq != nd ) {
                report( Level.ERROR, Report.toCode( "IN", context ),
                        context + ": wrong array length"
                      + " (" + nd + " != " + nreq + ")" );
                return null;
            }
            double[] darray = new double[ nd ];
            for ( int id = 0; id < nd; id++ ) {
                Object item = jarray.get( id );
                final double dval;
                if ( isNull( item ) ) {
                    dval = Double.NaN;
                }
                else if ( item instanceof Number ) {
                    dval = ((Number) item).doubleValue();
                }
                else {
                    report( Level.ERROR, Report.toCode( "NN", context ),
                            context + "[" + id + "]: "
                          + "non-numeric value (" + item + ")" );
                    return null;
                }
            }
            return darray;
        }
        else if ( isNull( json ) ) {
            if ( isRequired ) {
                report( Level.ERROR, Report.toCode( "UA", context ),
                        context + ": missing value" );
            }
            return null;
        }
        else {
            report( Level.ERROR, Report.toCode( "TA", context ),
                    context + ": non-array value" );
            return null;
        }
    }

    public Position asPosition( Object json, String context,
                                boolean isRequired ) {
        double[] pair = asNumericArray( json, context, isRequired, 2 );
        return pair == null
             ? null
             : new Position( pair[ 0 ], pair[ 1 ] );
    }

    public Position[] asPositionArray( Object json, String context,
                                       boolean isRequired ) {
        if ( json instanceof JSONArray ) {
            JSONArray jarray = (JSONArray) json;
            int np = jarray.length();
            Position[] positions = new Position[ np ];
            for ( int ip = 0; ip < np; ip++ ) {
                Position pos = asPosition( jarray.get( ip ),
                                           context + "[" + ip + "]", true );
                if ( pos == null ) {
                    return null;
                }
                positions[ ip ] = pos;
            }
            return positions;
        }
        else if ( isNull( json ) ) {
            if ( isRequired ) {
                report( Level.ERROR, Report.toCode( "UP", context ),
                        context + ": missing position array" );
            }
            return null;
        }
        else {
            report( Level.ERROR, Report.toCode( "TP", context ),
                    context + ": non-array value" );
            return null;
        }
    }

    public Position[][] asPositionArrayArray( Object json, String context,
                                              boolean isRequired ) {
        if ( json instanceof JSONArray ) {
            JSONArray jarray = (JSONArray) json;
            int na = jarray.length();
            Position[][] arrays = new Position[ na ][];
            for ( int ia = 0; ia < na; ia++ ) {
                Position[] parray =
                    asPositionArray( jarray.get( ia ), context + "[" + ia + "]",
                                     true );
                if ( parray == null ) {
                    return null;
                }
                arrays[ ia ] = parray;
            }
            return arrays;
        }
        else if ( isNull( json ) ) {
            if ( isRequired ) {
                report( Level.ERROR, Report.toCode( "UA", context ),
                        context + ": missing position[] array" );
            }
            return null;
        }
        else {
            report( Level.ERROR, Report.toCode( "AA", context ),
                    context + ": non-array value" );
            return null;
        }
    }

    public Position[][][] asPositionArrayArrayArray( Object json,
                                                     String context,
                                                     boolean isRequired ) {
        if ( json instanceof JSONArray ) {
            JSONArray jarray = (JSONArray) json;
            int na = jarray.length();
            Position[][][] arrays = new Position[ na ][][];
            for ( int ia - 0; ia < na; ia++ ) {
                Position[][] parray =
                    asPositionArrayArray( jarray.get( ia ),
                                          context + "[" + ia + "]", true );
                if ( parray == null ) {
                    return null;
                }
                arrays[ ia ] = parray;
            }
            return arrays;
        }
        else if ( isNull( json ) ) {
            if ( isRequired ) {
                report( Level.ERROR, Report.toCode( "UB", context ),
                        context + ": missing position[][] array" );
            }
            return null;
        }
        else {
            report( Level.ERROR, Report.toCode( "BB", context ),
                    context + ": non-array value" );
            return null;
        }
    }

    private static boolean isNull( Object json ) {
        return json == null || JSONObject.NULL.equals( json );
    }
}

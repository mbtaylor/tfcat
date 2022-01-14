
import java.lang.reflect.Array;
import java.util.Arrays;
import org.json.JSONArray;

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
                T item = scalarDecoder
                        .decode( reporter.createReporter( "[" + i + "]" ),
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

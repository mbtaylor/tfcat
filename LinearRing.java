
import java.util.function.Consumer;

public class LinearRing {

    private final Position[] distinctPositions_;

    LinearRing( Position[] distinctPositions ) {
        distinctPositions_ = distinctPositions;
    }

    public Position[] getDistinctPositions() {
        return distinctPositions_;
    }

    public static LinearRing createInstance( Consumer<Report> reporter,
                                             Position[] allPositions,
                                             String context ) {
        int np = allPositions.length;
        if ( np < 4 ) {
            reporter.accept( new Report( Level.ERROR,
                                         Report.toCode( "LF", context ),
                                         context + ": too few positions for "
                                       + "linear ring (" + np + "<4)" ) );
            return null;
        }
        else if ( ! allPositions[ 0 ].equals( allPositions[ np - 1 ] ) ) {
            reporter.accept( new Report( Level.ERROR,
                                         Report.toCode( "LE", context ),
                                         context + ": first and last positions "
                                       + "not identical for linear ring" ) );
            return null;
        }
        else {
            Position[] distinctPositions = new Position[ np - 1 ];
            System.arraycopy( allPositions, 0, distinctPositions, 0, np - 1 );
            return new LinearRing( distinctPositions );
        }
    }
}

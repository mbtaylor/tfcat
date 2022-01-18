
public class LinearRing {

    private final Position[] distinctPositions_;

    LinearRing( Position[] distinctPositions ) {
        distinctPositions_ = distinctPositions;
    }

    public Position[] getDistinctPositions() {
        return distinctPositions_;
    }

    public boolean isClockwise() {
        int np = distinctPositions_.length;
        double sum = 0;
        for ( int ip = 0; ip < np; ip++ ) {
            Position p0 = distinctPositions_[ ip ];
            Position p1 = distinctPositions_[ ( ip + 1 ) % np ];
            sum += ( p1.getTime() - p0.getTime() )
                 * ( p1.getSpectral() + p0.getSpectral() );
        }
        return sum > 0;
    }
}

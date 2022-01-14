
public class LinearRing {

    private final Position[] distinctPositions_;

    LinearRing( Position[] distinctPositions ) {
        distinctPositions_ = distinctPositions;
    }

    public Position[] getDistinctPositions() {
        return distinctPositions_;
    }
}

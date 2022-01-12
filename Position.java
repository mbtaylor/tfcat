
import java.util.Arrays;
import java.util.function.Consumer;

public class Position {

    private final double time_;
    private final double spectral_;

    public Position( double time, double spectral ) {
        time_ = time;
        spectral_ = spectral;
    }

    public double getTime() {
        return time_;
    }

    public double getSpectral() {
        return spectral_;
    }

    public static Position create( Consumer<Report> reporter,
                                   double[] darray ) {
        if ( darray == null ) {
            return null;
        }
        else if ( darray.length != 2 ||
                  Double.isNaN( darray[ 0 ] ) || Double.isNaN( darray[ 1 ] ) ) {
            reporter.accept( new Report( Level.ERROR, "POSA",
                                         "Bad position "
                                       + Arrays.toString( darray ) ) );
            return null;
        }
        else {
            return new Position( darray[ 0 ], darray[ 1 ] );
        }
    }
}

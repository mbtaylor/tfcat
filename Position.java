
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

    @Override
    public int hashCode() {
        int code = 9901;
        code = 23 * code + Float.floatToIntBits( (float) time_ );
        code = 23 * code + Float.floatToIntBits( (float) spectral_ );
        return code;
    }

    @Override
    public boolean equals( Object o ) {
        if ( o instanceof Position ) {
            Position other = (Position) o;
            return this.time_ == other.time_
                && this.spectral_ ==  other.spectral_;
        }
        else {
            return false;
        }
    }
}


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
}

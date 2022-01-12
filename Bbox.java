
public class Bbox {

    private final double tmin_;
    private final double smin_;
    private final double tmax_;
    private final double smax_;

    public Bbox( double tmin, double smin, double tmax, double smax ) {
        tmin_ = tmin;
        smin_ = smin;
        tmax_ = tmax;
        smax_ = smax;
    }

    public double getTimeMin() {
        return tmin_;
    }

    public double getSpectralMin() {
        return smin_;
    }

    public double getTimeMax() {
        return tmax_;
    }

    public double getSpectralMax() {
        return smax_;
    }
}


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class Crs {

    private final String type_;
    private final TimeCoords timeCoords_;
    private final SpectralCoords spectralCoords_;
    private final RefPosition refPosition_;

    public static Collection<String> CRS_TYPES =
            Collections.unmodifiableSet( new HashSet<String>( Arrays.asList( 
        "Time-Frequency", "Time-Wavelength", "Time-Energy", "Time-Wavenumber"
    ) ) );

    public Crs( String type, TimeCoords timeCoords,
                SpectralCoords spectralCoords, RefPosition refPosition ) {
        type_ = type;
        timeCoords_ = timeCoords;
        spectralCoords_ = spectralCoords;
        refPosition_ = refPosition;
    }

    public String getType() {
        return type_;
    }

    public TimeCoords getTimeCoords() {
        return timeCoords_;
    }

    public SpectralCoords getSpectralCoords() {
        return spectralCoords_;
    }

    public RefPosition getRefPosition() {
        return refPosition_;
    }
}


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

public interface TimeCoords {

    public static final Collection<String> TIME_SCALES =
            Collections
           .unmodifiableSet( new LinkedHashSet<String>( Arrays.asList(
        "GPS", "TAI", "TCB", "TCG", "TDB", "TT", "UT", "UTC", "UNKNOWN",
        "SCET", "SCLK"
    ) ) );

    public static final Pattern TIME_ORIGIN_REGEX =
        Pattern.compile( "([0-9]+)-([0-9]{1,2})-([0-9]{1,2})" +
                         "(?:[T ]([0-9]{1,2})" +
                            "(?::([0-9]{1,2})" +
                               "(?::([0-9]{1,2}(?:\\.[0-9]*)?))?" +
                            ")?" +
                         "Z?)?" );

    String getId();
    String getName();
    String getUnit();
    String getTimeOrigin();
    String getTimeScale();
}

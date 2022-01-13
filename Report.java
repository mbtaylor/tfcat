
public class Report {

    private final Level level_;
    private final String code_;
    private final String message_;

    public Report( Level level, String code, String message ) {
        level_ = level;
        code_ = code;
        message_ = message;
    }

    public Level getLevel() {
        return level_;
    }

    public String getCode() {
        return code_;
    }

    public String getMessage() {
        return message_;
    }

    @Override
    public String toString() {
        return level_.getChar() + "-" + code_ + ": " + message_;
    }

    public static String toCode( String prefix, String suffix ) {
        return pad( prefix + suffix, 4, 'X' ).toUpperCase();
    }

    private static String pad( String txt, int leng, char chr ) {
        StringBuffer sbuf = new StringBuffer( leng );
        sbuf.append( txt.substring( 0, Math.min( txt.length(), leng ) ) );
        while ( sbuf.length() < leng ) {
            sbuf.append( chr );
        }
        return sbuf.toString();
    }
}

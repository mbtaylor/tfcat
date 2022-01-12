
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
}

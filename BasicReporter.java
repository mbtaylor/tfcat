
import java.util.ArrayList;
import java.util.List;

public class BasicReporter implements Reporter {

    private final String context_;
    private final List<String> messages_; 

    public BasicReporter() {
        this( null, new ArrayList<String>() );
    }

    private BasicReporter( String context, List<String> messages ) {
        context_ = context;
        messages_ = messages;
    }

    public BasicReporter createReporter( String subContext ) {
        return new BasicReporter( context_ == null 
                                ? subContext
                                : context_ + "/" + subContext,
                                  messages_ );
    }

    public void report( String message ) {
        StringBuffer sbuf = new StringBuffer();
        if ( context_ != null ) {
            sbuf.append( context_ )
                .append( ": " );
        }
        sbuf.append( message );
        messages_.add( sbuf.toString() );
    }

    public List<String> getMessages() {
        return messages_;
    }
}

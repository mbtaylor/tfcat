
import java.util.ArrayList;
import java.util.List;

public class BasicReporter implements Reporter {

    private final boolean isDebug_;
    private final String context_;
    private final List<String> messages_; 

    public BasicReporter( boolean isDebug ) {
        this( isDebug, null, new ArrayList<String>() );
    }

    private BasicReporter( boolean isDebug, String context,
                           List<String> messages ) {
        isDebug_ = isDebug;
        context_ = context;
        messages_ = messages;
 
    }

    public BasicReporter createReporter( String subContext ) {
        String context = context_ == null ? subContext
                                          : context_ + "/" + subContext;
        return new BasicReporter( isDebug_, context, messages_ );
    }

    public BasicReporter createReporter( int subContext ) {
        String context = ( context_ == null ? "" : context_ )
                       + "[" + subContext + "]";
        return new BasicReporter( isDebug_, context, messages_ );
    }

    public void report( String message ) {
        StringBuffer sbuf = new StringBuffer();
        if ( context_ != null ) {
            sbuf.append( context_ )
                .append( ": " );
        }
        sbuf.append( message );
        String txt = sbuf.toString();
        messages_.add( txt );
        if ( isDebug_ ) {
            System.err.println( txt );
            Thread.dumpStack();
            System.err.println();
        }
    }

    public List<String> getMessages() {
        return messages_;
    }
}

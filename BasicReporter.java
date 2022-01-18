
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class BasicReporter implements Reporter {

    private final boolean isDebug_;
    private final String context_;
    private final List<String> messages_; 
    private final UnaryOperator<String> ucdChecker_;
    private final UnaryOperator<String> unitChecker_;

    public BasicReporter( boolean isDebug ) {
        this( isDebug, null, null );
    }

    public BasicReporter( boolean isDebug,
                          UnaryOperator<String> ucdChecker,
                          UnaryOperator<String> unitChecker ) {
        this( isDebug, ucdChecker, unitChecker,
              (String) null, new ArrayList<String>() );
    }

    private BasicReporter( BasicReporter template, String context ) {
        this( template.isDebug_, template.ucdChecker_, template.unitChecker_,
              context, template.messages_ );
    }

    private BasicReporter( boolean isDebug,
                           UnaryOperator<String> ucdChecker,
                           UnaryOperator<String> unitChecker,
                           String context, List<String> messages ) {
        isDebug_ = isDebug;
        ucdChecker_ = ucdChecker;
        unitChecker_ = unitChecker;
        context_ = context;
        messages_ = messages;
    }

    public BasicReporter createReporter( String subContext ) {
        String context = context_ == null ? subContext
                                          : context_ + "/" + subContext;
        return new BasicReporter( this, context );
    }

    public BasicReporter createReporter( int subContext ) {
        String context = ( context_ == null ? "" : context_ )
                       + "[" + subContext + "]";
        return new BasicReporter( this, context );
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

    public void checkUcd( String ucd ) {
        if ( ucdChecker_ != null && ucd != null ) {
            String report = ucdChecker_.apply( ucd );
            if ( report != null ) {
                report( report );
            }
        }
    }

    public void checkUnit( String unit ) {
        if ( unitChecker_ != null && unit != null ) {
            String report = unitChecker_.apply( unit );
            if ( report != null ) {
                report( report );
            }
        }
    }

    public List<String> getMessages() {
        return messages_;
    }
}

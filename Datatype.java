
public abstract class Datatype<T> {

    private final String name_;
    private final Class<T> clazz_;

    public static final Datatype<Long> INT;
    public static final Datatype<Double> FLOAT;
    public static final Datatype<Boolean> BOOL;
    public static final Datatype<String> STRING;

    private static final Datatype<?>[] ALL_TYPES = {
        INT = new Datatype<Long>( "int", Long.class ) {
            public boolean isType( String txt ) {
                return txt.matches( "[+-]?[0-9]+" );
            }
            public Long decode( String txt ) {
                return Long.valueOf( txt );
            }
        },
        FLOAT = new Datatype<Double>( "float", Double.class ) {
            public boolean isType( String txt ) {
                return txt.matches( "-?[0-9]+([eE][+-]?[0-9]+)?" );
            }
            public Double decode( String txt ) {
                return Double.parseDouble( txt );
            }
        },
        BOOL = new Datatype<Boolean>( "bool", Boolean.class ) {
            public boolean isType( String txt ) {
                return "true".equals( txt )
                    || "false".equals( txt );
            }
            public Boolean decode( String txt ) {
                if ( "true".equals( txt ) ) {
                    return Boolean.TRUE;
                }
                else if ( "false".equals( txt ) ) {
                    return Boolean.FALSE;
                }
                else {
                    return null;
                }
            }
        },
        STRING = new Datatype<String>( "str", String.class ) {
            public boolean isType( String txt ) {
                return true;
            }
            public String decode( String txt ) { 
                return txt;
            }
        },
    };

    private Datatype( String name, Class<T> clazz ) {
        name_ = name;
        clazz_ = clazz;
    }

    public String getName() {
        return name_;
    }

    public Class<T> getTypeClass() {
        return clazz_;
    }

    /**
     * @throws  RuntimeException  if the syntax is wrong
     */
    public abstract T decode( String txt );

    public abstract boolean isType( String txt );

    @Override
    public String toString() {
        return name_;
    }

    public static Datatype<?> forName( String name ) {
        for ( Datatype<?> t : ALL_TYPES ) {
            if ( t.name_.equals( name ) ) {
                return t;
            }
        }
        return null;
    }
}

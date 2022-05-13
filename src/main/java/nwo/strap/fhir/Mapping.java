package nwo.strap.fhir;

/**
 * This class provides convenient method to run mapping
 */
public class Mapping {

    private Mapping() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Run mapping
     *
     * @param input  JSON string to be mapped
     * @param config Google Whistle mapping configuration.
     *               See available configurations in the
     *               {@code config.properties} file.
     * @return Mapped JSON string
     */
    public static String run(String input, String config) {

        GWMapping.GoString.ByValue in = new GWMapping.GoString.ByValue();
        in.p = input;
        in.n = in.p.length();

        GWMapping.GoString.ByValue conf = new GWMapping.GoString.ByValue();
        conf.p = System.getProperty(config);
        conf.n = conf.p.length();

        GWMapping lib = GWMapping.INSTANCE;

        return lib.RunMapping(in, conf);
    }

}

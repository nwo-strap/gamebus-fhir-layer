package nwo.strap.fhir;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

/**
 * This interface connects to Google Whistle mapping function implemented in C
 */
public interface GWMapping extends Library {
    GWMapping INSTANCE = Native.load("google_whistle", GWMapping.class);

    @FieldOrder({ "p", "n" })
    public class GoString extends Structure {
        public static class ByValue extends GoString implements Structure.ByValue {
        }

        public String p;
        public long n;
    }

    public String RunMapping(GoString.ByValue input, GoString.ByValue config);
}

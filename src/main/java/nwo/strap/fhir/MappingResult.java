package nwo.strap.fhir;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

@FieldOrder({"result", "error"})
public class MappingResult extends Structure {
    public static class ByValue extends MappingResult implements Structure.ByValue {
    }

    public String result;
    public String error;
}
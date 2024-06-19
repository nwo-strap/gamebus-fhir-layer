package nwo.strap.fhir;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Location;

import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.NotImplementedOperationException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

public class LocationResourceProvider implements IResourceProvider {

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Location.class;
    }

    @Read()
    public Location read(@IdParam IdType theId, RequestDetails theRequestDetails) {
        String authHeader = theRequestDetails.getHeader("Authorization");
        // Request GameBus Activity
        String res = GamebusApiHandler.get("activities", theId.getIdPart(), authHeader);

        // Get the activity name
        JSONObject resJSON = new JSONObject(res);
        JSONObject gameDescriptor = resJSON.getJSONObject("gameDescriptor");
        String translationKey = gameDescriptor.getString("translationKey");

        try {
            // Run mapping using Google Whistle
            String resourcesStr = Mapping.run(res, "gwc.activity");

            if (!resourcesStr.startsWith("[")) {
                String errorMessage = String.format("GameBus gameDescriptor %s (%s) is NOT supported yet.", translationKey, theId.getIdPart());
                throw new NotImplementedOperationException(errorMessage);
            }

            // Run filtering
            JSONArray resources = new JSONArray(resourcesStr);
            IParser parser = theRequestDetails.getFhirContext().newJsonParser();
            Location output = null;
            for (Object resource : resources) {
                try {
                    output = parser.parseResource(Location.class, resource.toString());
                } catch (DataFormatException e) {
                    // ignore the exception, filter out undesired resources
                }
                if (output != null) {
                    break;
                }
            }
            return output;
        } catch (MappingException e) {
            String errorMessage = String.format("Failed to map GameBus gameDescriptor %s (%s): %s", translationKey, theId.getIdPart(), e);
            throw new NotImplementedOperationException(errorMessage);
        }
    }
}
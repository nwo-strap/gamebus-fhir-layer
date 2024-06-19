package nwo.strap.fhir;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;

import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.NotImplementedOperationException;

public class PatientResourceProvider implements IResourceProvider {

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Patient.class;
    }

    @Read()
    public Patient read(@IdParam IdType theId, RequestDetails theRequestDetails) {
        String authHeader = theRequestDetails.getHeader("Authorization");
        // Request GameBus player
        String res = GamebusApiHandler.get("players", theId.getIdPart(), authHeader);

        try {
            // Run the mapping using Google Whistle
            String output = Mapping.run(res, "gwc.player");

            // Create patient
            IParser parser = theRequestDetails.getFhirContext().newJsonParser();

            return parser.parseResource(Patient.class, output);
        } catch (MappingException e) {
            String errorMessage = String.format("Failed to map GameBus player %s: %s", theId.getIdPart(), e);
            throw new NotImplementedOperationException(errorMessage);
        }
    }

}
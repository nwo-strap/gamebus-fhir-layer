package nwo.strap.fhir;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;

import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Offset;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.TokenParamModifier;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.NotImplementedOperationException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

public class ObservationResourceProvider implements IResourceProvider {

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Observation.class;
    }

    @Read()
    public Observation read(@IdParam IdType theId, RequestDetails theRequestDetails) {

        String authHeader = theRequestDetails.getHeader("Authorization");

        // Request GameBus Activity
        String res = GamebusApiHandler.get("activities", theId.getIdPart(), authHeader);

        // Get the activity name
        JSONObject resJSON = new JSONObject(res);
        JSONObject gameDescriptor = resJSON.getJSONObject("gameDescriptor");
        String translationKey = gameDescriptor.getString("translationKey");

        // Run the mapping using Google Whistle
        try {
            String resourcesStr = Mapping.run(res, "gwc.activity");
            if (!resourcesStr.startsWith("[")) {
                String errorMessage = String.format("GameBus gameDescriptor %s (%s) is NOT supported yet.", translationKey, theId.getIdPart());
                throw new NotImplementedOperationException(errorMessage);
            }

            // Run filtering
            JSONArray resources = new JSONArray(resourcesStr);
            IParser parser = theRequestDetails.getFhirContext().newJsonParser();

            Observation output = null;
            for (Object resource : resources) {
                try {
                    output = parser.parseResource(Observation.class, resource.toString());
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

    @Search
    public List<Observation> search(
            @RequiredParam(name = Observation.SP_PATIENT) ReferenceParam thePatient,
            @OptionalParam(name = Observation.SP_DATE) DateRangeParam theDateRange,
            @OptionalParam(name = Observation.SP_CODE) TokenOrListParam theCodes,
            @Sort SortSpec theSort,
            @Offset Integer theOffset,
            @Count Integer theCount,
            RequestDetails theRequestDetails) {

        // TODO: to refactor this method to small pieces
        // Get Bearer token
        String authHeader = theRequestDetails.getHeader("Authorization");

        // Handle search parameters
        // Handle patient
        String patientId = thePatient.getIdPart();

        // Handle date range
        String gbStart = null;
        String gbEnd = null;
        if (theDateRange != null) {
            DateFormat gamebusDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date fromDate = theDateRange.getLowerBoundAsInstant();
            Date toDate = theDateRange.getUpperBoundAsInstant();
            gbStart = fromDate != null ? gamebusDateFormat.format(fromDate) : null;
            gbEnd = toDate != null ? gamebusDateFormat.format(toDate) : null;
        }

        // Handle code
        // Only support GameBus gameDescriptor.translationKey as code
        // TODO: add support for system|code, need new conectMaps and translator
        String gbGds = null;
        String gbExcludeGds = null;
        if (theCodes != null) {
            List<TokenParam> codesList = theCodes.getValuesAsQueryTokens();

            String value = codesList.stream()
                    .map(TokenParam::getValue)
                    .collect(Collectors.joining(","));

            if (codesList.get(0).getModifier() == TokenParamModifier.NOT) {
                gbExcludeGds = value;
            } else {
                gbGds = value;
            }
        }

        // Handle _sort:
        // Only support sort by: date, code, _id
        Map<String, String> allowedSort = new HashMap<>();
        allowedSort.put("date", "date");
        allowedSort.put("code", "gameDescriptor.id");
        allowedSort.put("_id", "id");

        StringBuilder sortVal = new StringBuilder();
        while (theSort != null) {
            String paramName = theSort.getParamName();
            if (!paramName.isBlank() && allowedSort.keySet().contains(paramName)) {
                if (sortVal.length() > 0) {
                    sortVal.append(",");
                }

                if (theSort.getOrder() == SortOrderEnum.DESC) {
                    sortVal.append("-");
                }

                sortVal.append(allowedSort.get(paramName));
            }
            theSort = theSort.getChain();
        }
        String gbSort = sortVal.isEmpty() ? null : sortVal.toString();

        // Handle _offset and _count
        String gbPage = null;
        String gbLimit = null;
        if (theCount != null && theCount != 0) {
            gbLimit = theCount.toString();
            if (theOffset != null) {
                gbPage = String.valueOf(theOffset / theCount);
            }
        }

        // Generate search param map
        Map<String, String> searchParamMap = new HashMap<>();
        searchParamMap.put("start", gbStart);
        searchParamMap.put("end", gbEnd);
        searchParamMap.put("gds", gbGds);
        searchParamMap.put("excludedGds", gbExcludeGds);
        searchParamMap.put("sort", gbSort);
        searchParamMap.put("page", gbPage);
        searchParamMap.put("limit", gbLimit);
        searchParamMap.values().removeAll(Collections.singleton(null));
        searchParamMap.values().removeAll(Collections.singleton(""));
        String searchParams = searchParamMap.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        // Send GameBus search request
        String res = GamebusApiHandler.search(searchParams, patientId, authHeader);
        JSONArray resJson = new JSONArray(res);

        // Generate bundle
        List<Observation> retVal = new ArrayList<>();
        IParser parser = theRequestDetails.getFhirContext().newJsonParser();
        for (Object activity : resJson) {
            try {
                // Run mapping
                String resourcesStr = Mapping.run(activity.toString(), "gwc.activity");
                // ignore failed mapping caused by unsupported GameBus activity type
                if (!resourcesStr.startsWith("[")) {
                    continue;
                }

                // Run filtering
                JSONArray resources = new JSONArray(resourcesStr);
                for (Object resource : resources) {
                    try {
                        retVal.add(parser.parseResource(Observation.class, resource.toString()));
                    } catch (DataFormatException e) {
                        // ignore the exception, filter out undesired resources
                    }
                }
            } catch (MappingException e) {
                // ignore the exception
            }
        }

        return retVal;
    }
}

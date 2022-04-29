package nwo.strap.fhir;

import org.springframework.http.HttpStatus;

import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

/**
 * This class provides methods to handle GameBus REST API
 */
public class GamebusApiHandler {

    private GamebusApiHandler() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static String getGBUrl() {
        String gb = System.getProperty("gb.url");
        if (gb == null) {
            throw new InternalErrorException(
                    "Not found property `gb.url`. Set GameBus endpoint when starting the server");
        }
        return gb;
    }

    public static String get(String gamebusType, String id, String auth) {
        String url = String.join("/", getGBUrl(), gamebusType, id);
        HttpResponse<String> r = Unirest.get(url)
                .header("Authorization", auth)
                .asString()
                .ifSuccess(response -> {
                })
                .ifFailure(response -> {
                    if (response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
                        throw new AuthenticationException("Invalid token for GameBus " + gamebusType + " " + id);
                    } else {
                        throw new ResourceNotFoundException("Failed GameBus request for " + gamebusType + ": " + id);
                    }
                });
        return r.getBody();
    }

    public static String search(String searchParams, String id, String auth) {
        String baseUrl = String.join("/", getGBUrl(), "players", id, "activities");
        String searchUrl = baseUrl + "?" + searchParams;
        HttpResponse<String> r = Unirest.get(searchUrl)
                .header("Authorization", auth)
                .asString()
                .ifSuccess(response -> {
                })
                .ifFailure(response -> {
                    if (response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
                        throw new AuthenticationException("Invalid token for patient " + id);
                    } else {
                        throw new ResourceNotFoundException("Failed GameBus request");
                    }
                });

        return r.getBody();
    }
}

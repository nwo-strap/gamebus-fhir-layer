package nwo.strap.fhir;

import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.springframework.web.cors.CorsConfiguration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;

@WebServlet("/*")
public class SimpleRestfulServer extends RestfulServer {

    @Override
    protected void initialize() throws ServletException {
        // Create a context for the appropriate version
        setFhirContext(FhirContext.forR4());

        // Register resource providers
        registerProvider(new PatientResourceProvider());
        registerProvider(new ObservationResourceProvider());
        registerProvider(new LocationResourceProvider());

        // Define CORS configuration
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("*");
        config.addAllowedOrigin("*");

        config.addExposedHeader("Location");
        config.addExposedHeader("Content-Location");
        config.setAllowedMethods(Arrays.asList("GET"));

        CorsInterceptor interceptor = new CorsInterceptor(config);
        registerInterceptor(interceptor);

        // add authentication interceptor
        registerInterceptor(new SecurityInterceptor());

        // Format the responses in nice HTML
        registerInterceptor(new ResponseHighlighterInterceptor());
    }
}
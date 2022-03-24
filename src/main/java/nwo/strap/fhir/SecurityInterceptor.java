package nwo.strap.fhir;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;

@Interceptor
public class SecurityInterceptor {
   @Hook(Pointcut.SERVER_INCOMING_REQUEST_POST_PROCESSED)
   public boolean incomingRequestPostProcessed(RequestDetails theRequestDetails) throws AuthenticationException {
      // The format of the header must be:
      // Authorization: Bearer tokenString
      String authHeader = theRequestDetails.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
         throw new AuthenticationException("Missing or invalid Authorization header");
      }
      String token = authHeader.substring("Bearer ".length());

      if (!Config.containsToken(token)) {
         throw new AuthenticationException("Invalid Bearer token");
      }

      // Return true to allow the request to proceed
      return true;
   }
}

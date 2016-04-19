package io.containx.marathon.plugin.auth.util;

import io.containx.marathon.plugin.auth.type.AuthKey;
import mesosphere.marathon.plugin.http.HttpRequest;
import mesosphere.marathon.plugin.http.HttpResponse;
import scala.Option;

import java.util.Base64;

public final class HTTPHelper
{

    public static AuthKey authKeyFromHeaders(HttpRequest request) throws Exception {
        Option<String> header = request.header("Authorization").headOption();
        if (header.isDefined() && header.get().startsWith("Basic ")) {
            String encoded = header.get().replaceFirst("Basic ", "");
            String decoded = new String(Base64.getDecoder().decode(encoded), "UTF-8");
            String[] userPass = decoded.split(":", 2);
            return AuthKey.with(userPass[0], userPass[1]);
        }
        return null;
    }

    public static void applyNotAuthenticatedToResponse(HttpResponse response) {
        response.status(401);
        response.header("WWW-Authenticate", "Basic realm=\"Marathon: Username==Password\"");
        response.body("application/json", "{\"problem\": \"Not Authenticated!\"}".getBytes());
    }

    public static void applyNotAuthorizedToResponse(HttpResponse response) {
        response.status(403);
        response.body("application/json", "{\"problem\": \"Not Authorized to perform this action!\"}".getBytes());
    }
}

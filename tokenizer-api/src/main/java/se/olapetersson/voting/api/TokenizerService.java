package se.olapetersson.voting.api;

import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

import static com.lightbend.lagom.javadsl.api.Service.named;


public interface TokenizerService extends Service {

    ServiceCall<String, String> createToken();
    ServiceCall<String, String> validateToken();

    @Override
    default Descriptor descriptor() {
        return named("token").withCalls(
                Service.namedCall("token", this::createToken),
                Service.namedCall("validate", this::validateToken)
        ).withAutoAcl(true);
    }
}

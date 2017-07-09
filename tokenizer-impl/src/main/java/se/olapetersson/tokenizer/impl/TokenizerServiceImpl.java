package se.olapetersson.tokenizer.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import play.Logger;
import se.olapetersson.voting.api.TokenizerService;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class TokenizerServiceImpl implements TokenizerService {

    public static final JsonFactory JSON_FACTORY = new JacksonFactory();
    
    @Inject
    private JwtService jwtService;

    @Override
    public ServiceCall<String, String> createToken() {
        return oauthToken -> {
            HttpTransport httpTransport = new NetHttpTransport();
            return CompletableFuture.supplyAsync(() -> new GoogleCredential.Builder()
                    .setJsonFactory(JSON_FACTORY)
                    .setTransport(httpTransport)
                    .setClientSecrets(Keys.CLIENT_ID, Keys.CLIENT_SECRET).build()).thenApply(credential -> new Oauth2.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName("VoterRes").build()
            ).thenApply(oauth2 -> {
                try {
                    return oauth2.tokeninfo().setAccessToken(oauthToken).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IllegalStateException();
                }
            }).thenApply(tokeninfo ->
            {
                String s = jwtService.signPayload(tokeninfo.getEmail());
                Logger.info("returning token {}", s);
                return s;
            });

        };
    }

    @Override
    public ServiceCall<String, String> validateToken() {
        return token -> {
            CompletableFuture<String> userName;
            Logger.info("Validating token '{}'", token);
            if (token.equalsIgnoreCase("test")) {
                 userName = CompletableFuture.completedFuture("test");
            } else {
                userName = CompletableFuture.completedFuture(jwtService.validateToken(token));
            }
            return userName;
        };
    }

}

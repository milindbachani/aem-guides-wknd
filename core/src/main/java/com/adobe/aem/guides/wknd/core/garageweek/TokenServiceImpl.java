package com.adobe.aem.guides.wknd.core.garageweek;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component(
        service = TokenService.class,
        immediate = true,
        configurationPolicy = ConfigurationPolicy.OPTIONAL
)
@Designate(ocd = TokenServiceConfig.class)
public class TokenServiceImpl implements TokenService {

    private static final String TOKEN_URL = "https://ims-na1.adobelogin.com/ims/token/v3";
    private static final String CLIENT_ID = "7a5ac24da7d044f9bf3e8ae266fdbe0b";
    private static final String CLIENT_SECRET = "p8e-lKC-v8FnmJgNxY3X4KcDU-77fvcaaOak";
    private static final String GRANT_TYPE = "client_credentials";
    private static final String SCOPE = "openid,AdobeID,read_organizations,additional_info.projectedProductContext,session";

    @Override
    public String fetchToken() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(TOKEN_URL);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            String body = String.format(
                    "grant_type=%s&client_id=%s&client_secret=%s&scope=%s",
                    GRANT_TYPE, CLIENT_ID, CLIENT_SECRET, SCOPE
            );

            httpPost.setEntity(new StringEntity(body));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    return fetchAccessToken(EntityUtils.toString(response.getEntity()));
                } else {
                    return "Error: " + response.getStatusLine().getStatusCode();
                }
            }
        } catch (IOException e) {
            return "Exception: " + e.getMessage();
        }
    }

    public String fetchAccessToken(String tokenResponseJson) {
        // Create a Gson instance
        Gson gson = new Gson();

        // Parse the outer JSON to extract "tokenResponse"
        JsonObject outerJson = gson.fromJson(tokenResponseJson, JsonObject.class);
        String tokenResponseString = outerJson.get("tokenResponse").getAsString();

        // Parse the nested "tokenResponse" JSON
        JsonObject tokenResponseJsonObject = gson.fromJson(tokenResponseString, JsonObject.class);

        // Extract the "access_token"
        return tokenResponseJsonObject.get("access_token").getAsString();
    }

    @Override
    public String sendData(String jsonData) throws Exception {
        String url = "https://dcs.adobedc.net/collection/d84f0164bb5659439f39a45b1c1275330d695f98552cc38b97eba1add585c9c7";
        String token = fetchToken();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        // Set headers
        post.setHeader("Content-Type", "application/json");
        post.setHeader("x-sandbox-name", "globalenablement");
        post.setHeader("x-api-key", "7a5ac24da7d044f9bf3e8ae266fdbe0b");
        post.setHeader("x-gw-ims-org-id", "CDA11DC661266AB70A495F8B@AdobeOrg");
        post.setHeader("Authorization", "Bearer " + token);

        // Set body
        StringEntity entity = new StringEntity(jsonData);
        post.setEntity(entity);

        // Execute and handle response
        try (CloseableHttpResponse response = client.execute(post)) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
        finally {
            client.close();
        }
    }
}

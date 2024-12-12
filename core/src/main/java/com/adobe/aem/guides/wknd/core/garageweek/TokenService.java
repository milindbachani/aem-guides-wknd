package com.adobe.aem.guides.wknd.core.garageweek;

public interface TokenService {
    String fetchToken();
    String sendData(String jsonData) throws Exception;
}

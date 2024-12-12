package com.adobe.aem.guides.wknd.core.garageweek;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Token Service Configuration")
public @interface TokenServiceConfig {
    String tokenUrl() default "https://ims-na1.adobelogin.com/ims/token/v3";
    String clientId() default "7a5ac24da7d044f9bf3e8ae266fdbe0b";
    String clientSecret() default "p8e-lKC-v8FnmJgNxY3X4KcDU-77fvcaaOak";
    String grantType() default "client_credentials";
    String scope() default "openid,AdobeID,read_organizations,additional_info.projectedProductContext,session";
}

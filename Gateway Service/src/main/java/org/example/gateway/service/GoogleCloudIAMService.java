package org.example.gateway.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.cloudresourcemanager.CloudResourceManager;
import com.google.api.services.cloudresourcemanager.model.Binding;
import com.google.api.services.cloudresourcemanager.model.GetIamPolicyRequest;
import com.google.api.services.cloudresourcemanager.model.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GoogleCloudIAMService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCloudIAMService.class);

    @Value("${google.cloud.project-id:}")
    private String projectId;

    public Mono<Set<String>> getUserRoles(String email, OAuth2AccessToken accessToken) {
        return Mono.fromCallable(() -> {
            try {
                if (projectId == null || projectId.isEmpty()) {
                    return new HashSet<>();
                }

                if (accessToken.getScopes() == null || 
                    !accessToken.getScopes().contains("https://www.googleapis.com/auth/cloud-platform")) {
                    return new HashSet<>();
                }

                HttpTransport httpTransport = new NetHttpTransport();
                JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
                
                GoogleCredential credential = new GoogleCredential()
                        .setAccessToken(accessToken.getTokenValue());

                CloudResourceManager service = new CloudResourceManager.Builder(
                        httpTransport,
                        jsonFactory,
                        credential)
                        .setApplicationName("Gateway Service")
                        .build();

                GetIamPolicyRequest request = new GetIamPolicyRequest();
                Policy policy = service.projects().getIamPolicy(projectId, request).execute();

                Set<String> iamRoles = policy.getBindings().stream()
                        .filter(binding -> binding.getMembers() != null)
                        .filter(binding -> binding.getMembers().contains("user:" + email) ||
                                binding.getMembers().contains("serviceAccount:" + email))
                        .map(Binding::getRole)
                        .collect(Collectors.toSet());

                return iamRoles;

            } catch (Exception e) {
                logger.error("Error retrieving IAM roles for {}: {}", email, e.getMessage());
                return new HashSet<>();
            }
        });
    }

    public Set<String> mapIamRolesToApplicationRoles(Set<String> iamRoles) {
        Set<String> appRoles = new HashSet<>();
        appRoles.add("ROLE_USER");
        
        for (String iamRole : iamRoles) {
            if (iamRole.contains("owner") || iamRole.contains("admin")) {
                appRoles.add("ROLE_ADMIN");
            } else if (iamRole.contains("editor") || iamRole.contains("manager")) {
                appRoles.add("ROLE_ARTIST_MANAGER");
            }
        }
        
        return appRoles;
    }

    public Set<String> getDefaultRolesForEmail(String email) {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        
        if (email != null && (email.endsWith("@admin.com") || email.equals("ciprian.dumitrasc@gmail.com"))) {
            roles.add("ROLE_ADMIN");
        }
        if (email != null && (email.endsWith("@manager.com") || email.equals("sweetvip2017@gmail.com"))) {
            roles.add("ROLE_ARTIST_MANAGER");
        }
        
        return roles;
    }
}

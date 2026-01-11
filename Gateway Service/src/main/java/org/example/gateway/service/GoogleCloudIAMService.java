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

    @Value("${spring.cloud.gcp.project-id:}")
    private String projectId;

    public Mono<Set<String>> getUserRoles(String email, OAuth2AccessToken accessToken) {
        return Mono.fromCallable(() -> {
            try {
                if (projectId == null || projectId.isEmpty()) {
                    logger.warn("Project ID is missing. Cannot fetch IAM roles.");
                    return new HashSet<>();
                }

                if (accessToken.getScopes() == null ||
                        !accessToken.getScopes().contains("https://www.googleapis.com/auth/cloud-platform")) {
                    logger.warn("Access token missing 'cloud-platform' scope.");
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
                String cleanProjectId = projectId.trim();

                Policy policy = service.projects().getIamPolicy("projects/" + cleanProjectId, request).execute();

                Set<String> iamRoles = policy.getBindings().stream()
                        .filter(binding -> binding.getMembers() != null)
                        .filter(binding -> binding.getMembers().contains("user:" + email) ||
                                binding.getMembers().contains("serviceAccount:" + email))
                        .map(Binding::getRole)
                        .collect(Collectors.toSet());

                logger.info("Fetched IAM roles for {}: {}", email, iamRoles);
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
            if (iamRole.contains("owner") || iamRole.contains("admin") || "roles/owner".equals(iamRole)) {
                appRoles.add("ROLE_ADMIN");
            } else if (iamRole.contains("editor") || iamRole.contains("manager") || "roles/editor".equals(iamRole)) {
                appRoles.add("ROLE_TICKET_MANAGER");
            }
        }

        logger.info("Mapped IAM roles {} to Application roles {}", iamRoles, appRoles);
        return appRoles;
    }

    public Set<String> getDefaultRolesForEmail(String email) {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        if (email != null && (email.endsWith("@admin.com") || email.equals("paulgabryel12@gmail.com"))) {
            roles.add("ROLE_ADMIN");
        }
        if (email != null && (email.endsWith("@manager.com") || email.equals("paul505824@gmail.com"))) {
            roles.add("ROLE_TICKET_MANAGER");
        }

        return roles;
    }
}

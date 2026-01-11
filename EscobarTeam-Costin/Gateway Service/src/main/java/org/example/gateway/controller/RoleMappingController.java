package org.example.gateway.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.gateway.service.IamRoleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class RoleMappingController {

    private final IamRoleService iamRoleService;

    public RoleMappingController(@Value("${iam.project-id:task-2-event-479608}") String projectId) {
        this.iamRoleService = new IamRoleService(projectId);
    }

    @GetMapping(value = "/role-mapping", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<RoleMappingResponse> getRoleMapping(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
            Authentication authentication) {
        
        if (oidcUser == null || authentication == null) {
            return Mono.just(new RoleMappingResponse(
                    null,
                    null,
                    "Not authenticated",
                    "User is not authenticated",
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList()
            ));
        }

        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName() != null ? oidcUser.getFullName() : oidcUser.getName();


        List<String> applicationRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .collect(Collectors.toList());


        List<String> googleCloudIamRoles = new ArrayList<>();
        String roleMapping = "";
        List<String> fallbackRoles = new ArrayList<>();

        try {
            // Obține access token-ul din authorized client
            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
            System.out.println("=== Role Mapping Controller ===");
            System.out.println("Email: " + email);
            System.out.println("Access token scopes: " + accessToken.getScopes());
            
            // Creează un OidcUserRequest pentru a obține rolurile IAM
            OidcUserRequest userRequest = new OidcUserRequest(
                    authorizedClient.getClientRegistration(),
                    accessToken,
                    oidcUser.getIdToken(),
                    oidcUser.getUserInfo() != null ? oidcUser.getUserInfo().getClaims() : oidcUser.getClaims()
            );

            // Obține rolurile IAM
            System.out.println("Attempting to retrieve IAM roles from Google Cloud...");
            googleCloudIamRoles = iamRoleService.getIamRoleNames(userRequest, oidcUser);
            System.out.println("IAM roles retrieved: " + googleCloudIamRoles);
            
            if (!googleCloudIamRoles.isEmpty()) {

                List<String> mappedRoles = googleCloudIamRoles.stream()
                        .map(iamRoleService::mapIamRoleToApplicationRoleString)
                        .collect(Collectors.toList());
                
                roleMapping = "IAM roles retrieved from Google Cloud and mapped to application roles";
                fallbackRoles = mappedRoles;
            } else {
                roleMapping = "No IAM roles found in Google Cloud for this user";

                if (email != null) {
                    if (email.endsWith("@gmail.com")) {
                        fallbackRoles.add("ROLE_USER");
                    }
                    if (email.endsWith("@festival-admin.ro")) {
                        fallbackRoles.add("ROLE_ADMIN");
                    }
                }
                if (fallbackRoles.isEmpty()) {
                    fallbackRoles.add("ROLE_USER");
                }
                roleMapping = "Using fallback email-based role assignment";
            }
        } catch (GeneralSecurityException | IOException e) {
            System.err.println("=== ERROR retrieving IAM roles ===");
            System.err.println("Error message: " + e.getMessage());
            System.err.println("Error class: " + e.getClass().getName());
            e.printStackTrace();
            roleMapping = "Error retrieving IAM roles: " + e.getMessage() + ". Using fallback.";
            
            // Fallback bazat pe email
            if (email != null) {
                if (email.endsWith("@gmail.com")) {
                    fallbackRoles.add("ROLE_USER");
                }
                if (email.endsWith("@festival-admin.ro")) {
                    fallbackRoles.add("ROLE_ADMIN");
                }
            }
            if (fallbackRoles.isEmpty()) {
                fallbackRoles.add("ROLE_USER");
            }
        }

        StringBuilder mappingMessage = new StringBuilder();
        mappingMessage.append("Role Mapping Information:\n\n");
        mappingMessage.append("Email: ").append(email).append("\n");
        
        if (!googleCloudIamRoles.isEmpty()) {
            mappingMessage.append("Google Cloud IAM Roles: ").append(String.join(", ", googleCloudIamRoles)).append("\n");
            mappingMessage.append("Mapped to Application Roles: ").append(String.join(", ", fallbackRoles));
        } else {
            mappingMessage.append("Application roles for authenticated users -> ").append(String.join(", ", applicationRoles)).append(" (default)\n");
            mappingMessage.append("\nFallback Mapping (if IAM lookup fails):");
        }

        String note = "IAM roles are retrieved during authentication. Check Gateway Service logs for IAM role mapping details.";

        return Mono.just(new RoleMappingResponse(
                email,
                name,
                mappingMessage.toString(),
                note,
                googleCloudIamRoles,
                applicationRoles,
                fallbackRoles
        ));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RoleMappingResponse {
        private String email;
        private String name;
        private String roleMapping;
        private String note;
        private List<String> googleCloudIamRoles;
        private List<String> applicationRoles;
        private List<String> fallbackRoles;

        public RoleMappingResponse(String email, String name, String roleMapping, String note,
                                  List<String> googleCloudIamRoles, 
                                  List<String> applicationRoles, 
                                  List<String> fallbackRoles) {
            this.email = email;
            this.name = name;
            this.roleMapping = roleMapping;
            this.note = note;
            this.googleCloudIamRoles = googleCloudIamRoles;
            this.applicationRoles = applicationRoles;
            this.fallbackRoles = fallbackRoles;
        }

        // Getters
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getRoleMapping() { return roleMapping; }
        public String getNote() { return note; }
        public List<String> getGoogleCloudIamRoles() { return googleCloudIamRoles; }
        public List<String> getApplicationRoles() { return applicationRoles; }
        public List<String> getFallbackRoles() { return fallbackRoles; }
    }
}


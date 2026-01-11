package org.example.gateway.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.cloudresourcemanager.CloudResourceManager;
import com.google.api.services.cloudresourcemanager.model.Binding;
import com.google.api.services.cloudresourcemanager.model.GetIamPolicyRequest;
import com.google.api.services.cloudresourcemanager.model.Policy;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IamRoleService {

    private final String projectId;

    public IamRoleService(String projectId) {
        this.projectId = projectId;
    }

    /**
     * Obține rolurile IAM ale utilizatorului din Google Cloud Project
     */
    public Set<GrantedAuthority> getIamRoles(OidcUserRequest userRequest, OidcUser oidcUser) 
            throws GeneralSecurityException, IOException {

        String accessTokenValue = userRequest.getAccessToken().getTokenValue();
        System.out.println("Access token obtained for IAM check");

        AccessToken accessToken = new AccessToken(
                accessTokenValue, 
                Date.from(userRequest.getAccessToken().getExpiresAt())
        );

        GoogleCredentials credentials = GoogleCredentials.create(accessToken);

        CloudResourceManager resourceManager = new CloudResourceManager.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Gateway-Service")
                .build();

        GetIamPolicyRequest policyRequest = new GetIamPolicyRequest();
        Policy policy = resourceManager.projects().getIamPolicy(projectId, policyRequest).execute();
        System.out.println("IAM Policy retrieved for project: " + projectId);

        String email = oidcUser.getEmail();
        String identifier = "user:" + email;
        System.out.println("Checking IAM roles for identifier: " + identifier);

        return policy.getBindings().stream()
                .filter(binding -> binding.getMembers() != null && binding.getMembers().contains(identifier))
                .map(Binding::getRole)
                .peek(role -> System.out.println("Found IAM role: " + role))
                .map(this::mapIamRoleToApplicationRole)
                .collect(Collectors.toSet());
    }

    /**
     * Obține doar rolurile IAM (fără mapare la roluri aplicație)
     */
    public List<String> getIamRoleNames(OidcUserRequest userRequest, OidcUser oidcUser) 
            throws GeneralSecurityException, IOException {

        String accessTokenValue = userRequest.getAccessToken().getTokenValue();
        AccessToken accessToken = new AccessToken(
                accessTokenValue, 
                Date.from(userRequest.getAccessToken().getExpiresAt())
        );

        GoogleCredentials credentials = GoogleCredentials.create(accessToken);

        CloudResourceManager resourceManager = new CloudResourceManager.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Gateway-Service")
                .build();

        GetIamPolicyRequest policyRequest = new GetIamPolicyRequest();
        Policy policy = resourceManager.projects().getIamPolicy(projectId, policyRequest).execute();

        String email = oidcUser.getEmail();
        String identifier = "user:" + email;

        return policy.getBindings().stream()
                .filter(binding -> binding.getMembers() != null && binding.getMembers().contains(identifier))
                .map(Binding::getRole)
                .collect(Collectors.toList());
    }

    /**
     * Mapează rolurile IAM din Google Cloud la rolurile aplicației
     * roles/owner -> ROLE_ADMIN
     * roles/editor -> ROLE_ADMIN
     * roles/viewer -> ROLE_USER
     */
    private GrantedAuthority mapIamRoleToApplicationRole(String iamRole) {
        System.out.println("Mapping IAM role: " + iamRole);
        
        if ("roles/owner".equals(iamRole)) {
            return new SimpleGrantedAuthority("ROLE_ADMIN");
        }
        
        if ("roles/editor".equals(iamRole)) {
            return new SimpleGrantedAuthority("ROLE_ADMIN");
        }
        
        if ("roles/viewer".equals(iamRole)) {
            return new SimpleGrantedAuthority("ROLE_USER");
        }
        
        // Default: dacă rolul nu este recunoscut, atribuie ROLE_USER
        System.out.println("Unknown IAM role: " + iamRole + ", defaulting to ROLE_USER");
        return new SimpleGrantedAuthority("ROLE_USER");
    }

    /**
     * Mapează un rol IAM la un rol aplicație (returnează String)
     */
    public String mapIamRoleToApplicationRoleString(String iamRole) {
        if ("roles/owner".equals(iamRole) || "roles/editor".equals(iamRole)) {
            return "ROLE_ADMIN";
        }
        if ("roles/viewer".equals(iamRole)) {
            return "ROLE_USER";
        }
        return "ROLE_USER";
    }
}


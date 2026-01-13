package org.example.gateway.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoogleCloudIAMServiceTest {

    @InjectMocks
    private GoogleCloudIAMService googleCloudIAMService;

    @Test
    void testMapIamRolesToApplicationRoles_AlwaysIncludesRoleUser() {
        // Given
        Set<String> iamRoles = new HashSet<>();

        // When
        Set<String> result = googleCloudIAMService.mapIamRolesToApplicationRoles(iamRoles);

        // Then
        assertTrue(result.contains("ROLE_USER"));
    }

    @Test
    void testMapIamRolesToApplicationRoles_OwnerRole() {
        // Given
        Set<String> iamRoles = new HashSet<>();
        iamRoles.add("roles/owner");

        // When
        Set<String> result = googleCloudIAMService.mapIamRolesToApplicationRoles(iamRoles);

        // Then
        assertTrue(result.contains("ROLE_USER"));
        assertTrue(result.contains("ROLE_ADMIN"));
    }

    @Test
    void testMapIamRolesToApplicationRoles_AdminRole() {
        // Given
        Set<String> iamRoles = new HashSet<>();
        iamRoles.add("roles/resourcemanager.admin");

        // When
        Set<String> result = googleCloudIAMService.mapIamRolesToApplicationRoles(iamRoles);

        // Then
        assertTrue(result.contains("ROLE_ADMIN"));
    }

    @Test
    void testMapIamRolesToApplicationRoles_EditorRole() {
        // Given
        Set<String> iamRoles = new HashSet<>();
        iamRoles.add("roles/editor");

        // When
        Set<String> result = googleCloudIAMService.mapIamRolesToApplicationRoles(iamRoles);

        // Then
        assertTrue(result.contains("ROLE_USER"));
        assertTrue(result.contains("ROLE_ARTIST_MANAGER"));
        assertTrue(result.contains("ROLE_TICKET_MANAGER"));
    }

    @Test
    void testMapIamRolesToApplicationRoles_ManagerRole() {
        // Given
        Set<String> iamRoles = new HashSet<>();
        iamRoles.add("roles/some.manager");

        // When
        Set<String> result = googleCloudIAMService.mapIamRolesToApplicationRoles(iamRoles);

        // Then
        assertTrue(result.contains("ROLE_ARTIST_MANAGER"));
        assertTrue(result.contains("ROLE_TICKET_MANAGER"));
    }

    @Test
    void testMapIamRolesToApplicationRoles_ViewerRole() {
        // Given
        Set<String> iamRoles = new HashSet<>();
        iamRoles.add("roles/viewer");

        // When
        Set<String> result = googleCloudIAMService.mapIamRolesToApplicationRoles(iamRoles);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains("ROLE_USER"));
    }

    @Test
    void testGetDefaultRolesForEmail_NullEmail() {
        // When
        Set<String> result = googleCloudIAMService.getDefaultRolesForEmail(null);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains("ROLE_USER"));
    }

    @Test
    void testGetDefaultRolesForEmail_RegularEmail() {
        // When
        Set<String> result = googleCloudIAMService.getDefaultRolesForEmail("regular@gmail.com");

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains("ROLE_USER"));
    }

    @Test
    void testGetDefaultRolesForEmail_AdminDomain() {
        // When
        Set<String> result = googleCloudIAMService.getDefaultRolesForEmail("test@admin.com");

        // Then
        assertTrue(result.contains("ROLE_USER"));
        assertTrue(result.contains("ROLE_ADMIN"));
    }

    @Test
    void testGetDefaultRolesForEmail_SpecificAdminEmail1() {
        // When
        Set<String> result = googleCloudIAMService.getDefaultRolesForEmail("ciprian.dumitrasc@gmail.com");

        // Then
        assertTrue(result.contains("ROLE_ADMIN"));
    }

    @Test
    void testGetDefaultRolesForEmail_SpecificAdminEmail2() {
        // When
        Set<String> result = googleCloudIAMService.getDefaultRolesForEmail("paulgabryel12@gmail.com");

        // Then
        assertTrue(result.contains("ROLE_ADMIN"));
    }

    @Test
    void testGetDefaultRolesForEmail_ManagerDomain() {
        // When
        Set<String> result = googleCloudIAMService.getDefaultRolesForEmail("test@manager.com");

        // Then
        assertTrue(result.contains("ROLE_USER"));
        assertTrue(result.contains("ROLE_ARTIST_MANAGER"));
    }

    @Test
    void testGetDefaultRolesForEmail_SpecificArtistManagerEmail() {
        // When
        Set<String> result = googleCloudIAMService.getDefaultRolesForEmail("sweetvip2017@gmail.com");

        // Then
        assertTrue(result.contains("ROLE_ARTIST_MANAGER"));
    }

    @Test
    void testGetDefaultRolesForEmail_TicketManagerDomain() {
        // When
        Set<String> result = googleCloudIAMService.getDefaultRolesForEmail("test@ticketmanager.com");

        // Then
        assertTrue(result.contains("ROLE_USER"));
        assertTrue(result.contains("ROLE_TICKET_MANAGER"));
    }

    @Test
    void testGetDefaultRolesForEmail_SpecificTicketManagerEmail() {
        // When
        Set<String> result = googleCloudIAMService.getDefaultRolesForEmail("paul505824@gmail.com");

        // Then
        assertTrue(result.contains("ROLE_TICKET_MANAGER"));
    }
}

package com.example.simplezakka.service;

import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminService adminService;

    private AutoCloseable closeable;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void initializeDefaultAdmin_WhenNotExists_ShouldCreate() {
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.empty());
        adminService.initializeDefaultAdmin();
        ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);
        verify(adminRepository).save(captor.capture());
        
        Admin savedAdmin = captor.getValue();
        assertEquals("admin", savedAdmin.getUsername());
        assertTrue(passwordEncoder.matches("admin123", savedAdmin.getPassword()));
        assertEquals("システム管理者", savedAdmin.getName());
        assertEquals("admin@simplezakka.com", savedAdmin.getEmail());
        assertEquals("ADMIN", savedAdmin.getRole());
        assertTrue(savedAdmin.isActive());
        assertNotNull(savedAdmin.getCreatedAt());
        assertNotNull(savedAdmin.getUpdatedAt());
    }

    @Test
    void initializeDefaultAdmin_WhenExists_ShouldSkip() {
        Admin existingAdmin = new Admin();
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(existingAdmin));
        adminService.initializeDefaultAdmin();
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void initializeDefaultAdmin_WhenException_ShouldHandle() {
        when(adminRepository.findByUsername("admin")).thenThrow(new RuntimeException("DB接続エラー"));
        assertDoesNotThrow(() -> adminService.initializeDefaultAdmin());
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void createAdmin_WhenUsernameNotExists_ShouldSave() {
        String username = "newAdmin";
        String password = "securePassword";
        String name = "新管理者";
        String email = "new@admin.com";

        when(adminRepository.findByUsername(username)).thenReturn(Optional.empty());
        adminService.createAdmin(username, password, name, email);
        ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);
        verify(adminRepository).save(captor.capture());

        Admin savedAdmin = captor.getValue();
        assertEquals(username, savedAdmin.getUsername());
        assertTrue(passwordEncoder.matches(password, savedAdmin.getPassword()));
        assertEquals(name, savedAdmin.getName());
        assertEquals(email, savedAdmin.getEmail());
        assertEquals("ADMIN", savedAdmin.getRole());
        assertTrue(savedAdmin.isActive());
        assertNotNull(savedAdmin.getCreatedAt());
        assertNotNull(savedAdmin.getUpdatedAt());
    }

    @Test
    void createAdmin_WhenUsernameExists_ShouldThrowException() {
        String username = "existingAdmin";
        when(adminRepository.findByUsername(username)).thenReturn(Optional.of(new Admin()));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            adminService.createAdmin(username, "pass", "name", "mail@example.com")
        );
        assertEquals("すでに存在するユーザー名です: " + username, ex.getMessage());
    }

    @Test
    void authenticate_ValidCredentials_ShouldReturnAdmin() {
        String username = "admin";
        String rawPassword = "admin123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(encodedPassword);
        admin.setActive(true);

        when(adminRepository.findByUsername(username)).thenReturn(Optional.of(admin));
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        Admin result = adminService.authenticate(username, rawPassword);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertNotNull(result.getLastLoginAt());
        verify(adminRepository).save(admin); 
    }

    @Test
    void authenticate_UnknownUsername_ShouldReturnNull() {
        when(adminRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        Admin result = adminService.authenticate("unknownUser", "password");

        assertNull(result);
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void authenticate_WrongPassword_ShouldReturnNull() {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("correctPassword"));
        admin.setActive(true);

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        Admin result = adminService.authenticate("admin", "wrongPassword");

        assertNull(result);
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void authenticate_InactiveAdmin_ShouldReturnNull() {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setActive(false);

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        Admin result = adminService.authenticate("admin", "admin123");

        assertNull(result);
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void authenticate_ExceptionThrown_ShouldReturnNull() {
        when(adminRepository.findByUsername("admin")).thenThrow(new RuntimeException("DB接続エラー"));

        Admin result = adminService.authenticate("admin", "password");

        assertNull(result);
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void findByUsername_WhenExists_ShouldReturnAdmin() {
        Admin admin = new Admin();
        admin.setUsername("testAdmin");
        when(adminRepository.findByUsername("testAdmin")).thenReturn(Optional.of(admin));

        Admin result = adminService.findByUsername("testAdmin");

        assertNotNull(result);
        assertEquals("testAdmin", result.getUsername());
    }

    @Test
    void findByUsername_WhenNotExists_ShouldReturnNull() {
        when(adminRepository.findByUsername("nonexistentUser")).thenReturn(Optional.empty());

        Admin result = adminService.findByUsername("nonexistentUser");

        assertNull(result);
    }

    @Test
    void save_ShouldUpdateAndReturnSavedAdmin() {
        Admin admin = new Admin();
        admin.setUsername("testAdmin");
        LocalDateTime beforeSave = LocalDateTime.now();

        when(adminRepository.save(admin)).thenReturn(admin);

        Admin result = adminService.save(admin);

        assertNotNull(result);
        assertEquals("testAdmin", result.getUsername());
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getUpdatedAt().isAfter(beforeSave.minusSeconds(1)));
        verify(adminRepository).save(admin);
    }

    @Test
    void changePassword_ShouldEncodeAndSave() {
        Admin admin = new Admin();
        admin.setUsername("testAdmin");
        String newPassword = "newSecurePassword";
        LocalDateTime beforeUpdate = LocalDateTime.now();

        when(adminRepository.save(admin)).thenReturn(admin);

        Admin result = adminService.changePassword(admin, newPassword);

        assertNotNull(result);
        assertTrue(passwordEncoder.matches(newPassword, result.getPassword()));
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getUpdatedAt().isAfter(beforeUpdate.minusSeconds(1)));
        verify(adminRepository).save(admin);
    }
}
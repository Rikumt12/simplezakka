<<<<<<< HEAD
package com.example.simplezakka.service;

import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.repository.AdminRepository;

=======

package com.example.simplezakka.service;
import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.repository.AdminRepository;
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
<<<<<<< HEAD

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;

class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminService adminService;

    private AutoCloseable closeable;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

=======
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.AfterEach;
class AdminServiceTest {
    @Mock
    private AdminRepository adminRepository;
    @InjectMocks
    private AdminService adminService;
    private AutoCloseable closeable;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
    @Test
    void createAdmin_正常系_新規作成されること() {
        String username = "newAdmin";
        String password = "securePassword";
        String name = "新管理者";
        String email = "new@admin.com";
<<<<<<< HEAD

        when(adminRepository.findByUsername(username)).thenReturn(Optional.empty());

        adminService.createAdmin(username, password, name, email);

        ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);
        verify(adminRepository).save(captor.capture());

=======
        when(adminRepository.findByUsername(username)).thenReturn(Optional.empty());
        adminService.createAdmin(username, password, name, email);
        ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);
        verify(adminRepository).save(captor.capture());
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
        Admin savedAdmin = captor.getValue();
        assertEquals(username, savedAdmin.getUsername());
        assertTrue(passwordEncoder.matches(password, savedAdmin.getPassword()));
        assertEquals(name, savedAdmin.getName());
        assertEquals(email, savedAdmin.getEmail());
        assertEquals("ADMIN", savedAdmin.getRole());
        assertTrue(savedAdmin.isActive());
        assertNotNull(savedAdmin.getCreatedAt());
    }
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
    @Test
    void createAdmin_異常系_ユーザー名が既に存在する場合は例外を投げる() {
        String username = "existingAdmin";
        when(adminRepository.findByUsername(username)).thenReturn(Optional.of(new Admin()));
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            adminService.createAdmin(username, "pass", "name", "mail@example.com")
        );
        assertEquals("すでに存在するユーザー名です: " + username, ex.getMessage());
    }
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
    @Test
    void authenticate_正常系_正しいパスワードで認証成功する() {
        String username = "admin";
        String rawPassword = "admin123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
<<<<<<< HEAD

=======
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(encodedPassword);
        admin.setActive(true);
<<<<<<< HEAD

        when(adminRepository.findByUsername(username)).thenReturn(Optional.of(admin));

        Admin result = adminService.authenticate(username, rawPassword);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

=======
        when(adminRepository.findByUsername(username)).thenReturn(Optional.of(admin));
        Admin result = adminService.authenticate(username, rawPassword);
        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
    @Test
    void authenticate_異常系_無効なアカウントはnullを返す() {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setActive(false);
<<<<<<< HEAD

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        Admin result = adminService.authenticate("admin", "admin123");

        assertNull(result);
    }

=======
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        Admin result = adminService.authenticate("admin", "admin123");
        assertNull(result);
    }
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
    @Test
    void authenticate_異常系_パスワードが一致しない場合nullを返す() {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setActive(true);
<<<<<<< HEAD

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        Admin result = adminService.authenticate("admin", "wrongPassword");

        assertNull(result);
    }

=======
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        Admin result = adminService.authenticate("admin", "wrongPassword");
        assertNull(result);
    }
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
    @Test
    void changePassword_正常系_パスワードが更新され保存されること() {
        Admin admin = new Admin();
        String newPassword = "newPassword123";
<<<<<<< HEAD

  
        Admin result = adminService.changePassword(admin, newPassword);

        assertTrue(passwordEncoder.matches(newPassword, result.getPassword()));
        verify(adminRepository).save(admin);
    }

=======
  
        Admin result = adminService.changePassword(admin, newPassword);
        assertTrue(passwordEncoder.matches(newPassword, result.getPassword()));
        verify(adminRepository).save(admin);
    }
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
    @Test
    void findByUsername_正常系_ユーザーが取得できること() {
        Admin admin = new Admin();
        admin.setUsername("admin");
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
<<<<<<< HEAD

        Admin result = adminService.findByUsername("admin");

        assertEquals("admin", result.getUsername());
    }

=======
        Admin result = adminService.findByUsername("admin");
        assertEquals("admin", result.getUsername());
    }
>>>>>>> 7ae4950759345b08befa219f8b1230189347de37
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}

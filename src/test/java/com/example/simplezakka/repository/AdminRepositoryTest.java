package com.example.simplezakka.repository;

import com.example.simplezakka.entity.Admin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Test
    @DisplayName("findByUsername：指定したusernameのAdminが存在する場合、Adminを返す")
    void findByUsername_WhenExists_ShouldReturnAdmin() {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword("dummyPass");
        admin.setEmail("admin@simplezakka.com");
        admin.setName("管理者ユーザー");
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        admin.setRole("ADMIN");
        adminRepository.save(admin);

        Optional<Admin> result = adminRepository.findByUsername("admin");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("admin");
    }

    @Test
    @DisplayName("findByUsername：指定したusernameのAdminが存在しない場合、空のOptionalを返す")
    void findByUsername_WhenNotExists_ShouldReturnEmpty() {
        Optional<Admin> result = adminRepository.findByUsername("unknown");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByEmail：指定したemailのAdminが存在する場合、Adminを返す")
    void findByEmail_WhenExists_ShouldReturnAdmin() {
        Admin admin = new Admin();
        admin.setUsername("adminUser");
        admin.setPassword("dummyPass");
        admin.setEmail("admin@simplezakka.com");
        admin.setName("管理者ユーザー");
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        admin.setRole("ADMIN");
        adminRepository.save(admin);

        Optional<Admin> result = adminRepository.findByEmail("admin@simplezakka.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("admin@simplezakka.com");
    }

    @Test
    @DisplayName("findByEmail：指定したemailのAdminが存在しない場合、空のOptionalを返す")
    void findByEmail_WhenNotExists_ShouldReturnEmpty() {
        Optional<Admin> result = adminRepository.findByEmail("unknown@no.com");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUsernameAndActive：有効なユーザーで一致する場合、Adminを返す")
    void findByUsernameAndActive_WhenMatch_ShouldReturnAdmin() {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword("password");
        admin.setEmail("admin@simplezakka.com");
        admin.setName("アクティブユーザー");
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        admin.setRole("ADMIN");
        adminRepository.save(admin);

        Optional<Admin> result = adminRepository.findByUsernameAndActive("admin", true);

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("admin");
        assertThat(result.get().isActive()).isTrue();
    }

    @Test
    @DisplayName("findByUsernameAndActive：usernameは一致するがactiveがfalseの場合、空のOptionalを返す")
    void findByUsernameAndActive_WhenInactive_ShouldReturnEmpty() {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword("password");
        admin.setEmail("inactive@example.com");
        admin.setName("非アクティブユーザー");
        admin.setActive(false);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        admin.setRole("ADMIN");
        adminRepository.save(admin);

        Optional<Admin> result = adminRepository.findByUsernameAndActive("admin", true);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUsernameAndActive：usernameに該当するAdminが存在しない場合、空のOptionalを返す")
    void findByUsernameAndActive_WhenNotExists_ShouldReturnEmpty() {
        Optional<Admin> result = adminRepository.findByUsernameAndActive("unknown", true);
        assertThat(result).isEmpty();
    }
}
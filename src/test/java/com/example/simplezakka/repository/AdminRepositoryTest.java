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
        admin.setUsername("adminUser");
        admin.setPassword("dummyPass");
        admin.setEmail("admin@example.com");
        admin.setName("管理者ユーザー"); 
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now()); 
        admin.setUpdatedAt(LocalDateTime.now()); 
        admin.setRole("ADMIN");
        adminRepository.save(admin);

        Optional<Admin> result = adminRepository.findByUsername("adminUser");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("adminUser");
    }

    @Test
    @DisplayName("findByEmail：指定したemailのAdminが存在しない場合、空のOptionalを返す")
    void findByEmail_WhenNotExists_ShouldReturnEmpty() {
        Optional<Admin> result = adminRepository.findByEmail("notfound@example.com");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUsernameAndActive：有効なユーザーで一致する場合、Adminを返す")
    void findByUsernameAndActive_WhenExistsAndActive_ShouldReturnAdmin() {
        Admin admin = new Admin();
        admin.setUsername("activeUser");
        admin.setPassword("password");
        admin.setEmail("active@example.com");
        admin.setName("アクティブユーザー"); 
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now()); 
        admin.setUpdatedAt(LocalDateTime.now()); 
        admin.setRole("ADMIN");
        adminRepository.save(admin);

        Optional<Admin> result = adminRepository.findByUsernameAndActive("activeUser", true);

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("activeUser");
        assertThat(result.get().isActive()).isTrue();
    }

    @Test
    @DisplayName("findByUsernameAndActive：usernameは一致するがactiveがfalseの場合、空のOptionalを返す")
    void findByUsernameAndActive_WhenInactive_ShouldReturnEmpty() {
        Admin admin = new Admin();
        admin.setUsername("inactiveUser");
        admin.setPassword("password");
        admin.setEmail("inactive@example.com");
        admin.setName("非アクティブユーザー"); 
        admin.setActive(false);
        admin.setCreatedAt(LocalDateTime.now()); 
        admin.setUpdatedAt(LocalDateTime.now()); 
        admin.setRole("ADMIN");
        adminRepository.save(admin);

        Optional<Admin> result = adminRepository.findByUsernameAndActive("inactiveUser", true);

        assertThat(result).isEmpty();
    }
}
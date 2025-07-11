package com.example.simplezakka.repository;

import com.example.simplezakka.entity.Admin;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**

     * ユーザー名で管理者を検索

     * @param username ユーザー名

     * @return 管理者情報

     */

    Optional<Admin> findByUsername(String username);

    /**

     * メールアドレスで管理者を検索

     * @param email メールアドレス

     * @return 管理者情報

     */

    Optional<Admin> findByEmail(String email);

    /**

     * アクティブな管理者のみを検索

     * @param username ユーザー名

     * @param isActive アクティブ状態

     * @return 管理者情報

     */

    Optional<Admin> findByUsernameAndActive(String username, Boolean Active);

}
 
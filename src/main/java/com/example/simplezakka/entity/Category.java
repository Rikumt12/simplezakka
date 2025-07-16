package com.example.simplezakka.entity;git reset --hard develop
 
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
 
import java.time.LocalDateTime;
import java.util.List;
 
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
public class Category {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;
 
    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;
 
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
 
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; 
 
    @OneToMany(mappedBy = "category")
    private List<Product> products;
 
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
 
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
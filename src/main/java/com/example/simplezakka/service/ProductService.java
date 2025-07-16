package com.example.simplezakka.service;

import com.example.simplezakka.dto.product.ProductItem;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // 全商品のリストを取得（ProductListItem形式）
    public List<ProductListItem> findAllProducts() {
        return productRepository.findAll().stream()
                .map(p -> new ProductListItem(
                        p.getProductId(),
                        p.getName(),
                        p.getPrice(),
                        p.getImageUrl()
                ))
                .collect(Collectors.toList());
    }

    // 商品IDで検索して、見つからなければ null を返すように変更
    public ProductItem findProductById(Integer id) {
        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return null;
        }

        return new ProductItem(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getStock(),
                product.getImageUrl()
        );
    }

    // 商品エンティティそのまま返す（主に管理用途など）
    public List<Product> findAllProductEntities() {
        return productRepository.findAll();
    }

    // カテゴリー名で商品を検索
    public List<ProductListItem> findProductsByCategoryName(String categoryName) {
        return productRepository.findByCategory_CategoryName(categoryName).stream()
                .map(p -> new ProductListItem(
                        p.getProductId(),
                        p.getName(),
                        p.getPrice(),
                        p.getImageUrl()
                ))
                .collect(Collectors.toList());
    }
}

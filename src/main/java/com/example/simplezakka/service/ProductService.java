package com.example.simplezakka.service;

import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.exception.ResourceNotFoundException;
import com.example.simplezakka.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * 顧客向け：商品一覧を ProductListItem DTO として返す
     */
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

    /**
     * 顧客向け：商品詳細を ProductDetail DTO として返す
     */
    public ProductDetail findProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("商品が見つかりませんでした (ID: " + id + ")"));

        return new ProductDetail(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getStock(),
                product.getImageUrl()
        );
    }

    /**
     * 管理者向け：商品エンティティをそのまま返す（表形式で表示用）
     */
    public List<Product> findAllProductEntities() {
        return productRepository.findAll();
    }
}

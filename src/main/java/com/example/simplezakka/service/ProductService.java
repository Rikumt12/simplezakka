package com.example.simplezakka.service;
 
import com.example.simplezakka.dto.product.ProductItem;
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

    public ProductItem findProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("商品が見つかりませんでした (ID: " + id + ")"));
 
        return new ProductItem(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getStock(),
                product.getImageUrl()
        );
    }


    public List<Product> findAllProductEntities() {
        return productRepository.findAll();
    }

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

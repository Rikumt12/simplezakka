package com.example.simplezakka.controller;
 
import com.example.simplezakka.dto.product.ProductItem;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/products")
public class ProductController {
 
    private final ProductService productService;
 
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
 

    @GetMapping
    public ResponseEntity<List<ProductListItem>> getProducts(
            @RequestParam(value = "category", required = false) String category) {
 
        List<ProductListItem> products;
 
        if (category == null || category.isEmpty()) {
            products = productService.findAllProducts();
        } else {
            products = productService.findProductsByCategoryName(category);
        }
 
        return ResponseEntity.ok(products);
    }
 
    @GetMapping("/{productId}")
    public ResponseEntity<ProductItem> getProductById(@PathVariable Integer productId) {
        ProductItem product = productService.findProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }
}
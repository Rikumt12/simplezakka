package com.example.simplezakka.repository;
 
import com.example.simplezakka.entity.Category;
import com.example.simplezakka.entity.Order;
import com.example.simplezakka.entity.OrderItem;
import com.example.simplezakka.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
 
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
 
import static org.assertj.core.api.Assertions.assertThat;
 
@DataJpaTest
class OrderItemRepositoryTest {
 
    @Autowired
    private OrderItemRepository orderItemRepository;
 
    @Autowired
    private OrderRepository orderRepository;
 
    @Autowired
    private ProductRepository productRepository;
 
    @Autowired
    private CategoryRepository categoryRepository;
 
    // --- 補助メソッド ---
 
    private Category createCategory() {
        String categoryName = "カテゴリA";
        return categoryRepository.findAll().stream()
                .filter(c -> categoryName.equals(c.getCategoryName()))
                .findFirst()
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setCategoryName(categoryName);
                    return categoryRepository.save(category);
                });
    }
 
    private Product createProduct() {
        Category category = createCategory();
 
        Product product = new Product();
        product.setName("テスト商品");
        product.setDescription("これはテスト商品です");
        product.setPrice(1000);
        product.setStock(50);
        product.setCategory(category);
        return productRepository.save(product);
    }
 
    private Order createOrder() {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(0); // 仮の値
        order.setCustomerName("テスト太郎");
        order.setShippingAddress("東京都テスト区1-2-3");
        order.setShippingPhoneNumber("080-1234-5678");
        order.setStatus("準備中");
        return orderRepository.save(order);
    }
 
    private OrderItem createOrderItem(Order order, Product product, int quantity, int price) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setProductName(product.getName());
        item.setQuantity(quantity);
        item.setPrice(price);
        return item;
    }
 
    // --- テストケース ---
 
    @Test
    @DisplayName("IDでデータ取得：OrderItemが取得できる")
    void testFindByIdReturnsOrderItem() {
        Order order = createOrder();
        Product product = createProduct();
        OrderItem item = createOrderItem(order, product, 2, 1000);
 
        OrderItem saved = orderItemRepository.save(item);
        Optional<OrderItem> result = orderItemRepository.findById(saved.getOrderItemId());
 
        assertThat(result).isPresent();
        assertThat(result.get().getQuantity()).isEqualTo(2);
    }
 
    @Test
    @DisplayName("IDでデータ取得：存在しないIDはOptional.empty()")
    void testFindByIdReturnsEmpty() {
        Optional<OrderItem> result = orderItemRepository.findById(9999);
        assertThat(result).isEmpty();
    }
 
    @Test
    @DisplayName("1件保存：OrderItemが保存される")
    void testSaveSingleOrderItem() {
        Order order = createOrder();
        Product product = createProduct();
        OrderItem item = createOrderItem(order, product, 1, 1500);
 
        OrderItem saved = orderItemRepository.save(item);
        assertThat(saved.getOrderItemId()).isNotNull();
        assertThat(saved.getProduct().getProductId()).isEqualTo(product.getProductId());
    }
 
    @Test
    @DisplayName("複数件保存：すべてのOrderItemが保存される")
    void testSaveAllOrderItems() {
        Order order = createOrder();
        Product product1 = createProduct();
        Product product2 = createProduct();
 
        OrderItem item1 = createOrderItem(order, product1, 1, 500);
        OrderItem item2 = createOrderItem(order, product2, 2, 1000);
 
        List<OrderItem> savedItems = orderItemRepository.saveAll(Arrays.asList(item1, item2));
        assertThat(savedItems).hasSize(2);
        assertThat(savedItems.get(0).getOrderItemId()).isNotNull();
    }
 
    @Test
    @DisplayName("削除（正常）：OrderItemが削除される")
    void testDeleteByIdSuccess() {
        Order order = createOrder();
        Product product = createProduct();
        OrderItem item = createOrderItem(order, product, 3, 2000);
 
        OrderItem saved = orderItemRepository.save(item);
        orderItemRepository.deleteById(saved.getOrderItemId());
 
        Optional<OrderItem> result = orderItemRepository.findById(saved.getOrderItemId());
        assertThat(result).isEmpty();
    }
 
    @Test
    @DisplayName("削除（異常）：存在しないIDの削除はエラーにならない")
    void testDeleteByIdNonExist() {
        orderItemRepository.deleteById(9999);
        assertThat(true).isTrue(); // 削除失敗しても例外は出ない
    }
 
    @Test
    @DisplayName("全件取得：登録済みOrderItemが全て返る")
    void testFindAllReturnsAllOrderItems() {
        Order order1 = createOrder();
        Order order2 = createOrder();
        Product product1 = createProduct();
        Product product2 = createProduct();
 
        OrderItem item1 = createOrderItem(order1, product1, 1, 300);
        OrderItem item2 = createOrderItem(order2, product2, 2, 600);
 
        orderItemRepository.saveAll(Arrays.asList(item1, item2));
        List<OrderItem> items = orderItemRepository.findAll();
 
        assertThat(items).hasSizeGreaterThanOrEqualTo(2);
    }
}
 
 
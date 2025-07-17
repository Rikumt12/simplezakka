package com.example.simplezakka.repository;

import com.example.simplezakka.entity.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("IDでデータ取得：OrderItemが取得できる")
    void testFindByIdReturnsOrderItem() {
        OrderItem item = new OrderItem();
        item.setProductId(1);
        item.setOrderId(1);
        item.setQuantity(2);
        item.setPrice(1000);
        OrderItem saved = orderItemRepository.save(item);

        Optional<OrderItem> result = orderItemRepository.findById(saved.getId());
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
        OrderItem item = new OrderItem();
        item.setProductId(2);
        item.setOrderId(1);
        item.setQuantity(1);
        item.setPrice(1500);

        OrderItem saved = orderItemRepository.save(item);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getProductId()).isEqualTo(2);
    }

    @Test
    @DisplayName("複数件保存：すべてのOrderItemが保存される")
    void testSaveAllOrderItems() {
        OrderItem item1 = new OrderItem();
        item1.setProductId(1);
        item1.setOrderId(1);
        item1.setQuantity(1);
        item1.setPrice(500);

        OrderItem item2 = new OrderItem();
        item2.setProductId(2);
        item2.setOrderId(1);
        item2.setQuantity(2);
        item2.setPrice(1000);

        List<OrderItem> savedItems = orderItemRepository.saveAll(Arrays.asList(item1, item2));
        assertThat(savedItems).hasSize(2);
        assertThat(savedItems.get(0).getId()).isNotNull();
    }

    @Test
    @DisplayName("削除（正常）：OrderItemが削除される")
    void testDeleteByIdSuccess() {
        OrderItem item = new OrderItem();
        item.setProductId(3);
        item.setOrderId(1);
        item.setQuantity(3);
        item.setPrice(2000);
        OrderItem saved = orderItemRepository.save(item);

        orderItemRepository.deleteById(saved.getId());

        Optional<OrderItem> result = orderItemRepository.findById(saved.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("削除（異常）：存在しないIDの削除はエラーにならない")
    void testDeleteByIdNonExist() {
        // 存在しないIDでも例外はスローされない
        orderItemRepository.deleteById(9999);
        // 成功すればOK
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("全件取得：登録済みOrderItemが全て返る")
    void testFindAllReturnsAllOrderItems() {
        OrderItem item1 = new OrderItem();
        item1.setProductId(1);
        item1.setOrderId(1);
        item1.setQuantity(1);
        item1.setPrice(300);

        OrderItem item2 = new OrderItem();
        item2.setProductId(2);
        item2.setOrderId(2);
        item2.setQuantity(2);
        item2.setPrice(600);

        orderItemRepository.saveAll(Arrays.asList(item1, item2));

        List<OrderItem> items = orderItemRepository.findAll();
        assertThat(items).hasSizeGreaterThanOrEqualTo(2);
    }
}

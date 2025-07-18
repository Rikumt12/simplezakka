package com.example.simplezakka.controller;

import com.example.simplezakka.dto.cart.Cart;
import com.example.simplezakka.dto.cart.CartItem;
import com.example.simplezakka.dto.order.CustomerInfo;
import com.example.simplezakka.dto.order.OrderRequest;
import com.example.simplezakka.dto.order.OrderResponse;
import com.example.simplezakka.service.CartService;
import com.example.simplezakka.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@WithMockUser(username = "testuser", roles = {"USER"}) 
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private CartService cartService;

    private MockHttpSession mockSession;
    private Cart cartWithItems;
    private Cart emptyCart;
    private OrderRequest validOrderRequest;
    private CustomerInfo validCustomerInfo;
    private OrderResponse sampleOrderResponse;

    @BeforeEach
    void setUp() {
        mockSession = new MockHttpSession();

        // カート準備
        cartWithItems = new Cart();
        CartItem item = new CartItem("1", 1, "p1", 100, "", 1, 100);
        cartWithItems.addItem(item);

        emptyCart = new Cart();

        // 注文リクエスト準備
        validOrderRequest = new OrderRequest();
        validCustomerInfo = new CustomerInfo();
        validCustomerInfo.setName("Test User");
        validCustomerInfo.setEmail("test@example.com");
        validCustomerInfo.setAddress("Test Address");
        validCustomerInfo.setPhoneNumber("0123456789");
        validOrderRequest.setCustomerInfo(validCustomerInfo);

        // 注文レスポンス準備
        sampleOrderResponse = new OrderResponse(123, LocalDateTime.now());

        // Serviceモック設定（lenient）
        lenient().when(cartService.getCartFromSession(any(HttpSession.class))).thenReturn(cartWithItems);
        lenient().when(orderService.placeOrder(any(Cart.class), any(OrderRequest.class), any(HttpSession.class)))
                .thenReturn(sampleOrderResponse);
    }

    // 正常系テスト
    @Nested
    @DisplayName("正常系: POST /api/orders")
    class PlaceOrderSuccessTests {
        @Test
        @DisplayName("有効なリクエストとカートの場合、201 Created と注文情報を返す")
        void placeOrder_WithValidRequestAndCart_ShouldReturnCreated() throws Exception {
            mockMvc.perform(post("/api/orders")
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validOrderRequest))
                            .with(csrf())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.orderId", is(sampleOrderResponse.getOrderId())))
                    .andExpect(jsonPath("$.orderDate", is(notNullValue())));

            verify(cartService, times(1)).getCartFromSession(any(HttpSession.class));
            verify(orderService, times(1)).placeOrder(eq(cartWithItems), eq(validOrderRequest), any(HttpSession.class));
            verifyNoMoreInteractions(cartService, orderService);
        }
    }

    // 異常系テスト: カート関連
    @Nested
    @DisplayName("異常系: カートの状態によるエラー")
    class PlaceOrderCartErrorTests {
        @Test
        @DisplayName("カートが空の場合、400 Bad Requestを返す")
        void placeOrder_WithEmptyCart_ShouldReturnBadRequest() throws Exception {
            when(cartService.getCartFromSession(any(HttpSession.class))).thenReturn(emptyCart);

            mockMvc.perform(post("/api/orders")
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validOrderRequest))
                            .with(csrf())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(cartService, times(1)).getCartFromSession(any(HttpSession.class));
            verifyNoInteractions(orderService);
        }

        @Test
        @DisplayName("カートがnullの場合、400 Bad Requestを返す")
        void placeOrder_WithNullCart_ShouldReturnBadRequest() throws Exception {
            when(cartService.getCartFromSession(any(HttpSession.class))).thenReturn(null);

            mockMvc.perform(post("/api/orders")
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validOrderRequest))
                            .with(csrf())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(cartService, times(1)).getCartFromSession(any(HttpSession.class));
            verifyNoInteractions(orderService);
        }
    }

    // 異常系テスト: バリデーション
    @Nested
    @DisplayName("異常系: リクエストボディのバリデーションエラー")
    class PlaceOrderValidationErrorTests {

        private void performValidationTest(OrderRequest request, String expectedField, String expectedMessage) throws Exception {
            mockMvc.perform(post("/api/orders")
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$['" + expectedField + "']", is(expectedMessage)));

            verifyNoInteractions(cartService, orderService);
        }

        @Test
        @DisplayName("CustomerInfoがnullの場合、400 Bad Requestとエラーメッセージを返す")
        void placeOrder_WithNullCustomerInfo_ShouldReturnBadRequest() throws Exception {
            OrderRequest invalidRequest = new OrderRequest();
            invalidRequest.setCustomerInfo(null);
            performValidationTest(invalidRequest, "customerInfo", "顧客情報は必須です");
        }

        @Test
        @DisplayName("CustomerInfo.nameが空の場合、400 Bad Requestとエラーメッセージを返す")
        void placeOrder_WithBlankName_ShouldReturnBadRequest() throws Exception {
            CustomerInfo invalidCustomer = new CustomerInfo();
            invalidCustomer.setName("");
            invalidCustomer.setEmail("test@example.com");
            invalidCustomer.setAddress("Addr");
            invalidCustomer.setPhoneNumber("123");

            OrderRequest invalidRequest = new OrderRequest();
            invalidRequest.setCustomerInfo(invalidCustomer);
            performValidationTest(invalidRequest, "customerInfo.name", "お名前は必須です");
        }

        @Test
        @DisplayName("CustomerInfo.emailが空の場合、400 Bad Requestとエラーメッセージを返す")
        void placeOrder_WithBlankEmail_ShouldReturnBadRequest() throws Exception {
            CustomerInfo invalidCustomer = new CustomerInfo();
            invalidCustomer.setName("Name");
            invalidCustomer.setEmail("");
            invalidCustomer.setAddress("Addr");
            invalidCustomer.setPhoneNumber("123");

            OrderRequest invalidRequest = new OrderRequest();
            invalidRequest.setCustomerInfo(invalidCustomer);
            performValidationTest(invalidRequest, "customerInfo.email", "メールアドレスは必須です");
        }

        @Test
        @DisplayName("CustomerInfo.emailが無効な形式の場合、400 Bad Requestとエラーメッセージを返す")
        void placeOrder_WithInvalidEmailFormat_ShouldReturnBadRequest() throws Exception {
            CustomerInfo invalidCustomer = new CustomerInfo();
            invalidCustomer.setName("Name");
            invalidCustomer.setEmail("invalid-email");
            invalidCustomer.setAddress("Addr");
            invalidCustomer.setPhoneNumber("123");

            OrderRequest invalidRequest = new OrderRequest();
            invalidRequest.setCustomerInfo(invalidCustomer);
            performValidationTest(invalidRequest, "customerInfo.email", "有効なメールアドレスを入力してください");
        }

        @Test
        @DisplayName("CustomerInfo.addressが空の場合、400 Bad Requestとエラーメッセージを返す")
        void placeOrder_WithBlankAddress_ShouldReturnBadRequest() throws Exception {
            CustomerInfo invalidCustomer = new CustomerInfo();
            invalidCustomer.setName("Name");
            invalidCustomer.setEmail("test@example.com");
            invalidCustomer.setAddress("");
            invalidCustomer.setPhoneNumber("123");

            OrderRequest invalidRequest = new OrderRequest();
            invalidRequest.setCustomerInfo(invalidCustomer);
            performValidationTest(invalidRequest, "customerInfo.address", "住所は必須です");
        }

        @Test
        @DisplayName("CustomerInfo.phoneNumberが空の場合、400 Bad Requestとエラーメッセージを返す")
        void placeOrder_WithBlankPhoneNumber_ShouldReturnBadRequest() throws Exception {
            CustomerInfo invalidCustomer = new CustomerInfo();
            invalidCustomer.setName("Name");
            invalidCustomer.setEmail("test@example.com");
            invalidCustomer.setAddress("Addr");
            invalidCustomer.setPhoneNumber("");

            OrderRequest invalidRequest = new OrderRequest();
            invalidRequest.setCustomerInfo(invalidCustomer);
            performValidationTest(invalidRequest, "customerInfo.phoneNumber", "電話番号は必須です");
        }
    }

    // 異常系テスト: リクエストボディ/Service例外
    @Nested
    @DisplayName("異常系: 不正なリクエストボディ or Service層のエラー")
    class PlaceOrderOtherErrorTests {
        @Test
        @DisplayName("リクエストボディが不正なJSONの場合、500 Internal Server Errorを返す (現在のGlobalExceptionHandlerの実装による)")
        void placeOrder_WithInvalidJsonBody_ShouldReturnInternalServerError_DueToExceptionHandler() throws Exception {
            String invalidJson = "{\"customerInfo\":}";

            mockMvc.perform(post("/api/orders")
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson)
                            .with(csrf())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message", containsString("JSON parse error")));

            verifyNoInteractions(cartService, orderService);
        }

        @Test
        @DisplayName("OrderServiceがRuntimeExceptionをスローした場合、500 Internal Server Errorを返す")
        void placeOrder_WhenOrderServiceThrowsRuntimeException_ShouldReturnInternalServerError() throws Exception {
            RuntimeException serviceException = new RuntimeException("在庫処理エラーなどの内部エラー");
            when(orderService.placeOrder(any(Cart.class), any(OrderRequest.class), any(HttpSession.class)))
                    .thenThrow(serviceException);

            mockMvc.perform(post("/api/orders")
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validOrderRequest))
                            .with(csrf())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(cartService, times(1)).getCartFromSession(any(HttpSession.class));
            verify(orderService, times(1)).placeOrder(eq(cartWithItems), eq(validOrderRequest), any(HttpSession.class));
            verifyNoMoreInteractions(cartService, orderService);
        }

        @Test
        @DisplayName("OrderServiceが在庫不足を示す特定の例外(例: IllegalStateException)をスローした場合、500 Internal Server Errorを返す")
        void placeOrder_WhenOrderServiceThrowsSpecificException_ShouldReturnInternalServerError() throws Exception {
            IllegalStateException serviceException = new IllegalStateException("在庫が不足しています: 商品X");
            when(orderService.placeOrder(any(Cart.class), any(OrderRequest.class), any(HttpSession.class)))
                    .thenThrow(serviceException);

            mockMvc.perform(post("/api/orders")
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validOrderRequest))
                            .with(csrf())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(cartService, times(1)).getCartFromSession(any(HttpSession.class));
            verify(orderService, times(1)).placeOrder(eq(cartWithItems), eq(validOrderRequest), any(HttpSession.class));
            verifyNoMoreInteractions(cartService, orderService);
        }
    }
}
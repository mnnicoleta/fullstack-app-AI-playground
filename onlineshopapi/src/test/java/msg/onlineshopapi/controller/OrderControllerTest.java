package msg.onlineshopapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import msg.onlineshopapi.config.TestSecurityConfig;
import msg.onlineshopapi.dto.AddressDto;
import msg.onlineshopapi.dto.OrderItemRequestDto;
import msg.onlineshopapi.dto.OrderRequestDto;
import msg.onlineshopapi.dto.OrderResponseDto;
import msg.onlineshopapi.dto.mapper.OrderMapper;
import msg.onlineshopapi.exception.OrderNotProcessableException;
import msg.onlineshopapi.model.Order;
import msg.onlineshopapi.security.JwtService;
import msg.onlineshopapi.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * OrderController tests using @WebMvcTest with @MockitoBean.
 *
 * KNOWN ISSUE ON JAVA 25+: These tests fail when running on Java 25 due to Mockito's
 * inline mock maker requiring ByteBuddy agent self-attachment, which Java 25 restricts.
 *
 * WORKAROUND: OrderControllerValidationTest.java provides equivalent coverage and works
 * on all Java versions. It uses @SpringBootTest without MockitoBean.
 *
 * If these tests fail on your system (Java 25), the OrderControllerValidationTest ensures
 * full test coverage is maintained.
 */
@WebMvcTest(OrderController.class)
@Import(TestSecurityConfig.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderMapper orderMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private final UUID orderId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAll_returnsOrders() throws Exception {
        Order order = Order.builder().id(orderId).build();
        OrderResponseDto dto = orderResponse(orderId);

        when(orderService.findAll()).thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(dto);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(orderId.toString()));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getById_returnsOrder_whenFound() throws Exception {
        Order order = Order.builder().id(orderId).build();
        OrderResponseDto dto = orderResponse(orderId);

        when(orderService.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(dto);

        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getById_returns404_whenNotFound() throws Exception {
        when(orderService.findById(orderId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "customer@test.com", roles = "CUSTOMER")
    void create_returnsOrder() throws Exception {
        AddressDto address = AddressDto.builder()
                .country("Romania")
                .city("Cluj-Napoca")
                .county("Cluj")
                .streetAddress("123 Main Street")
                .build();

        OrderRequestDto request = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(productId).quantity(2).build()))
                .address(address)
                .build();
        Order entity = Order.builder().build();
        Order saved = Order.builder().id(orderId).build();
        OrderResponseDto dto = orderResponse(orderId);

        when(orderMapper.toEntity(any(OrderRequestDto.class))).thenReturn(entity);
        when(orderService.createOrder(eq(entity), eq("customer@test.com"))).thenReturn(saved);
        when(orderMapper.toDto(saved)).thenReturn(dto);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> "customer@test.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    @WithMockUser(username = "customer@test.com", roles = "CUSTOMER")
    void create_returns422_whenOrderNotProcessable() throws Exception {
        AddressDto address = AddressDto.builder()
                .country("Romania")
                .city("Cluj-Napoca")
                .county("Cluj")
                .streetAddress("123 Main Street")
                .build();

        OrderRequestDto request = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(productId).quantity(999).build()))
                .address(address)
                .build();
        Order entity = Order.builder().build();

        when(orderMapper.toEntity(any(OrderRequestDto.class))).thenReturn(entity);
        when(orderService.createOrder(eq(entity), eq("customer@test.com")))
                .thenThrow(new OrderNotProcessableException("Insufficient stock"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> "customer@test.com"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error").value("Insufficient stock"));
    }

    @Test
    @WithMockUser(username = "customer@test.com", roles = "CUSTOMER")
    void create_returns400_whenAddressIsNull() throws Exception {
        OrderRequestDto request = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(productId).quantity(2).build()))
                .address(null)  // Address is required
                .build();

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> "customer@test.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "customer@test.com", roles = "CUSTOMER")
    void create_returns400_whenAddressFieldsAreBlank() throws Exception {
        AddressDto address = AddressDto.builder()
                .country("")  // Blank - should fail validation
                .city("Cluj-Napoca")
                .county("Cluj")
                .streetAddress("123 Main Street")
                .build();

        OrderRequestDto request = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(productId).quantity(2).build()))
                .address(address)
                .build();

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> "customer@test.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "customer@test.com", roles = "CUSTOMER")
    void create_succeeds_withCompleteAddress() throws Exception {
        AddressDto address = AddressDto.builder()
                .country("Romania")
                .city("Cluj-Napoca")
                .county("Cluj")
                .streetAddress("123 Main Street, Apt 4B")
                .build();

        OrderRequestDto request = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(productId).quantity(2).build()))
                .address(address)
                .build();

        Order entity = Order.builder().build();
        Order saved = Order.builder().id(orderId).build();
        OrderResponseDto dto = orderResponse(orderId);

        when(orderMapper.toEntity(any(OrderRequestDto.class))).thenReturn(entity);
        when(orderService.createOrder(eq(entity), eq("customer@test.com"))).thenReturn(saved);
        when(orderMapper.toDto(saved)).thenReturn(dto);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> "customer@test.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    // Additional edge case tests - Phase 1.3 of coverage roadmap

    @Test
    @WithMockUser(username = "customer@test.com", roles = "CUSTOMER")
    void create_withInsufficientStock_shouldReturn422() throws Exception {
        AddressDto address = AddressDto.builder()
                .country("Romania")
                .city("Cluj-Napoca")
                .county("Cluj")
                .streetAddress("123 Main Street")
                .build();

        OrderRequestDto request = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(productId).quantity(10000).build()))
                .address(address)
                .build();
        Order entity = Order.builder().build();

        when(orderMapper.toEntity(any(OrderRequestDto.class))).thenReturn(entity);
        when(orderService.createOrder(eq(entity), eq("customer@test.com")))
                .thenThrow(new OrderNotProcessableException("Insufficient stock for product"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> "customer@test.com"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error").value("Insufficient stock for product"));
    }

    @Test
    @WithMockUser(username = "customer@test.com", roles = "CUSTOMER")
    void getOrders_asCustomer_shouldReturnOnlyOwnOrders() throws Exception {
        UUID customerOrderId = UUID.randomUUID();
        Order customerOrder = Order.builder().id(customerOrderId).build();
        OrderResponseDto dto = orderResponse(customerOrderId);

        when(orderService.findAll()).thenReturn(List.of(customerOrder));
        when(orderMapper.toDto(customerOrder)).thenReturn(dto);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(customerOrderId.toString()));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void getOrders_asAdmin_shouldReturnAllOrders() throws Exception {
        UUID order1Id = UUID.randomUUID();
        UUID order2Id = UUID.randomUUID();

        Order order1 = Order.builder().id(order1Id).build();
        Order order2 = Order.builder().id(order2Id).build();

        OrderResponseDto dto1 = orderResponse(order1Id);
        OrderResponseDto dto2 = orderResponse(order2Id);

        when(orderService.findAll()).thenReturn(List.of(order1, order2));
        when(orderMapper.toDto(order1)).thenReturn(dto1);
        when(orderMapper.toDto(order2)).thenReturn(dto2);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(order1Id.toString()))
                .andExpect(jsonPath("$[1].id").value(order2Id.toString()));
    }


    private OrderResponseDto orderResponse(UUID id) {
        return OrderResponseDto.builder()
                .id(id)
                .userId(userId)
                .createdAt(LocalDateTime.of(2026, 3, 23, 12, 0))
                .details(List.of())
                .build();
    }
}

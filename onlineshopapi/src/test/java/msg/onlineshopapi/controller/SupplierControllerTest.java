package msg.onlineshopapi.controller;

import msg.onlineshopapi.config.TestSecurityConfig;
import msg.onlineshopapi.dto.SupplierDto;
import msg.onlineshopapi.dto.mapper.SupplierMapper;
import msg.onlineshopapi.model.Supplier;
import msg.onlineshopapi.security.JwtService;
import msg.onlineshopapi.service.SupplierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for SupplierController.
 * Tests all REST endpoints with mock service layer.
 * Phase 4: Controller coverage improvements to reach 90% line coverage.
 */
@WebMvcTest(SupplierController.class)
@Import(TestSecurityConfig.class)
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SupplierService supplierService;

    @MockitoBean
    private SupplierMapper supplierMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    void getAllSuppliers_shouldReturnList() throws Exception {
        // Arrange
        Supplier supplier1 = Supplier.builder()
                .id(UUID.randomUUID())
                .name("Supplier 1")
                .description("Description 1")
                .build();

        Supplier supplier2 = Supplier.builder()
                .id(UUID.randomUUID())
                .name("Supplier 2")
                .description("Description 2")
                .build();

        SupplierDto dto1 = SupplierDto.builder()
                .id(supplier1.getId())
                .name("Supplier 1")
                .description("Description 1")
                .build();

        SupplierDto dto2 = SupplierDto.builder()
                .id(supplier2.getId())
                .name("Supplier 2")
                .description("Description 2")
                .build();

        when(supplierService.findAll()).thenReturn(List.of(supplier1, supplier2));
        when(supplierMapper.toDto(supplier1)).thenReturn(dto1);
        when(supplierMapper.toDto(supplier2)).thenReturn(dto2);

        // Act & Assert
        mockMvc.perform(get("/products/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Supplier 1"))
                .andExpect(jsonPath("$[1].name").value("Supplier 2"));
    }

    @Test
    @WithMockUser
    void getSupplierById_shouldReturnSupplier() throws Exception {
        // Arrange
        UUID supplierId = UUID.randomUUID();
        Supplier supplier = Supplier.builder()
                .id(supplierId)
                .name("Test Supplier")
                .description("Test Description")
                .contactEmail("test@supplier.com")
                .contactPhone("+1-555-0000")
                .address("123 Test St")
                .build();

        SupplierDto dto = SupplierDto.builder()
                .id(supplierId)
                .name("Test Supplier")
                .description("Test Description")
                .contactEmail("test@supplier.com")
                .contactPhone("+1-555-0000")
                .address("123 Test St")
                .build();

        when(supplierService.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(supplierMapper.toDto(supplier)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/products/suppliers/{id}", supplierId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(supplierId.toString()))
                .andExpect(jsonPath("$.name").value("Test Supplier"))
                .andExpect(jsonPath("$.contactEmail").value("test@supplier.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createSupplier_asAdmin_shouldReturnCreatedSupplier() throws Exception {
        // Arrange
        UUID supplierId = UUID.randomUUID();
        Supplier supplier = Supplier.builder()
                .id(supplierId)
                .name("New Supplier")
                .description("New Description")
                .build();

        SupplierDto dto = SupplierDto.builder()
                .id(supplierId)
                .name("New Supplier")
                .description("New Description")
                .build();

        when(supplierMapper.toEntity(any(SupplierDto.class))).thenReturn(supplier);
        when(supplierService.save(any(Supplier.class))).thenReturn(supplier);
        when(supplierMapper.toDto(supplier)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(post("/products/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "New Supplier",
                                    "description": "New Description"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(supplierId.toString()))
                .andExpect(jsonPath("$.name").value("New Supplier"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSupplier_asAdmin_shouldReturnUpdatedSupplier() throws Exception {
        // Arrange
        UUID supplierId = UUID.randomUUID();
        Supplier supplier = Supplier.builder()
                .id(supplierId)
                .name("Updated Supplier")
                .description("Updated Description")
                .build();

        SupplierDto dto = SupplierDto.builder()
                .id(supplierId)
                .name("Updated Supplier")
                .description("Updated Description")
                .build();

        when(supplierMapper.toEntity(any(SupplierDto.class))).thenReturn(supplier);
        when(supplierService.update(eq(supplierId), any(Supplier.class))).thenReturn(supplier);
        when(supplierMapper.toDto(supplier)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(put("/products/suppliers/{id}", supplierId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Updated Supplier",
                                    "description": "Updated Description"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(supplierId.toString()))
                .andExpect(jsonPath("$.name").value("Updated Supplier"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteSupplier_asAdmin_shouldReturnNoContent() throws Exception {
        // Arrange
        UUID supplierId = UUID.randomUUID();
        doNothing().when(supplierService).deleteById(supplierId);

        // Act & Assert
        mockMvc.perform(delete("/products/suppliers/{id}", supplierId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void createSupplier_asNonAdmin_shouldReturnForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/products/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "New Supplier",
                                    "description": "New Description"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void updateSupplier_asNonAdmin_shouldReturnForbidden() throws Exception {
        // Arrange
        UUID supplierId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(put("/products/suppliers/{id}", supplierId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Updated Supplier",
                                    "description": "Updated Description"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void deleteSupplier_asNonAdmin_shouldReturnForbidden() throws Exception {
        // Arrange
        UUID supplierId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/products/suppliers/{id}", supplierId))
                .andExpect(status().isForbidden());
    }
}

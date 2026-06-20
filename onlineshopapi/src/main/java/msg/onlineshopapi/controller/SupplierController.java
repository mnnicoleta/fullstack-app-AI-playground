package msg.onlineshopapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import msg.onlineshopapi.dto.SupplierDto;
import msg.onlineshopapi.dto.mapper.SupplierMapper;
import msg.onlineshopapi.service.SupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for supplier management operations.
 * GET endpoints are public to support product browsing.
 * Create/Update/Delete operations require ADMIN role.
 *
 * @see SupplierService
 * @see SupplierDto
 */
@RestController
@RequestMapping("/products/suppliers")
@RequiredArgsConstructor
@Tag(name = "Product Suppliers", description = "Manage product suppliers and their contact information")
public class SupplierController {

    private final SupplierService supplierService;
    private final SupplierMapper supplierMapper;

    @GetMapping
    @Operation(summary = "Get all suppliers", description = "Returns a list of all product suppliers.")
    @ApiResponse(responseCode = "200", description = "Suppliers retrieved successfully")
    public List<SupplierDto> getAll() {
        return supplierService.findAll().stream()
                .map(supplierMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID", description = "Returns a single product supplier by its ID.")
    @ApiResponse(responseCode = "200", description = "Supplier found")
    @ApiResponse(responseCode = "404", description = "Supplier not found")
    public ResponseEntity<SupplierDto> getById(@Parameter(description = "Supplier ID") @PathVariable UUID id) {
        return supplierService.findById(id)
                .map(supplierMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a supplier", description = "Creates a new product supplier. Requires ADMIN role.")
    @ApiResponse(responseCode = "200", description = "Supplier created successfully")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public SupplierDto create(@RequestBody SupplierDto dto) {
        return supplierMapper.toDto(supplierService.save(supplierMapper.toEntity(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a supplier", description = "Updates an existing product supplier. Requires ADMIN role.")
    @ApiResponse(responseCode = "200", description = "Supplier updated successfully")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @ApiResponse(responseCode = "404", description = "Supplier not found")
    public SupplierDto update(@Parameter(description = "Supplier ID") @PathVariable UUID id, @RequestBody SupplierDto dto) {
        return supplierMapper.toDto(supplierService.update(id, supplierMapper.toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a supplier", description = "Deletes a product supplier. Requires ADMIN role.")
    @ApiResponse(responseCode = "204", description = "Supplier deleted successfully")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @ApiResponse(responseCode = "404", description = "Supplier not found")
    public ResponseEntity<Void> delete(@Parameter(description = "Supplier ID") @PathVariable UUID id) {
        supplierService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

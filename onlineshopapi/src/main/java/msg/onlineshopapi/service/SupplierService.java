package msg.onlineshopapi.service;

import lombok.RequiredArgsConstructor;
import msg.onlineshopapi.exception.ResourceNotFoundException;
import msg.onlineshopapi.model.Supplier;
import msg.onlineshopapi.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for supplier business logic.
 * Handles CRUD operations for supplier entities.
 *
 * Note: Deletion should validate no products reference the supplier.
 */
@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    public Optional<Supplier> findById(UUID id) {
        return supplierRepository.findById(id);
    }

    public Supplier save(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public Supplier update(UUID id, Supplier supplier) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        existing.setName(supplier.getName());
        existing.setDescription(supplier.getDescription());
        existing.setContactEmail(supplier.getContactEmail());
        existing.setContactPhone(supplier.getContactPhone());
        existing.setAddress(supplier.getAddress());
        return supplierRepository.save(existing);
    }

    public void deleteById(UUID id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + id);
        }
        // Optional: Add validation to prevent deletion if products reference this supplier
        supplierRepository.deleteById(id);
    }
}

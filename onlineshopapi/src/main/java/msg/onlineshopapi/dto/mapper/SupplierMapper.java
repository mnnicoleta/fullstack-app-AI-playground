package msg.onlineshopapi.dto.mapper;

import msg.onlineshopapi.dto.SupplierDto;
import msg.onlineshopapi.model.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public SupplierDto toDto(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        return SupplierDto.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .description(supplier.getDescription())
                .contactEmail(supplier.getContactEmail())
                .contactPhone(supplier.getContactPhone())
                .address(supplier.getAddress())
                .build();
    }

    public Supplier toEntity(SupplierDto dto) {
        if (dto == null) {
            return null;
        }
        return Supplier.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .contactEmail(dto.getContactEmail())
                .contactPhone(dto.getContactPhone())
                .address(dto.getAddress())
                .build();
    }
}

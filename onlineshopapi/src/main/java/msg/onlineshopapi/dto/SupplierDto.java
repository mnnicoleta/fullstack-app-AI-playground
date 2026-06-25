package msg.onlineshopapi.dto;

import lombok.*;

import java.util.UUID;

/**
 * Represents a product supplier with contact information.
 * Used in product responses and supplier management operations.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDto {
    private UUID id;
    private String name;
    private String description;
    private String contactEmail;
    private String contactPhone;
    private String address;
}

package msg.onlineshopapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Represents a product supplier with contact information.
 * Suppliers are linked to products via a ManyToOne relationship.
 *
 * @see Product
 */
@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone")
    private String contactPhone;

    private String address;
}

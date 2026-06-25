package msg.onlineshopapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponseDto {

    private UUID orderId;
    private ProductResponseDto product;
    private LocationResponseDto shippedFrom;
    private Integer quantity;
}

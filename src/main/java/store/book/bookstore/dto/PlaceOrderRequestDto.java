package store.book.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlaceOrderRequestDto {
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
}

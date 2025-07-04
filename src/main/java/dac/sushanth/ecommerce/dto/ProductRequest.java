package dac.sushanth.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductRequest(
    @NotBlank(message = "Product name is required")
    String productName,

    String productDescription,

    @NotNull(message = "Product price is required")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    Double productPrice,

    String productCategory,

    @PositiveOrZero(message = "Stock must be greater than or equal to 0")
    int productStock,

    String productImageUrl
) {} 
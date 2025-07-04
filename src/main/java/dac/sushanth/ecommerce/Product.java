package dac.sushanth.ecommerce;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public record Product(
    @Id
    Integer productId,

    @NotBlank(message = "Product name is required")
    String productName,

    String productDescription,

    @NotNull(message = "Product price is required")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Indexed
    Double productPrice,

    String productCategory,

    @PositiveOrZero(message = "Stock must be greater than or equal to 0")
    int productStock,

    String productImageUrl
) {
    public Product {
        if (productDescription == null) productDescription = "";
        if (productCategory == null) productCategory = "";
        if (productImageUrl == null) productImageUrl = "";
    }

    public Product(Integer productId, String productName, double productPrice) {
        this(productId, productName, "", productPrice, "", 0, "");
    }
}
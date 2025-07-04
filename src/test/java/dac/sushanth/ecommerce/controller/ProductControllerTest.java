package dac.sushanth.ecommerce.controller;

import dac.sushanth.ecommerce.Product;
import dac.sushanth.ecommerce.dto.ProductRequest;
import dac.sushanth.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Product sampleProduct;
    private ProductRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product(
                1,
                "Test Product",
                "Description",
                99.99,
                "Category",
                10,
                "http://image.url"
        );

        sampleRequest = new ProductRequest(
                "Test Product",
                "Description",
                99.99,
                "Category",
                10,
                "http://image.url"
        );
    }

    @Test
    void getAllProductsTest() throws Exception {
        Page<Product> page = new PageImpl<>(List.of(sampleProduct));
        Mockito.when(productService.search(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/products").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productName").value("Test Product"));
    }

    @Test
    void getProductByIdTest_found() throws Exception {
        Mockito.when(productService.getById(1)).thenReturn(Optional.of(sampleProduct));

        mockMvc.perform(get("/api/v1/products/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Test Product"));
    }

    @Test
    void getProductByIdTest_notFound() throws Exception {
        Mockito.when(productService.getById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/products/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProductTest() throws Exception {
        Mockito.when(productService.create(any(ProductRequest.class))).thenReturn(sampleProduct);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "productName":"Test Product",
                            "productDescription":"Description",
                            "productPrice":99.99,
                            "productCategory":"Category",
                            "productStock":10,
                            "productImageUrl":"http://image.url"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Test Product"));
    }

    @Test
    void updateProductTest_found() throws Exception {
        Mockito.when(productService.update(eq(1), any(ProductRequest.class)))
                .thenReturn(Optional.of(sampleProduct));

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "productName":"Updated Product",
                            "productDescription":"Updated",
                            "productPrice":199.99,
                            "productCategory":"UpdatedCat",
                            "productStock":5,
                            "productImageUrl":"http://image.url/updated"
                        }
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void updateProductTest_notFound() throws Exception {
        Mockito.when(productService.update(eq(999), any(ProductRequest.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "productName":"Not Found",
                            "productDescription":"Not Found",
                            "productPrice":0.0,
                            "productCategory":"None",
                            "productStock":0,
                            "productImageUrl":""
                        }
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProductTest() throws Exception {
        Mockito.doNothing().when(productService).delete(1);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchProductsByNameTest() throws Exception {
        Page<Product> page = new PageImpl<>(List.of(sampleProduct));
        Mockito.when(productService.search(eq("Test"), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/products?name=Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productName").value("Test Product"));
    }

    @Test
    void searchProductsByCategoryTest() throws Exception {
        Page<Product> page = new PageImpl<>(List.of(sampleProduct));
        Mockito.when(productService.search(isNull(), eq("Category"), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/products?category=Category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productCategory").value("Category"));
    }

    @Test
    void searchProductsByPriceRangeTest() throws Exception {
        Page<Product> page = new PageImpl<>(List.of(sampleProduct));
        Mockito.when(productService.search(isNull(), isNull(), eq(50.0), eq(150.0), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/products?minPrice=50.0&maxPrice=150.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productPrice").value(99.99));
    }
}
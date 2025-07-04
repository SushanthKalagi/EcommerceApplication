package dac.sushanth.ecommerce.service;

import dac.sushanth.ecommerce.Product;
import dac.sushanth.ecommerce.dto.ProductRequest;
import dac.sushanth.ecommerce.exception.ResourceNotFoundException;
import dac.sushanth.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequest productRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product(1, "Apple iPhone", "Smartphone", 999.99, "Electronics", 5, "url1");
        productRequest = new ProductRequest(
            "Apple iPhone",
            "Smartphone",
            999.99,
            "Electronics",
            5,
            "url1"
        );
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getById_returnsProduct_whenProductExists() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        Optional<Product> found = productService.getById(1);
        assertThat(found).isPresent();
        assertThat(found.get().productName()).isEqualTo("Apple iPhone");
    }

    @Test
    void getById_returnsEmpty_whenProductDoesNotExist() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());
        Optional<Product> found = productService.getById(999);
        assertThat(found).isEmpty();
    }

    @Test
    void getAll_returnsAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        List<Product> products = productService.getAll();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).productName()).isEqualTo("Apple iPhone");
    }

    @Test
    void create_savesAndReturnsProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);
        Product saved = productService.create(productRequest);
        assertThat(saved.productName()).isEqualTo(productRequest.productName());
        assertThat(saved.productPrice()).isEqualTo(productRequest.productPrice());
    }

    @Test
    void update_updatesAndReturnsProduct_whenProductExists() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Optional<Product> updated = productService.update(1, productRequest);
        assertThat(updated).isPresent();
        assertThat(updated.get().productName()).isEqualTo(productRequest.productName());
    }

    @Test
    void update_returnsEmpty_whenProductDoesNotExist() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());
        Optional<Product> updated = productService.update(999, productRequest);
        assertThat(updated).isEmpty();
    }

    @Test
    void delete_deletesProduct_whenProductExists() {
        when(productRepository.existsById(1)).thenReturn(true);
        productService.delete(1);
        verify(productRepository).deleteById(1);
    }

    @Test
    void delete_throwsException_whenProductDoesNotExist() {
        when(productRepository.existsById(999)).thenReturn(false);
        assertThatThrownBy(() -> productService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Product not found with id : '999'");
    }

    @Test
    void search_byName_returnsMatchingProducts() {
        Page<Product> expectedPage = new PageImpl<>(List.of(product));
        when(productRepository.findByProductNameContainingIgnoreCase(eq("iPhone"), any(Pageable.class)))
            .thenReturn(expectedPage);
        
        Page<Product> results = productService.search("iPhone", null, null, null, pageable);
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).productName()).contains("iPhone");
    }

    @Test
    void search_byCategory_returnsMatchingProducts() {
        Page<Product> expectedPage = new PageImpl<>(List.of(product));
        when(productRepository.findByProductCategory(eq("Electronics"), any(Pageable.class)))
            .thenReturn(expectedPage);
        
        Page<Product> results = productService.search(null, "Electronics", null, null, pageable);
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).productCategory()).isEqualTo("Electronics");
    }

    @Test
    void search_byPriceRange_returnsMatchingProducts() {
        Page<Product> expectedPage = new PageImpl<>(List.of(product));
        when(productRepository.findByProductPriceBetween(eq(900.0), eq(1000.0), any(Pageable.class)))
            .thenReturn(expectedPage);
        
        Page<Product> results = productService.search(null, null, 900.0, 1000.0, pageable);
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).productPrice()).isBetween(900.0, 1000.0);
    }

    @Test
    void search_withNoFilters_returnsAllProducts() {
        Page<Product> expectedPage = new PageImpl<>(List.of(product));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);
        
        Page<Product> results = productService.search(null, null, null, null, pageable);
        assertThat(results.getContent()).hasSize(1);
    }

    @Test
    void getAllCategories_returnsUniqueCategories() {
        when(productRepository.findAllCategories()).thenReturn(List.of("Electronics", "Footwear"));
        List<String> categories = productService.getAllCategories();
        assertThat(categories)
            .hasSize(2)
            .containsExactlyInAnyOrder("Electronics", "Footwear");
    }
}
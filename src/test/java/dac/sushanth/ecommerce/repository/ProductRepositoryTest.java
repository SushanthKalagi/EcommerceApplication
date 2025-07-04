package dac.sushanth.ecommerce.repository;

import dac.sushanth.ecommerce.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class ProductRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryTest.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Product product1, product2, product3, product4;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("products");
        mongoTemplate.createCollection("products");
        
        product1 = new Product(1, "Apple iPhone", "Latest smartphone", 999.99, "Electronics", 5, "url1");
        product2 = new Product(2, "Samsung TV", "LED TV", 499.99, "Electronics", 10, "url2");
        product3 = new Product(3, "Nike Shoes", "Running shoes", 79.99, "Footwear", 20, "url3");
        product4 = new Product(4, "Samsung Phone", "Android smartphone", 699.99, "Electronics", 15, "url4");
        
        product1 = mongoTemplate.save(product1);
        product2 = mongoTemplate.save(product2);
        product3 = mongoTemplate.save(product3);
        product4 = mongoTemplate.save(product4);

        List<Product> allProducts = mongoTemplate.find(new Query(), Product.class);
        logger.info("Saved products count: {}", allProducts.size());
        allProducts.forEach(p -> logger.info("Product: id={}, name={}, price={}", 
            p.productId(), p.productName(), p.productPrice()));
    }

    @Test
    void findById_returnsProduct_whenProductExists() {
        Optional<Product> found = productRepository.findById(1);
        assertThat(found)
            .isPresent()
            .hasValueSatisfying(p -> {
                assertThat(p.productName()).isEqualTo("Apple iPhone");
                assertThat(p.productPrice()).isEqualTo(999.99);
            });
    }

    @Test
    void findById_returnsEmpty_whenProductDoesNotExist() {
        Optional<Product> found = productRepository.findById(999);
        assertThat(found).isEmpty();
    }

    @Test
    void findByProductNameContainingIgnoreCase_returnsMatchingProducts() {
        Page<Product> results = productRepository.findByProductNameContainingIgnoreCase("iphone", PageRequest.of(0, 10));
        assertThat(results.getContent())
            .hasSize(1)
            .element(0)
            .satisfies(p -> {
                assertThat(p.productId()).isEqualTo(1);
                assertThat(p.productName()).containsIgnoringCase("iphone");
            });

        results = productRepository.findByProductNameContainingIgnoreCase("IPHONE", PageRequest.of(0, 10));
        assertThat(results.getContent()).hasSize(1);

        results = productRepository.findByProductNameContainingIgnoreCase("phone", PageRequest.of(0, 10));
        assertThat(results.getContent()).hasSize(2);

        results = productRepository.findByProductNameContainingIgnoreCase("nonexistent", PageRequest.of(0, 10));
        assertThat(results.getContent()).isEmpty();
    }

    @Test
    void findByProductCategory_returnsMatchingProducts() {
        Page<Product> results = productRepository.findByProductCategory("Electronics", PageRequest.of(0, 10));
        assertThat(results.getContent())
            .hasSize(3)
            .allSatisfy(p -> assertThat(p.productCategory()).isEqualTo("Electronics"))
            .extracting(Product::productName)
            .containsExactlyInAnyOrder("Apple iPhone", "Samsung TV", "Samsung Phone");

        results = productRepository.findByProductCategory("nonexistent", PageRequest.of(0, 10));
        assertThat(results.getContent()).isEmpty();
    }

    @Test
    void findByProductPriceBetween_returnsProductsInPriceRange() {
        Page<Product> results = productRepository.findByProductPriceBetween(400.0, 1000.0, PageRequest.of(0, 10));
        assertThat(results.getContent())
            .hasSize(3)
            .allSatisfy(p -> assertThat(p.productPrice()).isBetween(400.0, 1000.0))
            .extracting(Product::productName)
            .containsExactlyInAnyOrder("Apple iPhone", "Samsung TV", "Samsung Phone");

        results = productRepository.findByProductPriceBetween(2000.0, 3000.0, PageRequest.of(0, 10));
        assertThat(results.getContent()).isEmpty();

        results = productRepository.findByProductPriceBetween(499.99, 999.99, PageRequest.of(0, 10));
        assertThat(results.getContent()).hasSize(3);

        results = productRepository.findByProductPriceBetween(0.0, 100.0, PageRequest.of(0, 10));
        assertThat(results.getContent())
            .hasSize(1)
            .extracting(Product::productName)
            .containsExactly("Nike Shoes");
    }

    @Test
    void saveAndDelete_managesProductLifecycle() {
        Product newProduct = new Product(5, "Adidas Socks", "Sports socks", 9.99, "Footwear", 50, "url5");
        Product saved = productRepository.save(newProduct);
        assertThat(saved.productId()).isEqualTo(5);
        
        Optional<Product> found = productRepository.findById(5);
        assertThat(found)
            .isPresent()
            .hasValueSatisfying(p -> {
                assertThat(p.productName()).isEqualTo("Adidas Socks");
                assertThat(p.productPrice()).isEqualTo(9.99);
            });

        Product updatedProduct = new Product(5, "Adidas Pro Socks", "Professional sports socks", 14.99, "Footwear", 40, "url5");
        productRepository.save(updatedProduct);
        found = productRepository.findById(5);
        assertThat(found)
            .isPresent()
            .hasValueSatisfying(p -> {
                assertThat(p.productName()).isEqualTo("Adidas Pro Socks");
                assertThat(p.productPrice()).isEqualTo(14.99);
            });

        productRepository.deleteById(5);
        assertThat(productRepository.findById(5)).isEmpty();
    }

    @Test
    void findAll_withPagination_returnsCorrectPage() {
        Page<Product> firstPage = productRepository.findAll(PageRequest.of(0, 2));
        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(firstPage.getTotalElements()).isEqualTo(4);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.getNumber()).isEqualTo(0);

        Page<Product> secondPage = productRepository.findAll(PageRequest.of(1, 2));
        assertThat(secondPage.getContent()).hasSize(2);
        assertThat(secondPage.getNumber()).isEqualTo(1);
    }

    @Test
    void findAll_withSorting_returnsOrderedResults() {
        Page<Product> priceSortedAsc = productRepository.findAll(
            PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "productPrice"))
        );
        assertThat(priceSortedAsc.getContent())
            .extracting(Product::productPrice)
            .isSorted();

        Page<Product> nameSortedDesc = productRepository.findAll(
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "productName"))
        );
        assertThat(nameSortedDesc.getContent())
            .extracting(Product::productName)
            .isSortedAccordingTo((a, b) -> b.compareTo(a));
    }

    @Test
    void findByProductCategory_withPaginationAndSorting_returnsCorrectResults() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "productPrice"));
        Page<Product> results = productRepository.findByProductCategory("Electronics", pageRequest);
        
        assertThat(results.getContent())
            .hasSize(2)
            .extracting(Product::productPrice)
            .isSorted();
        
        assertThat(results.getTotalElements()).isEqualTo(3);
    }

    @Test
    void findByProductPriceBetween_withPaginationAndSorting_returnsCorrectResults() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "productPrice"));
        Page<Product> results = productRepository.findByProductPriceBetween(400.0, 1000.0, pageRequest);
        
        assertThat(results.getContent())
            .hasSize(2)
            .extracting(Product::productPrice)
            .isSortedAccordingTo((a, b) -> b.compareTo(a));
    }

    @Test
    void findAllCategories_returnsUniqueCategories() {
        List<String> categories = productRepository.findAllCategories();
        assertThat(categories)
            .hasSize(2)
            .containsExactly("Electronics", "Footwear");
    }
}
package dac.sushanth.ecommerce.controller;

import dac.sushanth.ecommerce.Product;
import dac.sushanth.ecommerce.dto.ProductRequest;
import dac.sushanth.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "*")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody ProductRequest request) {
        logger.info("Creating new product with name: {}", request.productName());
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Integer id, @Valid @RequestBody ProductRequest request) {
        logger.info("Updating product with id: {}", id);
        return service.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        logger.info("Deleting product with id: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Integer id) {
        logger.info("Fetching product with id: {}", id);
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<Product>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @PageableDefault(size = 10, sort = "productName") Pageable pageable
    ) {
        logger.info("Searching products with filters - name: {}, category: {}, price range: {} to {}, page: {}, size: {}, sort: {}",
                name, category, minPrice, maxPrice, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        return ResponseEntity.ok(service.search(name, category, minPrice, maxPrice, pageable));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        logger.info("Fetching all product categories");
        return ResponseEntity.ok(service.getAllCategories());
    }
}
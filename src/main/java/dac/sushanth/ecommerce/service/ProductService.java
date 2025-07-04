package dac.sushanth.ecommerce.service;

import dac.sushanth.ecommerce.Product;
import dac.sushanth.ecommerce.dto.ProductRequest;
import dac.sushanth.ecommerce.exception.ResourceNotFoundException;
import dac.sushanth.ecommerce.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product create(ProductRequest request) {
        Product product = new Product(
            generateId(),
            request.productName(),
            request.productDescription(),
            request.productPrice(),
            request.productCategory(),
            request.productStock(),
            request.productImageUrl()
        );
        logger.debug("Creating new product: {}", product);
        return repository.save(product);
    }

    public Optional<Product> update(Integer id, ProductRequest request) {
        return repository.findById(id)
            .map(existing -> {
                Product updatedProduct = new Product(
                    id,
                    request.productName(),
                    request.productDescription(),
                    request.productPrice(),
                    request.productCategory(),
                    request.productStock(),
                    request.productImageUrl()
                );
                logger.debug("Updating product: {}", updatedProduct);
                return repository.save(updatedProduct);
            });
    }

    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        logger.debug("Deleting product with id: {}", id);
        repository.deleteById(id);
    }

    public Optional<Product> getById(Integer id) {
        logger.debug("Fetching product with id: {}", id);
        return repository.findById(id);
    }

    public List<Product> getAll() {
        logger.debug("Fetching all products");
        return repository.findAll();
    }

    public Page<Product> search(String name, String category, Double minPrice, Double maxPrice, Pageable pageable) {
        logger.debug("Searching products with filters - name: {}, category: {}, price range: {} to {}, page: {}, size: {}, sort: {}",
                name, category, minPrice, maxPrice, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        if (name != null) {
            return repository.findByProductNameContainingIgnoreCase(name, pageable);
        }
        if (category != null) {
            return repository.findByProductCategory(category, pageable);
        }
        if (minPrice != null && maxPrice != null) {
            return repository.findByProductPriceBetween(minPrice, maxPrice, pageable);
        }
        return repository.findAll(pageable);
    }

    public List<String> getAllCategories() {
        logger.debug("Fetching all product categories");
        return repository.findAllCategories();
    }

    private Integer generateId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }
}
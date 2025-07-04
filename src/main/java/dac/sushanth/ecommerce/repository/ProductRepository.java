package dac.sushanth.ecommerce.repository;

import dac.sushanth.ecommerce.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, Integer> {
    Page<Product> findByProductNameContainingIgnoreCase(String productName, Pageable pageable);
    
    Page<Product> findByProductCategory(String productCategory, Pageable pageable);
    
    @Query("{ 'productPrice' : { $gte: ?0, $lte: ?1 } }")
    Page<Product> findByProductPriceBetween(double min, double max, Pageable pageable);

    @Aggregation(pipeline = {
        "{ $group: { _id: '$productCategory' } }",
        "{ $project: { _id: 0, category: '$_id' } }",
        "{ $sort: { category: 1 } }"
    })
    List<String> findAllCategories();

    List<Product> findByProductNameContainingIgnoreCase(String productName);
    List<Product> findByProductCategory(String productCategory);
    List<Product> findByProductPriceBetween(double min, double max);
}
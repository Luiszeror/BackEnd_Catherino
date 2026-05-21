package DIMENSITY.SOFTWARE.Back_Catherino.repository;

import DIMENSITY.SOFTWARE.Back_Catherino.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    Optional<Product> findByCode(String code);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategory(String category);

    List<Product> findByActiveTrue();

    List<Product> findByStockGreaterThan(Integer stock);

    List<Product> findByStockLessThanEqual(Integer stock);

    boolean existsByCode(String code);
}
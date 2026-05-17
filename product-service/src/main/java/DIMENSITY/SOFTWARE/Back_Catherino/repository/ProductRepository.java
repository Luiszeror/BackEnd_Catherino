package DIMENSITY.SOFTWARE.Back_Catherino.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import DIMENSITY.SOFTWARE.Back_Catherino.model.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
}

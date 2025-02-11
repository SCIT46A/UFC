package app.scit46.ufc.repository;

import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;
import app.scit46.ufc.entity.CreatorEntity;

public interface CreatorRepository extends JpaRepository<CreatorEntity, BigInteger> {

}

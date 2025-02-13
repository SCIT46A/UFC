package app.scit46.ufc.repository;

import app.scit46.ufc.entity.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<SearchEntity, Long> {

    @Query("SELECT s FROM SearchEntity s WHERE LOWER(REPLACE(s.name, ' ', '')) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SearchEntity> searchByKeyword(@Param("keyword") String keyword);

}

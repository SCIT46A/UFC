package app.scit46.ufc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    ItemEntity findByName(String name);
    
}

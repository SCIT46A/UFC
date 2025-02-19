package app.scit46.ufc.repository.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.TagEntity;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
    
}

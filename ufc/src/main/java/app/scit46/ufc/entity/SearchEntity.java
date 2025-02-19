package app.scit46.ufc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import net.jcip.annotations.Immutable;

@Entity
@Immutable  // 🔹 VIEW는 읽기 전용 (변경 불가능)
@Table(name = "SearchView")
@Getter
public class SearchEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "created_by")
    private Long createdBy;
}
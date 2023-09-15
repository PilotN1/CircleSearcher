package searchengine.model;

import lombok.Data;

import jakarta.persistence.*;
import jakarta.persistence.Index;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = @Index(name = "page_id", columnList = "path", unique = true))
@Data
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "page_seq")
    private int id;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Site site;

    @Column(columnDefinition = "VARCHAR(200) NOT NULL")
    private String path;

    @Column(columnDefinition = "INTEGER NOT NULL")
    private int code;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @OneToMany(mappedBy = "page")
    private List<searchengine.model.Index> searchIndex = new ArrayList<>();
}

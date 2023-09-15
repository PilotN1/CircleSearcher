package searchengine.model;

import lombok.Data;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Lemma {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lemma_seq")
    private int id;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Site site;

    @Column(columnDefinition = "varchar(255) NOT NULL")
    private String lemma;

    @Column(columnDefinition = "INTEGER NOT NULL")
    private Integer frequency;

    @OneToMany(mappedBy = "lemma")
    private List<Index> searchIndex = new ArrayList<>();
}


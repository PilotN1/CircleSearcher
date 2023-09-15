package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "search_index")
@Getter
@Setter
public class Index {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "index_seq")
    private int id;

    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page page;

    @ManyToOne
    @JoinColumn(name = "lemma_id")
    private Lemma lemma;

    @Column(name = "lemmas_rank", columnDefinition = "FLOAT NOT NULL")
    private Float rank;
}

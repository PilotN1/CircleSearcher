package searchengine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_seq")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED') not null")
    private Status status;

    @Column(name = "status_time", columnDefinition = "datetime not null")
    private Date statusTime;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String url;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;


}

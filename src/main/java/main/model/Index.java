package main.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "lemma_index")
public class Index {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "page_id")
    private Page page;

    @OneToOne
    @JoinColumn(name = "lemma_id")
    private Lemma lemma;

    @Column(name = "lemma_rank")
    private float lemmaRank;
}

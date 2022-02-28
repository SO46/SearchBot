package ru.skillbox.diplom.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "lemma")
public class Lemma extends AbstractEntity {

    private String lemma;
    private int frequency;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "lemma")
    private List<Index> indexes;

    @Column(name = "site_id")
    private Long site_id;

}

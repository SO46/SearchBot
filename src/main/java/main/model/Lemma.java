package main.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "lemma")
public class Lemma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String lemma;
    private int frequency;
}

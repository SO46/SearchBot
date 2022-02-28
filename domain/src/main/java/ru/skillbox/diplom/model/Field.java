package ru.skillbox.diplom.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "field")
public class Field extends AbstractEntity {

    private String name;
    private String selector;
    private float weight;

}

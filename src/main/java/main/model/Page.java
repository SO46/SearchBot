package main.model;

import lombok.Data;

import javax.persistence.*;
import javax.persistence.Index;

@Data
@Entity
@Table(name = "page", indexes = @Index(columnList = "path"))
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String path;
    private int code;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

}

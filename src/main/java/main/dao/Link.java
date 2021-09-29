package main.dao;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "page")
public class Link{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String path;
    private int code;
    @Column(columnDefinition = "LONGTEXT")
    private String content;

}

package main.model;

import lombok.Data;

import javax.persistence.*;
import javax.persistence.Index;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return id == page.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

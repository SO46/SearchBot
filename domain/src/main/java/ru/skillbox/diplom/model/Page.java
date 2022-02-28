package ru.skillbox.diplom.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Index;

@Getter
@Setter
@Entity
@Table(name = "page", indexes = @Index(columnList = "path"))
public class Page extends AbstractEntity{

    private String path;
    private int code;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "site_id")
    private Long site_id;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return getId() == page.getId();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime * (int) (long) getId();
    }
}

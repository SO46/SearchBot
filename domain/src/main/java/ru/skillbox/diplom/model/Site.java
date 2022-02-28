package ru.skillbox.diplom.model;

import ru.skillbox.diplom.model.enums.SiteStatus;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "site")
public class Site extends AbstractEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    private SiteStatus status;

    @NotNull
    @Column(name = "status_time")
    private LocalDateTime statusTime;

    @Column(name = "last_error")
    private String lastError;

    private String url;
    private String name;

}

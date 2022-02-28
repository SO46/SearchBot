package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.diplom.model.Page;

import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Long> {
    Page findByPath(String url);
    Optional<Page> findById(Long id);
}

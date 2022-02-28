package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.diplom.model.Index;

import java.util.List;

public interface IndexRepository extends JpaRepository<Index, Long> {
    List<Index> findByLemmaId(Long id);
}

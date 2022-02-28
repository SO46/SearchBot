package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.diplom.model.Lemma;


public interface LemmaRepository extends JpaRepository<Lemma, Long> {
    Lemma findByLemma(String lemma);
}

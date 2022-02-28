package ru.skillbox.diplom.repository;

import ru.skillbox.diplom.model.Field;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldRepository extends JpaRepository<Field, Long> {
}

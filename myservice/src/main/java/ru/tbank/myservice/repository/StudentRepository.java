package ru.tbank.myservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.myservice.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
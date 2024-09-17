package com.app.urna.repository;

import com.app.urna.entity.Elector;
import com.app.urna.entity.enums.StatusElector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectorRepository extends JpaRepository<Elector, Long> {
    List<Elector> findByStatus(StatusElector status); //procura o eleitor de acordo com o stts
}

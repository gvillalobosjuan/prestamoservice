package com.guerrero.prestamoservice.repository;

import com.guerrero.prestamoservice.entity.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    List<Prestamo> findByUsuarioId(Long usuarioId);
    Optional<Prestamo> findByIdAndUsuarioId(Long id, Long usuarioId);
}
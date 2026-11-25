package com.guerrero.prestamoservice.service;

import com.guerrero.prestamoservice.dto.PrestamoRequest;
import com.guerrero.prestamoservice.dto.PrestamoResponse;
import com.guerrero.prestamoservice.entity.Prestamo;
import com.guerrero.prestamoservice.entity.Usuario;
import com.guerrero.prestamoservice.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;

    private Usuario getUsuarioAutenticado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public List<PrestamoResponse> listarMisPrestamos() {
        Usuario usuario = getUsuarioAutenticado();
        return prestamoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PrestamoResponse obtenerPorId(Long id) {
        Usuario usuario = getUsuarioAutenticado();
        Prestamo prestamo = prestamoRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado"));
        return mapToResponse(prestamo);
    }

    public PrestamoResponse registrar(PrestamoRequest request) {
        Usuario usuario = getUsuarioAutenticado();

        Prestamo prestamo = Prestamo.builder()
                .cliente(request.getCliente())
                .dni(request.getDni())
                .monto(request.getMonto())
                .interes(request.getInteres())
                .fechaPrestamo(LocalDate.now())
                .fechaVencimiento(request.getFechaVencimiento())
                .pagado(request.getPagado() != null ? request.getPagado() : false)
                .usuario(usuario)
                .build();

        return mapToResponse(prestamoRepository.save(prestamo));
    }

    public PrestamoResponse actualizar(Long id, PrestamoRequest request) {
        Usuario usuario = getUsuarioAutenticado();
        Prestamo prestamo = prestamoRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado"));

        prestamo.setCliente(request.getCliente());
        prestamo.setDni(request.getDni());
        prestamo.setMonto(request.getMonto());
        prestamo.setInteres(request.getInteres());
        prestamo.setFechaVencimiento(request.getFechaVencimiento());
        if (request.getPagado() != null) {
            prestamo.setPagado(request.getPagado());
        }

        return mapToResponse(prestamoRepository.save(prestamo));
    }

    public void eliminar(Long id) {
        Usuario usuario = getUsuarioAutenticado();
        Prestamo prestamo = prestamoRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado"));
        prestamoRepository.delete(prestamo);
    }

    private PrestamoResponse mapToResponse(Prestamo p) {
        BigDecimal ganancia = p.getMonto()
                .multiply(p.getInteres())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalCobrar = p.getMonto().add(ganancia);

        return PrestamoResponse.builder()
                .id(p.getId())
                .cliente(p.getCliente())
                .dni(p.getDni())
                .monto(p.getMonto())
                .interes(p.getInteres())
                .totalCobrar(totalCobrar)
                .fechaPrestamo(p.getFechaPrestamo())
                .fechaVencimiento(p.getFechaVencimiento())
                .pagado(p.getPagado())
                .build();
    }
}
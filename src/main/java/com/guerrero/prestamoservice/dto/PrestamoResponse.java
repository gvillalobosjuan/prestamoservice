package com.guerrero.prestamoservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PrestamoResponse {
    private Long id;
    private String cliente;
    private String dni;
    private BigDecimal monto;
    private BigDecimal interes;
    private BigDecimal totalCobrar;
    private LocalDate fechaPrestamo;
    private LocalDate fechaVencimiento;
    private Boolean pagado;
}
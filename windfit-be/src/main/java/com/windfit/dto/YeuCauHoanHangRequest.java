package com.windfit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YeuCauHoanHangRequest {
    @NotBlank(message = "Lý do hoàn hàng không được để trống")
    private String lyDoHoan;

    private BigDecimal tienHoan;

    private Long idNhanVien;
}

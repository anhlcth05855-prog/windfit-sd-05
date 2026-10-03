package com.windfit.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamChiTietRequestDTO {
    private Long id;
    @NotNull(message = "Mau sac khong duoc de trong")
    private Long idMauSac;
    @NotNull(message = "Kich co khong duoc de trong")
    private Long idKichCo;
    private String maSpct;
    private String maVach;
    @NotNull(message = "Gia ban khong duoc de trong")
    @DecimalMin(value = "0.0", inclusive = false, message = "Gia ban phai lon hon 0")
    private BigDecimal giaBan;
    @NotNull(message = "So luong ton khong duoc de trong")
    @Min(value = 0, message = "So luong ton khong duoc am")
    private Integer soLuongTon;
    @Builder.Default
    private Integer trangThai = 1;
}

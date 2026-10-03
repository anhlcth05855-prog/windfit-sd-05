package com.windfit.dto.discount;

import jakarta.validation.constraints.DecimalMax;
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
public class DotGiamGiaItemRequestDTO {
    @NotNull(message = "ID bien the khong duoc de trong")
    private Long idSanPhamChiTiet;

    @NotNull(message = "Phan tram giam khong duoc de trong")
    @DecimalMin(value = "1.0", message = "Phan tram giam toi thieu la 1%")
    @DecimalMax(value = "99.0", message = "Phan tram giam toi da la 99%")
    private BigDecimal phanTramGiam;

    @NotNull(message = "So luong khong duoc de trong")
    @Min(value = 1, message = "So luong toi thieu la 1")
    private Integer soLuong;
}

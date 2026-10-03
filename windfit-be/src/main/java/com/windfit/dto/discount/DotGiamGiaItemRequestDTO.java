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
    @NotNull(message = "ID biến thể không được để trống")
    private Long idSanPhamChiTiet;

    @NotNull(message = "Phần trăm giảm không được để trống")
    @DecimalMin(value = "0.01", message = "Phần trăm giảm tối thiểu là 0.01%")
    @DecimalMax(value = "100.0", message = "Phần trăm giảm tối đa là 100%")
    private BigDecimal phanTramGiam;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng tối thiểu là 1")
    private Integer soLuong;
}

package com.windfit.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuickUpdateVariantDTO {
    @DecimalMin(value = "0.0", inclusive = false, message = "Gia ban phai lon hon 0")
    private BigDecimal giaBan;
    @Min(value = 0, message = "So luong ton khong duoc am")
    private Integer soLuongTon;
    private Integer trangThai;
    private String maVach;
}

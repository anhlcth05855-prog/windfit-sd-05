package com.windfit.dto.discount;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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
public class ValidateVoucherRequest {
    @NotBlank(message = "Ma voucher khong duoc de trong")
    private String maVoucher;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal tongTienDonHang;

    private Long idKhachHang;
}

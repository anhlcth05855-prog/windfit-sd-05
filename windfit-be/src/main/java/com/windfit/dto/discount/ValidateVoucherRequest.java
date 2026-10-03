package com.windfit.dto.discount;

import jakarta.validation.constraints.DecimalMin;
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
    private String maVoucher;
    private String maPhieuGiamGia;

    @NotNull(message = "Tổng tiền đơn hàng không được để trống")
    @DecimalMin(value = "0.0", message = "Tổng tiền đơn hàng không được âm")
    private BigDecimal tongTienDonHang;

    private Long idKhachHang;
    private String maKhachHang;

    public String getEffectiveCode() {
        if (maVoucher != null && !maVoucher.trim().isEmpty()) {
            return maVoucher.trim();
        }
        if (maPhieuGiamGia != null && !maPhieuGiamGia.trim().isEmpty()) {
            return maPhieuGiamGia.trim();
        }
        return null;
    }
}

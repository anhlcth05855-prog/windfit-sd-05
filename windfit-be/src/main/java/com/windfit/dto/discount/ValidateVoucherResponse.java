package com.windfit.dto.discount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateVoucherResponse {
    private Boolean valid;
    private Long idVoucher;
    private String maVoucher;
    private String tenVoucher;
    private String loaiGiamGia;
    private BigDecimal giaTriGiam;
    private BigDecimal soTienGiam;
    private BigDecimal thanhTienSauGiam;
    private String message;
}

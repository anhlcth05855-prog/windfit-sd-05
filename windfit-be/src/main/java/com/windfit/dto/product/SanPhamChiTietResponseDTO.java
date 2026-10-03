package com.windfit.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamChiTietResponseDTO {
    private Long id;
    private Long idSanPham;
    private String tenSanPham;
    private String maSpct;
    private String maVach;
    private Long idMauSac;
    private String tenMauSac;
    private Long idKichCo;
    private String tenKichCo;
    private BigDecimal giaBan;
    private Integer soLuongTon;
    private Integer trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
}

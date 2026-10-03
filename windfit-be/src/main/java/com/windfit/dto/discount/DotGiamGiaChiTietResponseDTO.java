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
public class DotGiamGiaChiTietResponseDTO {
    private Long id;
    private Long idDotGiamGia;
    private Long idSanPhamChiTiet;
    private Long idSanPham;
    private String tenSanPham;
    private String maSpct;
    private String maVach;
    private String tenMauSac;
    private String tenKichCo;
    private BigDecimal giaGoc;
    private BigDecimal phanTramGiam;
    private BigDecimal giaSauGiam;
    private Integer soLuong;
    private Integer soLuongTonHienTai;
    private Integer trangThai;
}

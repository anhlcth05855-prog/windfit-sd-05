package com.windfit.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietHoaDonDTO {
    private Long id;
    private Long idSanPhamChiTiet;
    private String tenSanPham;
    private String maSku;
    private String tenMau;
    private String tenKichCo;
    private String urlAnh;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
}

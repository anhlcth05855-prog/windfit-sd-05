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
public class GiaHienTaiSpctResponseDTO {
    private Long idSanPhamChiTiet;
    private String maSpct;
    private BigDecimal giaGoc;
    private BigDecimal giaSauGiam;
    private BigDecimal phanTramGiam;
    private Boolean coGiamGia;
    private String tenDotGiamGia;
    private String maDotGiamGia;
    private String hinhThuc;
}

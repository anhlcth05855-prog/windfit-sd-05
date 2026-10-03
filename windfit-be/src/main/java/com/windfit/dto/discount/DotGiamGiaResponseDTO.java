package com.windfit.dto.discount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DotGiamGiaResponseDTO {
    private Long id;
    private String maDotGiamGia;
    private String tenDotGiamGia;
    private String moTa;
    private String dieuKienApDung;
    private String hinhThuc;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Integer trangThai;
    private String tinhTrang;
    private Integer soLuongSanPham;
    @Builder.Default
    private List<DotGiamGiaChiTietResponseDTO> danhSachChiTiet = new ArrayList<>();
}

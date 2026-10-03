package com.windfit.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamResponseDTO {
    private Long id;
    private String maSanPham;
    private String tenSanPham;
    private Long idDanhMuc;
    private String tenDanhMuc;
    private Long idThuongHieu;
    private String tenThuongHieu;
    private Long idChatLieu;
    private String tenChatLieu;
    private Long idXuatXu;
    private String tenXuatXu;
    private Long idKieuDang;
    private String tenKieuDang;
    private Long idMonTheThao;
    private String tenMonTheThao;
    private String doiTuong;
    private String tinhNang;
    private String moTaChiTiet;
    private String videoUrl;
    private Integer trangThai;
    private String anhChinh;
    @Builder.Default
    private List<HinhAnhSanPhamDTO> danhSachAnh = new ArrayList<>();
    @Builder.Default
    private List<SanPhamChiTietResponseDTO> danhSachBienThe = new ArrayList<>();
    private Integer tongSoLuongTon;
    private BigDecimal giaThapNhat;
    private BigDecimal giaCaoNhat;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
}

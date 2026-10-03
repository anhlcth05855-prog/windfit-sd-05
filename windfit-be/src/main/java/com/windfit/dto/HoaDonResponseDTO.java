package com.windfit.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonResponseDTO {
    private Long id;
    private String maHoaDon;
    private String loaiHoaDon;
    private String kenhBan;
    private Long idKhachHang;
    private String tenKhachHang;
    private String sdtKhachHang;
    private String tenNguoiNhan;
    private String sdtNguoiNhan;
    private String diaChiGiao;
    private Long idNhanVien;
    private String tenNhanVien;
    private String maPhieuGiamGia;
    private BigDecimal tongTien;
    private BigDecimal tienGiam;
    private BigDecimal phiShip;
    private BigDecimal thanhTien;
    private String ghiChu;
    private String lyDoHuy;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private LocalDateTime ngayNhanHang;
    private Integer trangThai;
    private String tenTrangThai;
    private String trangThaiHoaDon;
    private String trangThaiThanhToan;

    // Hoàn hàng
    private String lyDoHoan;
    private LocalDateTime ngayHoan;
    private BigDecimal tienHoan;

    private List<ChiTietHoaDonDTO> danhSachChiTiet;
    private List<ThanhToanHoaDonDTO> danhSachThanhToan;
    private List<LichSuHoaDonDTO> danhSachLichSu;
}

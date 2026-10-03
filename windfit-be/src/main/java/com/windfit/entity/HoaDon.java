package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hoa_don")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia")
    private PhieuGiamGia phieuGiamGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hinh_thuc_thanh_toan")
    private HinhThucThanhToan hinhThucThanhToan;

    @Column(name = "ma_hoa_don", length = 50, nullable = false, unique = true)
    private String maHoaDon;

    @Column(name = "loai_hoa_don", length = 50, nullable = false)
    private String loaiHoaDon; // TAI_QUAY, ONLINE

    @Column(name = "ten_nguoi_nhan", length = 150)
    private String tenNguoiNhan;

    @Column(name = "sdt_nguoi_nhan", length = 20)
    private String sdtNguoiNhan;

    @Column(name = "dia_chi_nhan", length = 500)
    private String diaChiNhan;

    @Column(name = "tong_tien_hang", precision = 18, scale = 2, nullable = false)
    private BigDecimal tongTienHang;

    @Column(name = "tien_giam", precision = 18, scale = 2, nullable = false)
    private BigDecimal tienGiam;

    @Column(name = "phi_ship", precision = 18, scale = 2, nullable = false)
    private BigDecimal phiShip;

    @Column(name = "tong_tien_thanh_toan", precision = 18, scale = 2, nullable = false)
    private BigDecimal tongTienThanhToan;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "ly_do_huy", length = 500)
    private String lyDoHuy;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_mong_muon_nhan")
    private LocalDateTime ngayMongMuonNhan;

    @Column(name = "ngay_giao_hang")
    private LocalDateTime ngayGiaoHang;

    @Column(name = "ngay_nhan_hang")
    private LocalDateTime ngayNhanHang;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "ngay_cap_nhat", nullable = false)
    private LocalDateTime ngayCapNhat;

    @Column(name = "trang_thai_hoa_don", length = 50, nullable = false)
    private String trangThaiHoaDon; // CHO_XAC_NHAN, DANG_GIAO, HOAN_THANH, DA_HUY, YEU_CAU_HOAN, DA_HOAN

    @Column(name = "trang_thai_thanh_toan", length = 50, nullable = false)
    private String trangThaiThanhToan; // CHUA_THANH_TOAN, DA_THANH_TOAN

    @Column(name = "ly_do_hoan", length = 500)
    private String lyDoHoan;

    @Column(name = "ngay_hoan")
    private LocalDateTime ngayHoan;

    @Column(name = "tien_hoan", precision = 18, scale = 2)
    private BigDecimal tienHoan;
}

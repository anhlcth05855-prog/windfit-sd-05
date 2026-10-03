package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "phieu_giam_gia")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PhieuGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_phieu_giam_gia", length = 50, nullable = false, unique = true)
    private String maPhieuGiamGia;

    @Column(name = "ten_phieu_giam_gia", length = 200, nullable = false)
    private String tenPhieuGiamGia;

    @Column(name = "loai_giam_gia", length = 50, nullable = false)
    private String loaiGiamGia;

    @Column(name = "gia_tri_giam", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaTriGiam;

    @Column(name = "giam_toi_da", precision = 18, scale = 2)
    private BigDecimal giamToiDa;

    @Column(name = "don_toi_thieu", precision = 18, scale = 2, nullable = false)
    private BigDecimal donToiThieu;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "so_luong_da_dung", nullable = false)
    private Integer soLuongDaDung;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "dot_giam_gia")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DotGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_dot_giam_gia", length = 50, nullable = false, unique = true)
    private String maDotGiamGia;

    @Column(name = "ten_dot_giam_gia", length = 200, nullable = false)
    private String tenDotGiamGia;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "dieu_kien_ap_dung", length = 500)
    private String dieuKienApDung;

    @Column(name = "hinh_thuc", length = 50)
    private String hinhThuc; // 'THEO_MUA' hoặc 'FLASH_SALE'

    @Column(name = "gio_bat_dau")
    private LocalTime gioBatDau;

    @Column(name = "gio_ket_thuc")
    private LocalTime gioKetThuc;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

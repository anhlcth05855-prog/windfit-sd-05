package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "san_pham_chi_tiet")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SanPhamChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham", nullable = false)
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mau_sac", nullable = false)
    private MauSac mauSac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kich_co", nullable = false)
    private KichCo kichCo;

    @Column(name = "ma_spct", length = 100, nullable = false, unique = true)
    private String maSpct;

    @Column(name = "ma_vach", length = 100)
    private String maVach;

    @Column(name = "gia_ban", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaBan;

    @Column(name = "so_luong_ton", nullable = false)
    private Integer soLuongTon;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_sua")
    private LocalDateTime ngaySua;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

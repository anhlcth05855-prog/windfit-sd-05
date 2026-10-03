package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "dot_giam_gia_chi_tiet")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DotGiamGiaChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dot_giam_gia", nullable = false)
    private DotGiamGia dotGiamGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham_chi_tiet", nullable = false)
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "phan_tram_giam", precision = 5, scale = 2, nullable = false)
    private BigDecimal phanTramGiam;

    @Column(name = "gia_sau_giam", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaSauGiam;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

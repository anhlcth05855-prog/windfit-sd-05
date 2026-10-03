package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hinh_anh_san_pham")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HinhAnhSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham", nullable = false)
    private SanPham sanPham;

    @Column(name = "url_anh", length = 500, nullable = false)
    private String urlAnh;

    @Column(name = "is_anh_chinh", nullable = false)
    private Boolean isAnhChinh;
}

package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kieu_dang")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KieuDang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_kieu_dang", length = 50, nullable = false, unique = true)
    private String maKieuDang;

    @Column(name = "ten_kieu_dang", length = 150, nullable = false)
    private String tenKieuDang;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

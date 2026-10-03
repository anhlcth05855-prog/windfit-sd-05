package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hinh_thuc_thanh_toan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HinhThucThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_hinh_thuc", length = 50, nullable = false, unique = true)
    private String maHinhThuc;

    @Column(name = "ten_hinh_thuc", length = 100, nullable = false)
    private String tenHinhThuc;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

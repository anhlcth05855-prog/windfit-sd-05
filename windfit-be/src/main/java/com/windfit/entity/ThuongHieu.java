package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "thuong_hieu")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ThuongHieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_thuong_hieu", length = 50, nullable = false, unique = true)
    private String maThuongHieu;

    @Column(name = "ten_thuong_hieu", length = 150, nullable = false)
    private String tenThuongHieu;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phuong_thuc_thanh_toan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PhuongThucThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_phuong_thuc", length = 50, nullable = false, unique = true)
    private String maPhuongThuc;

    @Column(name = "ten_phuong_thuc", length = 100, nullable = false)
    private String tenPhuongThuc;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

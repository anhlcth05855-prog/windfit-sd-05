package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "xuat_xu")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class XuatXu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_xuat_xu", length = 50, nullable = false, unique = true)
    private String maXuatXu;

    @Column(name = "ten_xuat_xu", length = 150, nullable = false)
    private String tenXuatXu;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

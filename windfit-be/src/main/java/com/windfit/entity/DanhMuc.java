package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "danh_muc")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_danh_muc", length = 50, nullable = false, unique = true)
    private String maDanhMuc;

    @Column(name = "ten_danh_muc", length = 150, nullable = false)
    private String tenDanhMuc;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

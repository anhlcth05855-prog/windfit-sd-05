package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vai_tro")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VaiTro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_vai_tro", length = 50, nullable = false, unique = true)
    private String maVaiTro;

    @Column(name = "ten_vai_tro", length = 100, nullable = false)
    private String tenVaiTro;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

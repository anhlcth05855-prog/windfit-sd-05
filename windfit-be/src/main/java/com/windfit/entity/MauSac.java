package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mau_sac")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MauSac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_mau", length = 50, nullable = false, unique = true)
    private String maMau;

    @Column(name = "ten_mau", length = 100, nullable = false)
    private String tenMau;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

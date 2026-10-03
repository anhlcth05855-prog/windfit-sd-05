package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mon_the_thao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MonTheThao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_mon_the_thao", length = 50, nullable = false, unique = true)
    private String maMonTheThao;

    @Column(name = "ten_mon_the_thao", length = 150, nullable = false)
    private String tenMonTheThao;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kich_co")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KichCo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_kich_co", length = 50, nullable = false, unique = true)
    private String maKichCo;

    @Column(name = "ten_kich_co", length = 50, nullable = false)
    private String tenKichCo;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

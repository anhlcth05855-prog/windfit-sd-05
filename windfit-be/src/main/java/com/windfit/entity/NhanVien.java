package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhan_vien")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vai_tro", nullable = false)
    private VaiTro vaiTro;

    @Column(name = "ma_nhan_vien", length = 50, nullable = false, unique = true)
    private String maNhanVien;

    @Column(name = "ho_ten", length = 150, nullable = false)
    private String hoTen;

    @Column(name = "email", length = 150, unique = true)
    private String email;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "mat_khau", length = 255, nullable = false)
    private String matKhau;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @Column(name = "dia_chi", length = 255)
    private String diaChi;

    @Column(name = "anh_dai_dien", length = 500)
    private String anhDaiDien;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}

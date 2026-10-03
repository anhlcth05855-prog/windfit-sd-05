package com.windfit.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamCreateRequestDTO {
    private String maSanPham;
    @NotBlank(message = "Ten san pham khong duoc de trong")
    private String tenSanPham;
    @NotNull(message = "Danh muc khong duoc de trong")
    private Long idDanhMuc;
    @NotNull(message = "Thuong hieu khong duoc de trong")
    private Long idThuongHieu;
    @NotNull(message = "Chat lieu khong duoc de trong")
    private Long idChatLieu;
    @NotNull(message = "Xuat xu khong duoc de trong")
    private Long idXuatXu;
    @NotNull(message = "Kieu dang khong duoc de trong")
    private Long idKieuDang;
    @NotNull(message = "Mon the thao khong duoc de trong")
    private Long idMonTheThao;
    private String doiTuong;
    private String tinhNang;
    private String moTaChiTiet;
    private String videoUrl;
    @Builder.Default
    private Integer trangThai = 1;
    private String anhChinhUrl;
    @Builder.Default
    private List<String> danhSachAnh = new ArrayList<>();
    @NotEmpty(message = "San pham phai co it nhat mot bien the")
    @Valid
    @Builder.Default
    private List<SanPhamChiTietRequestDTO> bienTheList = new ArrayList<>();
}

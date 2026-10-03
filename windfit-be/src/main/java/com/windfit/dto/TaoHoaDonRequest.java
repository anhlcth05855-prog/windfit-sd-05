package com.windfit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaoHoaDonRequest {
    private Long idKhachHang;
    private Long idNhanVien;
    private Long idPhieuGiamGia;

    @NotBlank(message = "Kênh bán không được để trống")
    private String kenhBan; // TAI_QUAY, ONLINE

    private String tenNguoiNhan;
    private String sdtNguoiNhan;
    private String diaChiGiao;
    private String ghiChu;
    private String hinhThucThanhToan; // TIEN_MAT, CHUYEN_KHOAN, VIETQR, VNPAY

    @NotEmpty(message = "Danh sách sản phẩm không được rỗng")
    private List<ItemRequest> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemRequest {
        private Long idSanPhamChiTiet;
        private Integer soLuong;
        private BigDecimal donGia;
    }
}

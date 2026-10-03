package com.windfit.dto.discount;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DotGiamGiaRequestDTO {
    private String maDotGiamGia;

    @NotBlank(message = "Ten dot giam gia khong duoc de trong")
    private String tenDotGiamGia;

    private String moTa;
    private String dieuKienApDung;

    @NotNull(message = "Ngay bat dau khong duoc de trong")
    private LocalDateTime ngayBatDau;

    @NotNull(message = "Ngay ket thuc khong duoc de trong")
    private LocalDateTime ngayKetThuc;

    @Builder.Default
    private Integer trangThai = 1;

    @NotEmpty(message = "Dot giam gia phai co it nhat mot san pham")
    @Valid
    @Builder.Default
    private List<DotGiamGiaItemRequestDTO> danhSachChiTiet = new ArrayList<>();
}

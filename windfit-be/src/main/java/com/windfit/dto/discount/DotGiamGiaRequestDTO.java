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

    @NotBlank(message = "Tên đợt giảm giá không được để trống")
    private String tenDotGiamGia;

    private String moTa;
    private String dieuKienApDung;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime ngayKetThuc;

    @Builder.Default
    private Integer trangThai = 1;

    @NotEmpty(message = "Đợt giảm giá phải chọn ít nhất một sản phẩm biến thể")
    @Valid
    @Builder.Default
    private List<DotGiamGiaItemRequestDTO> danhSachChiTiet = new ArrayList<>();
}

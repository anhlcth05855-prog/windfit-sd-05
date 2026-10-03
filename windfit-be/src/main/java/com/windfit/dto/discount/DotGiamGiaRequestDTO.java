package com.windfit.dto.discount;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Builder.Default
    private String hinhThuc = "THEO_MUA"; // 'THEO_MUA' hoặc 'FLASH_SALE'

    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime ngayKetThuc;

    @Builder.Default
    private Integer trangThai = 1;

    @Valid
    @Builder.Default
    private List<DotGiamGiaItemRequestDTO> danhSachChiTiet = new ArrayList<>();
}

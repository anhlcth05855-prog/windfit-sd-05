package com.windfit.dto.discount;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuGiamGiaRequestDTO {
    private String maPhieuGiamGia;

    @NotBlank(message = "Ten phieu giam gia khong duoc de trong")
    private String tenPhieuGiamGia;

    @NotBlank(message = "Loai giam gia khong duoc de trong")
    private String loaiGiamGia;

    @NotNull(message = "Gia tri giam khong duoc de trong")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal giaTriGiam;

    private BigDecimal giamToiDa;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal donToiThieu;

    @NotNull
    @Min(value = 1)
    private Integer soLuong;

    @NotNull
    private LocalDateTime ngayBatDau;

    @NotNull
    private LocalDateTime ngayKetThuc;

    @Builder.Default
    private Integer trangThai = 1;
}

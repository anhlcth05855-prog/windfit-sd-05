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
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuGiamGiaRequestDTO {
    private String maPhieuGiamGia;

    @NotBlank(message = "Tên phiếu giảm giá không được để trống")
    private String tenPhieuGiamGia;

    @NotBlank(message = "Loại giảm giá không được để trống")
    private String loaiGiamGia;

    @NotNull(message = "Giá trị giảm không được để trống")
    @DecimalMin(value = "0.01", message = "Giá trị giảm phải lớn hơn 0")
    private BigDecimal giaTriGiam;

    private BigDecimal giamToiDa;

    @NotNull(message = "Đơn hàng tối thiểu không được để trống")
    @DecimalMin(value = "0.0", message = "Đơn tối thiểu không được âm")
    private BigDecimal donToiThieu;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng tối thiểu là 1")
    private Integer soLuong;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime ngayKetThuc;

    @Builder.Default
    private Integer trangThai = 1;

    // Danh sách id khách hàng được gán voucher riêng tư (tùy chọn)
    @Builder.Default
    private List<Long> customerIds = new ArrayList<>();
}

package com.windfit.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThanhToanHoaDonDTO {
    private Long id;
    private String hinhThuc;
    private BigDecimal soTien;
    private String maGiaoDich;
    private LocalDateTime ngayThanhToan;
    private Integer trangThai;
}

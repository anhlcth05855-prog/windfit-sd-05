package com.windfit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichSuHoaDonDTO {
    private Long id;
    private Long idHoaDon;
    private Long idNhanVien;
    private String tenNhanVien;
    private String hanhDong;
    private String ghiChu;
    private LocalDateTime ngayTao;
}

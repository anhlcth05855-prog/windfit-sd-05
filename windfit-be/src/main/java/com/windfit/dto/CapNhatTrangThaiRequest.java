package com.windfit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CapNhatTrangThaiRequest {
    @NotNull(message = "Trạng thái mới không được để trống")
    private Integer trangThaiMoi;
    private String ghiChu;
    private Long idNhanVienThaoTac;
}

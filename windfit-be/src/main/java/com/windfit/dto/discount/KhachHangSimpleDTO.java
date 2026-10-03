package com.windfit.dto.discount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHangSimpleDTO {
    private Long id;
    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private Integer trangThai;
}

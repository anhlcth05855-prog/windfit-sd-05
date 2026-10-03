package com.windfit.dto.product;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThuocTinhDTO {
    private Long id;
    @NotBlank(message = "Ma thuoc tinh khong duoc de trong")
    private String ma;
    @NotBlank(message = "Ten thuoc tinh khong duoc de trong")
    private String ten;
    private Integer trangThai;
    private String loaiThuocTinh;

    public String getLabel() {
        return this.ten;
    }
}

package com.windfit.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HinhAnhSanPhamDTO {
    private Long id;
    private String urlAnh;
    private Boolean isAnhChinh;
}

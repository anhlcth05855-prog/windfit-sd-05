package com.windfit.dto.discount;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignSkuRequest {
    @NotEmpty(message = "Danh sách sản phẩm chi tiết không được để trống")
    @Valid
    @Builder.Default
    private List<DotGiamGiaItemRequestDTO> skus = new ArrayList<>();
}

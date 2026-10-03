package com.windfit.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamFilterRequest {
    private String keyword;
    private Long idDanhMuc;
    private Long idThuongHieu;
    private Long idChatLieu;
    private Long idXuatXu;
    private Long idKieuDang;
    private Long idMonTheThao;
    private Integer trangThai;
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 10;
    @Builder.Default
    private String sortBy = "id";
    @Builder.Default
    private String sortDir = "desc";
}

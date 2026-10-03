package com.windfit.dto.discount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThongKeGiamGiaDTO {
    private long tongSo;
    private long dangDienRa;
    private long sapDienRa;
    private long daKetThuc;
    private long ngungApDung;
}

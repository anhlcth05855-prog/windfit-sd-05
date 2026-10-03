package com.windfit.controller;

import com.windfit.dto.*;
import com.windfit.service.HoaDonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hoa-don")
@RequiredArgsConstructor
public class HoaDonController {

    private final HoaDonService hoaDonService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<HoaDonResponseDTO>>> layDanhSach(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) String kenhBan,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime denNgay,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<HoaDonResponseDTO> result = hoaDonService.layDanhSachHoaDon(
                keyword, trangThai, kenhBan, tuNgay, denNgay, PageRequest.of(page, size)
        );
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HoaDonResponseDTO>> layChiTiet(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(hoaDonService.layChiTietHoaDon(id)));
    }

    @GetMapping("/ma/{maHoaDon}")
    public ResponseEntity<ApiResponse<HoaDonResponseDTO>> layTheoMa(@PathVariable String maHoaDon) {
        return ResponseEntity.ok(ApiResponse.ok(hoaDonService.layChiTietTheoMa(maHoaDon)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HoaDonResponseDTO>> taoHoaDon(@Valid @RequestBody TaoHoaDonRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Tạo hóa đơn thành công", hoaDonService.taoHoaDon(request)));
    }

    @PutMapping("/{id}/trang-thai")
    public ResponseEntity<ApiResponse<HoaDonResponseDTO>> capNhatTrangThai(
            @PathVariable Long id,
            @Valid @RequestBody CapNhatTrangThaiRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật trạng thái thành công", hoaDonService.capNhatTrangThai(id, request)));
    }

    @PutMapping("/{id}/huy")
    public ResponseEntity<ApiResponse<Void>> huyHoaDon(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Khách hàng yêu cầu hủy") String lyDoHuy
    ) {
        hoaDonService.huyHoaDon(id, lyDoHuy);
        return ResponseEntity.ok(ApiResponse.ok("Hủy hóa đơn thành công", null));
    }

    @GetMapping("/cho-tai-quay")
    public ResponseEntity<ApiResponse<List<HoaDonResponseDTO>>> layHoaDonChoTaiQuay() {
        return ResponseEntity.ok(ApiResponse.ok(hoaDonService.layHoaDonChoTaiQuay()));
    }

    @PostMapping("/{id}/yeu-cau-hoan")
    public ResponseEntity<ApiResponse<HoaDonResponseDTO>> yeuCauHoanHang(
            @PathVariable Long id,
            @Valid @RequestBody YeuCauHoanHangRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Yêu cầu hoàn hàng đã được gửi", hoaDonService.yeuCauHoanHang(id, request)));
    }

    @PostMapping("/{id}/xac-nhan-hoan")
    public ResponseEntity<ApiResponse<HoaDonResponseDTO>> xacNhanHoanHang(
            @PathVariable Long id,
            @RequestParam(required = false) Long idNhanVien
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Xác nhận hoàn hàng thành công, tồn kho đã được hoàn lại", hoaDonService.xacNhanHoanHang(id, idNhanVien)));
    }

    @PostMapping("/{id}/tu-choi-hoan")
    public ResponseEntity<ApiResponse<HoaDonResponseDTO>> tuChoiHoanHang(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Không đáp ứng chính sách đổi trả WindFit") String lyDoTuChoi,
            @RequestParam(required = false) Long idNhanVien
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Đã từ chối hoàn hàng, đơn về trạng thái Hoàn thành", hoaDonService.tuChoiHoanHang(id, lyDoTuChoi, idNhanVien)));
    }
}

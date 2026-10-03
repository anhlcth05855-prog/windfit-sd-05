package com.windfit.controller;

import com.windfit.dto.ApiResponse;
import com.windfit.dto.discount.DotGiamGiaRequestDTO;
import com.windfit.dto.discount.DotGiamGiaResponseDTO;
import com.windfit.dto.discount.KhachHangSimpleDTO;
import com.windfit.dto.discount.PhieuGiamGiaRequestDTO;
import com.windfit.dto.discount.PhieuGiamGiaResponseDTO;
import com.windfit.dto.discount.ValidateVoucherRequest;
import com.windfit.dto.discount.ValidateVoucherResponse;
import com.windfit.dto.product.SanPhamChiTietResponseDTO;
import com.windfit.service.GiamGiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/phieu-giam-gia")
@RequiredArgsConstructor
public class PhieuGiamGiaController {

    private final GiamGiaService giamGiaService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PhieuGiamGiaResponseDTO>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String loaiGiamGia,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime denNgay,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PhieuGiamGiaResponseDTO> result = giamGiaService.getPhieuGiamGiaList(keyword, loaiGiamGia, trangThai, tuNgay, denNgay, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách phiếu giảm giá thành công", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PhieuGiamGiaResponseDTO>> getById(@PathVariable Long id) {
        PhieuGiamGiaResponseDTO result = giamGiaService.getPhieuGiamGiaById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết phiếu giảm giá thành công", result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PhieuGiamGiaResponseDTO>> create(@Valid @RequestBody PhieuGiamGiaRequestDTO request) {
        PhieuGiamGiaResponseDTO result = giamGiaService.createPhieuGiamGia(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo mới phiếu giảm giá thành công", result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PhieuGiamGiaResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody PhieuGiamGiaRequestDTO request
    ) {
        PhieuGiamGiaResponseDTO result = giamGiaService.updatePhieuGiamGia(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật phiếu giảm giá thành công", result));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(@PathVariable Long id) {
        giamGiaService.toggleStatusPhieuGiamGia(id);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái phiếu giảm giá thành công", null));
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<ValidateVoucherResponse>> validate(@Valid @RequestBody ValidateVoucherRequest request) {
        ValidateVoucherResponse result = giamGiaService.validateVoucher(request);
        return ResponseEntity.ok(ApiResponse.success("Kiểm tra voucher thành công", result));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<PhieuGiamGiaResponseDTO>>> getAvailable() {
        List<PhieuGiamGiaResponseDTO> result = giamGiaService.getAvailableVouchers();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách voucher khả dụng thành công", result));
    }

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<Page<KhachHangSimpleDTO>>> getCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<KhachHangSimpleDTO> result = giamGiaService.searchKhachHang(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khách hàng thành công", result));
    }

    @GetMapping("/{id}/assigned-customers")
    public ResponseEntity<ApiResponse<List<Long>>> getAssignedCustomers(@PathVariable Long id) {
        List<Long> result = giamGiaService.getAssignedCustomerIds(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khách hàng đã gán thành công", result));
    }

    @PostMapping("/{id}/assign-customers")
    public ResponseEntity<ApiResponse<Void>> assignCustomers(
            @PathVariable Long id,
            @RequestBody List<Long> customerIds
    ) {
        giamGiaService.assignCustomersToVoucher(id, customerIds);
        return ResponseEntity.ok(ApiResponse.success("Gán khách hàng cho voucher thành công", null));
    }
}

@RestController
@RequestMapping("/api/v1/dot-giam-gia")
@RequiredArgsConstructor
class DotGiamGiaController {

    private final GiamGiaService giamGiaService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DotGiamGiaResponseDTO>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime denNgay,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<DotGiamGiaResponseDTO> result = giamGiaService.getDotGiamGiaList(keyword, trangThai, tuNgay, denNgay, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách đợt giảm giá thành công", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DotGiamGiaResponseDTO>> getById(@PathVariable Long id) {
        DotGiamGiaResponseDTO result = giamGiaService.getDotGiamGiaById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết đợt giảm giá thành công", result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DotGiamGiaResponseDTO>> create(@Valid @RequestBody DotGiamGiaRequestDTO request) {
        DotGiamGiaResponseDTO result = giamGiaService.createDotGiamGia(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo mới đợt giảm giá thành công", result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DotGiamGiaResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody DotGiamGiaRequestDTO request
    ) {
        DotGiamGiaResponseDTO result = giamGiaService.updateDotGiamGia(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật đợt giảm giá thành công", result));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(@PathVariable Long id) {
        giamGiaService.toggleStatusDotGiamGia(id);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái đợt giảm giá thành công", null));
    }

    @GetMapping("/san-pham-chi-tiet")
    public ResponseEntity<ApiResponse<Page<SanPhamChiTietResponseDTO>>> getSanPhamChiTiet(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long idSanPham,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SanPhamChiTietResponseDTO> result = giamGiaService.searchSanPhamChiTiet(keyword, idSanPham, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách biến thể sản phẩm thành công", result));
    }
}

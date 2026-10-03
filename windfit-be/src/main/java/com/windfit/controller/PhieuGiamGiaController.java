package com.windfit.controller;

import com.windfit.dto.ApiResponse;
import com.windfit.dto.discount.*;
import com.windfit.service.GiamGiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PhieuGiamGiaResponseDTO> result = giamGiaService.getPhieuGiamGiaList(keyword, loaiGiamGia, trangThai, page, size);
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<DotGiamGiaResponseDTO> result = giamGiaService.getDotGiamGiaList(keyword, trangThai, page, size);
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
}

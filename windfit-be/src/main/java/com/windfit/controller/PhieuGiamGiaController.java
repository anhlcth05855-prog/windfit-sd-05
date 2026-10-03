package com.windfit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.windfit.dto.ApiResponse;
import com.windfit.dto.PageResponse;
import com.windfit.dto.discount.*;
import com.windfit.service.GiamGiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/phieu-giam-gia")
@RequiredArgsConstructor
public class PhieuGiamGiaController {

    private final GiamGiaService giamGiaService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PhieuGiamGiaResponseDTO>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String loaiGiam,
            @RequestParam(required = false) String loaiGiamGia,
            @RequestParam(required = false) String nhomPhieu,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime denNgay,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    ) {
        String effectiveLoaiGiam = (loaiGiam != null && !loaiGiam.trim().isEmpty()) ? loaiGiam : loaiGiamGia;
        PageResponse<PhieuGiamGiaResponseDTO> result = giamGiaService.getPhieuGiamGiaList(
                keyword, effectiveLoaiGiam, nhomPhieu, trangThai, tuNgay, denNgay, page, size, sort
        );
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

    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<ApiResponse<Void>> changeStatus(@PathVariable Long id) {
        giamGiaService.toggleStatusPhieuGiamGia(id);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái phiếu giảm giá thành công", null));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(@PathVariable Long id) {
        return changeStatus(id);
    }

    @GetMapping("/thong-ke")
    public ResponseEntity<ApiResponse<ThongKeGiamGiaDTO>> getStatistics() {
        ThongKeGiamGiaDTO result = giamGiaService.getThongKePhieuGiamGia();
        return ResponseEntity.ok(ApiResponse.success("Lấy thống kê phiếu giảm giá thành công", result));
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<ValidateVoucherResponse>> validate(@Valid @RequestBody ValidateVoucherRequest request) {
        ValidateVoucherResponse result = giamGiaService.validateVoucher(request);
        return ResponseEntity.ok(ApiResponse.success(result.getMessage(), result));
    }

    @PostMapping("/ap-ma")
    public ResponseEntity<ApiResponse<ValidateVoucherResponse>> applyCode(@Valid @RequestBody ValidateVoucherRequest request) {
        return validate(request);
    }

    @PostMapping("/best-voucher")
    public ResponseEntity<ApiResponse<ValidateVoucherResponse>> findBestVoucher(
            @RequestParam(required = false) Long idKhachHang,
            @RequestParam BigDecimal tongTienDonHang
    ) {
        ValidateVoucherResponse result = giamGiaService.findBestVoucher(idKhachHang, tongTienDonHang);
        return ResponseEntity.ok(ApiResponse.success(result.getMessage(), result));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<PhieuGiamGiaResponseDTO>>> getAvailable() {
        List<PhieuGiamGiaResponseDTO> result = giamGiaService.getAvailableVouchers();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách voucher khả dụng thành công", result));
    }

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<PageResponse<KhachHangSimpleDTO>>> getCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<KhachHangSimpleDTO> result = giamGiaService.searchKhachHang(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khách hàng thành công", result));
    }

    @GetMapping("/{id}/assigned-customers")
    public ResponseEntity<ApiResponse<PageResponse<KhachHangSimpleDTO>>> getAssignedCustomers(
            @PathVariable Long id,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<KhachHangSimpleDTO> result = giamGiaService.getAssignedCustomersPaged(id, keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khách hàng đã gán thành công", result));
    }

    @PostMapping("/{id}/assign-customers")
    public ResponseEntity<ApiResponse<Void>> assignCustomers(
            @PathVariable Long id,
            @RequestBody JsonNode node
    ) {
        List<Long> customerIds = extractIdList(node, "customerIds");
        giamGiaService.assignCustomersToVoucher(id, customerIds);
        return ResponseEntity.ok(ApiResponse.success("Gán khách hàng cho voucher thành công", null));
    }

    @PatchMapping("/{id}/unassign-customers")
    public ResponseEntity<ApiResponse<Void>> unassignCustomers(
            @PathVariable Long id,
            @RequestBody JsonNode node
    ) {
        List<Long> customerIds = extractIdList(node, "customerIds");
        giamGiaService.unassignCustomersFromVoucher(id, customerIds);
        return ResponseEntity.ok(ApiResponse.success("Gỡ khách hàng khỏi voucher thành công", null));
    }

    private List<Long> extractIdList(JsonNode node, String fieldName) {
        List<Long> ids = new ArrayList<>();
        if (node == null) return ids;
        if (node.isArray()) {
            node.forEach(item -> ids.add(item.asLong()));
        } else if (node.has(fieldName) && node.get(fieldName).isArray()) {
            node.get(fieldName).forEach(item -> ids.add(item.asLong()));
        } else if (node.isNumber()) {
            ids.add(node.asLong());
        }
        return ids;
    }
}

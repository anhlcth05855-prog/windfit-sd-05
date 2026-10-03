package com.windfit.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.windfit.dto.ApiResponse;
import com.windfit.dto.PageResponse;
import com.windfit.dto.discount.*;
import com.windfit.dto.product.SanPhamChiTietResponseDTO;
import com.windfit.service.GiamGiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dot-giam-gia")
@RequiredArgsConstructor
public class DotGiamGiaController {

    private final GiamGiaService giamGiaService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DotGiamGiaResponseDTO>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String hinhThuc,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime denNgay,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    ) {
        PageResponse<DotGiamGiaResponseDTO> result = giamGiaService.getDotGiamGiaList(
                keyword, hinhThuc, trangThai, tuNgay, denNgay, page, size, sort
        );
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

    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<ApiResponse<Void>> changeStatus(@PathVariable Long id) {
        giamGiaService.toggleStatusDotGiamGia(id);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái đợt giảm giá thành công", null));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(@PathVariable Long id) {
        return changeStatus(id);
    }

    @GetMapping("/thong-ke")
    public ResponseEntity<ApiResponse<ThongKeGiamGiaDTO>> getStatistics() {
        ThongKeGiamGiaDTO result = giamGiaService.getThongKeDotGiamGia();
        return ResponseEntity.ok(ApiResponse.success("Lấy thống kê đợt giảm giá thành công", result));
    }

    @GetMapping("/{id}/skus")
    public ResponseEntity<ApiResponse<PageResponse<DotGiamGiaChiTietResponseDTO>>> getSkusOfCampaign(
            @PathVariable Long id,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<DotGiamGiaChiTietResponseDTO> result = giamGiaService.getSkusOfCampaign(id, keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách SKU của đợt giảm giá thành công", result));
    }

    @PostMapping("/{id}/skus")
    public ResponseEntity<ApiResponse<Void>> assignSkus(
            @PathVariable Long id,
            @RequestBody JsonNode node
    ) {
        List<DotGiamGiaItemRequestDTO> items = new ArrayList<>();
        try {
            if (node.isArray()) {
                items = objectMapper.readerFor(new TypeReference<List<DotGiamGiaItemRequestDTO>>() {}).readValue(node);
            } else if (node.has("skus") && node.get("skus").isArray()) {
                items = objectMapper.readerFor(new TypeReference<List<DotGiamGiaItemRequestDTO>>() {}).readValue(node.get("skus"));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Dữ liệu danh sách SKU không hợp lệ: " + e.getMessage());
        }
        giamGiaService.assignSkusToCampaign(id, items);
        return ResponseEntity.ok(ApiResponse.success("Gắn SKU vào đợt giảm giá thành công", null));
    }

    @PatchMapping("/{id}/skus/remove")
    public ResponseEntity<ApiResponse<Void>> removeSkus(
            @PathVariable Long id,
            @RequestBody JsonNode node
    ) {
        List<Long> ids = new ArrayList<>();
        if (node.isArray()) {
            node.forEach(item -> ids.add(item.asLong()));
        } else if (node.has("idSanPhamChiTiets") && node.get("idSanPhamChiTiets").isArray()) {
            node.get("idSanPhamChiTiets").forEach(item -> ids.add(item.asLong()));
        } else if (node.isNumber()) {
            ids.add(node.asLong());
        }
        giamGiaService.removeSkusFromCampaign(id, ids);
        return ResponseEntity.ok(ApiResponse.success("Gỡ SKU khỏi đợt giảm giá thành công", null));
    }

    @GetMapping("/gia-hien-tai/{spctId}")
    public ResponseEntity<ApiResponse<GiaHienTaiSpctResponseDTO>> getCurrentPrice(@PathVariable Long spctId) {
        GiaHienTaiSpctResponseDTO result = giamGiaService.getGiaHienTaiSpct(spctId);
        return ResponseEntity.ok(ApiResponse.success("Lấy giá hiện tại của sản phẩm chi tiết thành công", result));
    }

    @PostMapping("/gia-hien-tai/batch")
    public ResponseEntity<ApiResponse<Map<Long, GiaHienTaiSpctResponseDTO>>> getCurrentPricesBatch(@RequestBody List<Long> spctIds) {
        Map<Long, GiaHienTaiSpctResponseDTO> result = giamGiaService.getGiaHienTaiBatch(spctIds);
        return ResponseEntity.ok(ApiResponse.success("Lấy giá hiện tại danh sách sản phẩm thành công", result));
    }

    @GetMapping("/san-pham-chi-tiet")
    public ResponseEntity<ApiResponse<PageResponse<SanPhamChiTietResponseDTO>>> getSanPhamChiTiet(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long idSanPham,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<SanPhamChiTietResponseDTO> result = giamGiaService.searchSanPhamChiTiet(keyword, idSanPham, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách biến thể sản phẩm thành công", result));
    }
}

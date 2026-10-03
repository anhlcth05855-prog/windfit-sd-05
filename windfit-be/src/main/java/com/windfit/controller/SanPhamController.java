package com.windfit.controller;

import com.windfit.dto.ApiResponse;
import com.windfit.dto.product.*;
import com.windfit.service.SanPhamService;
import com.windfit.service.ThuocTinhService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/san-pham")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SanPhamResponseDTO>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long idDanhMuc,
            @RequestParam(required = false) Long idThuongHieu,
            @RequestParam(required = false) Long idChatLieu,
            @RequestParam(required = false) Long idXuatXu,
            @RequestParam(required = false) Long idKieuDang,
            @RequestParam(required = false) Long idMonTheThao,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        SanPhamFilterRequest filter = SanPhamFilterRequest.builder()
                .keyword(keyword)
                .idDanhMuc(idDanhMuc)
                .idThuongHieu(idThuongHieu)
                .idChatLieu(idChatLieu)
                .idXuatXu(idXuatXu)
                .idKieuDang(idKieuDang)
                .idMonTheThao(idMonTheThao)
                .trangThai(trangThai)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();

        Page<SanPhamResponseDTO> result = sanPhamService.getAllSanPham(filter);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách sản phẩm thành công", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SanPhamResponseDTO>> getById(@PathVariable Long id) {
        SanPhamResponseDTO result = sanPhamService.getSanPhamById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết sản phẩm thành công", result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SanPhamResponseDTO>> create(@Valid @RequestBody SanPhamCreateRequestDTO request) {
        SanPhamResponseDTO result = sanPhamService.createSanPham(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo mới sản phẩm thành công", result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SanPhamResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody SanPhamCreateRequestDTO request
    ) {
        SanPhamResponseDTO result = sanPhamService.updateSanPham(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật sản phẩm thành công", result));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(@PathVariable Long id) {
        sanPhamService.toggleStatusSanPham(id);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái sản phẩm thành công", null));
    }

    @GetMapping("/{id}/bien-the")
    public ResponseEntity<ApiResponse<List<SanPhamChiTietResponseDTO>>> getVariantsByProductId(@PathVariable Long id) {
        List<SanPhamChiTietResponseDTO> result = sanPhamService.getBienTheBySanPham(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách biến thể thành công", result));
    }

    @GetMapping("/bien-the/{spctId}")
    public ResponseEntity<ApiResponse<SanPhamChiTietResponseDTO>> getVariantById(@PathVariable Long spctId) {
        SanPhamChiTietResponseDTO result = sanPhamService.getBienTheById(spctId);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin biến thể thành công", result));
    }

    @PutMapping("/bien-the/{spctId}")
    public ResponseEntity<ApiResponse<SanPhamChiTietResponseDTO>> updateVariant(
            @PathVariable Long spctId,
            @Valid @RequestBody QuickUpdateVariantDTO request
    ) {
        SanPhamChiTietResponseDTO result = sanPhamService.updateBienThe(spctId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật biến thể thành công", result));
    }

    @PatchMapping("/bien-the/{spctId}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleVariantStatus(@PathVariable Long spctId) {
        sanPhamService.toggleStatusBienThe(spctId);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái biến thể thành công", null));
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ApiResponse<SanPhamChiTietResponseDTO>> getByBarcode(@PathVariable String barcode) {
        SanPhamChiTietResponseDTO result = sanPhamService.getBienTheByBarcode(barcode);
        return ResponseEntity.ok(ApiResponse.success("Tìm biến thể theo mã thành công", result));
    }
}

@RestController
@RequestMapping("/api/v1/thuoc-tinh")
@RequiredArgsConstructor
class ThuocTinhController {

    private final ThuocTinhService thuocTinhService;

    @GetMapping("/all-options")
    public ResponseEntity<ApiResponse<Map<String, List<ThuocTinhDTO>>>> getAllOptions() {
        return ResponseEntity.ok(thuocTinhService.getAllOptions());
    }

    @GetMapping("/{type}")
    public ResponseEntity<ApiResponse<List<ThuocTinhDTO>>> getByType(@PathVariable String type) {
        ApiResponse<List<ThuocTinhDTO>> res = thuocTinhService.getByType(type);
        if (!res.isSuccess()) {
            return ResponseEntity.badRequest().body(res);
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping("/{type}")
    public ResponseEntity<ApiResponse<ThuocTinhDTO>> create(
            @PathVariable String type,
            @Valid @RequestBody ThuocTinhDTO dto) {
        ApiResponse<ThuocTinhDTO> res = thuocTinhService.create(type, dto);
        if (!res.isSuccess()) {
            return ResponseEntity.badRequest().body(res);
        }
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{type}/{id}")
    public ResponseEntity<ApiResponse<ThuocTinhDTO>> update(
            @PathVariable String type,
            @PathVariable Long id,
            @Valid @RequestBody ThuocTinhDTO dto) {
        ApiResponse<ThuocTinhDTO> res = thuocTinhService.update(type, id, dto);
        if (!res.isSuccess()) {
            return ResponseEntity.badRequest().body(res);
        }
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/{type}/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String type,
            @PathVariable Long id) {
        ApiResponse<Void> res = thuocTinhService.toggleStatus(type, id);
        if (!res.isSuccess()) {
            return ResponseEntity.badRequest().body(res);
        }
        return ResponseEntity.ok(res);
    }
}

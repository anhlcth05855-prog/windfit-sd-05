<template>
  <div class="product-management-page">
    <!-- Header -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
      <div>
        <h4 class="fw-bold mb-1 d-flex align-items-center gap-2">
          <span>Quản Lý Sản Phẩm Áo Khoác Gió</span>
          <span class="badge bg-primary-subtle text-primary rounded-pill fs-6 px-3 py-1">{{ totalElements }} sản phẩm</span>
        </h4>
        <p class="text-muted mb-0 small">
          Hệ thống quản lý mẫu mã áo khoác gió thể thao WindFit, biến thể màu sắc - kích cỡ & kiểm soát tồn kho
        </p>
      </div>
      <div class="d-flex align-items-center gap-2">
        <button class="btn btn-outline-dark d-flex align-items-center gap-2 px-3 shadow-sm" @click="openBarcodeModal">
          <i class="bi bi-upc-scan"></i>
          <span>Quét Mã Vạch</span>
        </button>
        <button class="btn btn-primary d-flex align-items-center gap-2 px-3 shadow-sm fw-medium" @click="openCreateModal">
          <i class="bi bi-plus-circle-fill"></i>
          <span>Thêm Áo Khoác Mới</span>
        </button>
      </div>
    </div>

    <!-- Quick Overview Stats -->
    <div class="row g-3 mb-4">
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Tổng mẫu áo khoác</div>
              <div class="fs-4 fw-bold text-dark mt-1">{{ totalElements }}</div>
            </div>
            <div class="stat-icon bg-primary-subtle text-primary rounded-3">
              <i class="bi bi-layers-fill fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Đang kinh doanh</div>
              <div class="fs-4 fw-bold text-success mt-1">{{ activeCount }}</div>
            </div>
            <div class="stat-icon bg-success-subtle text-success rounded-3">
              <i class="bi bi-check-circle-fill fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Tổng tồn kho SPCT</div>
              <div class="fs-4 fw-bold text-primary mt-1">{{ totalStockCount.toLocaleString('vi-VN') }}</div>
            </div>
            <div class="stat-icon bg-info-subtle text-info rounded-3">
              <i class="bi bi-box-seam-fill fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Cảnh báo sắp hết hàng</div>
              <div class="fs-4 fw-bold text-warning mt-1">{{ lowStockCount }}</div>
            </div>
            <div class="stat-icon bg-warning-subtle text-warning rounded-3">
              <i class="bi bi-exclamation-triangle-fill fs-4"></i>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Filter & Table Card -->
    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
      <!-- Search & Filters -->
      <div class="row g-2 mb-4">
        <div class="col-md-3">
          <div class="input-group search-input-group">
            <span class="input-group-text bg-light border-end-0"><i class="bi bi-search text-muted"></i></span>
            <input 
              type="text" 
              v-model="filters.keyword" 
              @keyup.enter="loadProducts"
              class="form-control bg-light border-start-0" 
              placeholder="Tìm theo mã SP, tên áo..." 
            />
            <button class="btn btn-light border" v-if="filters.keyword" @click="filters.keyword = ''; loadProducts()">
              <i class="bi bi-x"></i>
            </button>
          </div>
        </div>

        <div class="col-6 col-md-2">
          <select class="form-select" v-model="filters.idDanhMuc" @change="loadProducts">
            <option value="">Tất cả danh mục</option>
            <option v-for="opt in attributes.danhMuc" :key="opt.id" :value="opt.id">{{ opt.label }}</option>
          </select>
        </div>

        <div class="col-6 col-md-2">
          <select class="form-select" v-model="filters.idThuongHieu" @change="loadProducts">
            <option value="">Tất cả thương hiệu</option>
            <option v-for="opt in attributes.thuongHieu" :key="opt.id" :value="opt.id">{{ opt.label }}</option>
          </select>
        </div>

        <div class="col-6 col-md-2">
          <select class="form-select" v-model="filters.idMonTheThao" @change="loadProducts">
            <option value="">Tất cả môn thể thao</option>
            <option v-for="opt in attributes.monTheThao" :key="opt.id" :value="opt.id">{{ opt.label }}</option>
          </select>
        </div>

        <div class="col-6 col-md-2">
          <select class="form-select" v-model="filters.trangThai" @change="loadProducts">
            <option value="">Tất cả trạng thái</option>
            <option value="1">Đang kinh doanh</option>
            <option value="0">Ngừng kinh doanh</option>
          </select>
        </div>

        <div class="col-12 col-md-1 d-flex gap-1">
          <button class="btn btn-light border w-100" @click="resetFilters" title="Đặt lại bộ lọc">
            <i class="bi bi-arrow-counterclockwise"></i>
          </button>
          <button class="btn btn-primary w-100" @click="loadProducts" title="Tìm kiếm">
            <i class="bi bi-search"></i>
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Đang tải...</span>
        </div>
        <p class="text-muted mt-2 small">Đang tải danh sách áo khoác gió...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="products.length === 0" class="text-center py-5">
        <i class="bi bi-inboxes text-muted display-4"></i>
        <h6 class="fw-bold mt-3 text-secondary">Không tìm thấy áo khoác gió nào</h6>
        <p class="text-muted small">Hãy thử tìm với từ khóa khác hoặc bấm nút thêm áo khoác mới</p>
        <button class="btn btn-sm btn-outline-primary" @click="openCreateModal">
          <i class="bi bi-plus"></i> Thêm Áo Khoác Mới
        </button>
      </div>

      <!-- Table View -->
      <div v-else class="table-responsive">
        <table class="table table-hover align-middle custom-table">
          <thead>
            <tr>
              <th style="width: 50px;">#</th>
              <th style="width: 80px;">Hình ảnh</th>
              <th style="width: 120px;">Mã SP</th>
              <th>Tên áo khoác & Đặc tính</th>
              <th>Danh mục & Thể thao</th>
              <th class="text-end">Khoảng giá</th>
              <th class="text-center">Biến thể</th>
              <th class="text-center">Tổng tồn kho</th>
              <th class="text-center">Trạng thái</th>
              <th class="text-center" style="width: 130px;">Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(p, index) in products" :key="p.id">
              <td class="text-muted small">{{ page * size + index + 1 }}</td>
              <td>
                <div class="product-thumb-wrapper">
                  <img 
                    :src="p.anhChinh || 'https://images.unsplash.com/photo-1544441893-675973e31985?w=200'" 
                    alt="Product"
                    class="product-thumb rounded-3 border"
                  />
                </div>
              </td>
              <td>
                <span class="badge bg-dark-subtle text-dark border font-monospace fw-bold">{{ p.maSanPham }}</span>
              </td>
              <td>
                <div class="fw-bold text-dark">{{ p.tenSanPham }}</div>
                <div class="d-flex flex-wrap gap-1 mt-1">
                  <span class="badge bg-secondary-subtle text-secondary small">{{ p.doiTuong }}</span>
                  <span class="badge bg-light text-muted border small" v-if="p.tenChatLieu">{{ p.tenChatLieu }}</span>
                  <span class="badge bg-light text-muted border small" v-if="p.tinhNang">{{ p.tinhNang }}</span>
                </div>
              </td>
              <td>
                <div class="small fw-semibold text-primary">{{ p.tenDanhMuc }}</div>
                <div class="small text-muted d-flex align-items-center gap-1 mt-1">
                  <i class="bi bi-activity"></i>
                  <span>{{ p.tenMonTheThao }}</span>
                </div>
              </td>
              <td class="text-end">
                <div class="fw-bold text-primary">
                  <span v-if="p.giaThapNhat === p.giaCaoNhat">
                    {{ formatCurrency(p.giaThapNhat) }}
                  </span>
                  <span v-else>
                    {{ formatCurrency(p.giaThapNhat) }} - {{ formatCurrency(p.giaCaoNhat) }}
                  </span>
                </div>
              </td>
              <td class="text-center">
                <span class="badge bg-light text-dark border px-2 py-1">
                  <i class="bi bi-grid-3x3-gap me-1 text-primary"></i>
                  {{ p.danhSachBienThe ? p.danhSachBienThe.length : 0 }} biến thể
                </span>
              </td>
              <td class="text-center">
                <span 
                  class="badge rounded-pill px-3 py-2 fw-bold"
                  :class="getStockBadgeClass(p.tongSoLuongTon)"
                >
                  {{ p.tongSoLuongTon }} chiếc
                </span>
              </td>
              <td class="text-center">
                <span 
                  class="badge px-2 py-1 rounded-pill"
                  :class="p.trangThai === 1 ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'"
                >
                  <i :class="p.trangThai === 1 ? 'bi bi-check-circle-fill me-1' : 'bi bi-x-circle-fill me-1'"></i>
                  {{ p.trangThai === 1 ? 'Đang bán' : 'Ngừng bán' }}
                </span>
              </td>
              <td class="text-center">
                <div class="btn-group btn-group-sm">
                  <button 
                    class="btn btn-outline-primary" 
                    @click="viewVariants(p)" 
                    title="Xem chi tiết & Quản lý ma trận biến thể"
                  >
                    <i class="bi bi-grid"></i>
                  </button>
                  <button 
                    class="btn btn-outline-secondary" 
                    @click="openEditModal(p)" 
                    title="Chỉnh sửa sản phẩm"
                  >
                    <i class="bi bi-pencil"></i>
                  </button>
                  <button 
                    class="btn" 
                    :class="p.trangThai === 1 ? 'btn-outline-danger' : 'btn-outline-success'"
                    @click="toggleProductStatus(p)" 
                    :title="p.trangThai === 1 ? 'Ngừng kinh doanh' : 'Mở bán lại'"
                  >
                    <i :class="p.trangThai === 1 ? 'bi bi-slash-circle' : 'bi bi-arrow-repeat'"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="d-flex flex-wrap justify-content-between align-items-center mt-4 gap-2" v-if="totalPages > 1">
        <div class="text-muted small">
          Hiển thị trang {{ page + 1 }} / {{ totalPages }} (Tổng {{ totalElements }} sản phẩm)
        </div>
        <ul class="pagination pagination-sm mb-0">
          <li class="page-item" :class="{ disabled: page === 0 }">
            <button class="page-link" @click="changePage(page - 1)"><i class="bi bi-chevron-left"></i></button>
          </li>
          <li 
            v-for="pNum in totalPages" 
            :key="pNum" 
            class="page-item"
            :class="{ active: page === pNum - 1 }"
          >
            <button class="page-link" @click="changePage(pNum - 1)">{{ pNum }}</button>
          </li>
          <li class="page-item" :class="{ disabled: page >= totalPages - 1 }">
            <button class="page-link" @click="changePage(page + 1)"><i class="bi bi-chevron-right"></i></button>
          </li>
        </ul>
      </div>
    </div>

    <!-- MODAL 1: VIEW & QUICK EDIT VARIANTS (SPCT MATRIX) -->
    <div class="modal fade" id="variantMatrixModal" tabindex="-1" aria-hidden="true" ref="variantMatrixModalRef">
      <div class="modal-dialog modal-xl modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content border-0 shadow-lg rounded-4">
          <div class="modal-header border-bottom bg-light px-4 py-3">
            <div>
              <div class="badge bg-primary text-white font-monospace mb-1">{{ selectedProduct?.maSanPham }}</div>
              <h5 class="modal-title fw-bold text-dark">{{ selectedProduct?.tenSanPham }}</h5>
            </div>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body p-4">
            <!-- Tabs inside detail modal -->
            <ul class="nav nav-pills custom-pills mb-4">
              <li class="nav-item">
                <button class="nav-link active" data-bs-toggle="pill" data-bs-target="#tab-variants">
                  <i class="bi bi-grid-3x3-gap me-2"></i>Ma Trận Biến Thể SPCT ({{ selectedProduct?.danhSachBienThe?.length || 0 }})
                </button>
              </li>
              <li class="nav-item">
                <button class="nav-link" data-bs-toggle="pill" data-bs-target="#tab-specs">
                  <i class="bi bi-info-circle me-2"></i>Thông Số Kỹ Thuật & Ảnh
                </button>
              </li>
            </ul>

            <div class="tab-content">
              <!-- Tab 1: Variants Matrix -->
              <div class="tab-pane fade show active" id="tab-variants">
                <div class="alert alert-info py-2 px-3 small d-flex align-items-center justify-content-between mb-3 rounded-3">
                  <div class="d-flex align-items-center gap-2">
                    <i class="bi bi-lightning-charge-fill text-warning fs-5"></i>
                    <span>Bạn có thể chỉnh sửa trực tiếp <strong>Giá bán</strong> và <strong>Số lượng tồn kho</strong> rồi bấm nút Lưu để cập nhật tức thì.</span>
                  </div>
                </div>

                <div class="table-responsive">
                  <table class="table table-hover align-middle border">
                    <thead class="table-light">
                      <tr>
                        <th>#</th>
                        <th>Mã SPCT</th>
                        <th>Màu Sắc</th>
                        <th>Kích Cỡ</th>
                        <th>Mã Vạch / Barcode</th>
                        <th style="width: 170px;">Giá Bán (VNĐ)</th>
                        <th style="width: 130px;">Tồn Kho</th>
                        <th class="text-center">Trạng Thái</th>
                        <th class="text-center" style="width: 100px;">Lưu</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(v, idx) in selectedVariants" :key="v.id">
                        <td class="text-muted small">{{ idx + 1 }}</td>
                        <td>
                          <span class="badge bg-light text-dark border font-monospace">{{ v.maSpct }}</span>
                        </td>
                        <td>
                          <span class="badge bg-dark px-2 py-1">{{ v.tenMauSac }}</span>
                        </td>
                        <td>
                          <span class="badge bg-secondary px-2 py-1">{{ v.tenKichCo }}</span>
                        </td>
                        <td>
                          <input 
                            type="text" 
                            v-model="v.maVach" 
                            class="form-control form-control-sm font-monospace" 
                            placeholder="Mã vạch..."
                          />
                        </td>
                        <td>
                          <div class="input-group input-group-sm">
                            <input 
                              type="number" 
                              v-model.number="v.giaBan" 
                              class="form-control fw-bold text-primary" 
                              min="0" 
                              step="5000"
                            />
                            <span class="input-group-text">đ</span>
                          </div>
                        </td>
                        <td>
                          <input 
                            type="number" 
                            v-model.number="v.soLuongTon" 
                            class="form-control form-control-sm fw-bold text-center" 
                            min="0"
                            :class="v.soLuongTon <= 10 ? 'border-warning text-warning' : 'text-success'"
                          />
                        </td>
                        <td class="text-center">
                          <button 
                            class="btn btn-sm"
                            :class="v.trangThai === 1 ? 'btn-success-subtle text-success' : 'btn-danger-subtle text-danger'"
                            @click="toggleVariantStatus(v)"
                          >
                            {{ v.trangThai === 1 ? 'Đang bán' : 'Khóa' }}
                          </button>
                        </td>
                        <td class="text-center">
                          <button 
                            class="btn btn-sm btn-primary px-2"
                            @click="saveQuickVariant(v)"
                            :disabled="v.saving"
                            title="Lưu biến thể này"
                          >
                            <i class="bi bi-check2" v-if="!v.saving"></i>
                            <span class="spinner-border spinner-border-sm" v-else></span>
                          </button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <!-- Tab 2: Specs & Images -->
              <div class="tab-pane fade" id="tab-specs">
                <div class="row g-4" v-if="selectedProduct">
                  <div class="col-md-5">
                    <h6 class="fw-bold mb-3">Thư Viện Ảnh Áo Khoác</h6>
                    <div class="d-flex flex-wrap gap-2">
                      <div 
                        v-for="(img, i) in selectedProduct.danhSachAnh" 
                        :key="i" 
                        class="position-relative border rounded-3 overflow-hidden" 
                        style="width: 120px; height: 120px;"
                      >
                        <img :src="img.urlAnh" class="w-100 h-100 object-fit-cover" alt="Image" />
                        <span v-if="img.isAnhChinh" class="badge bg-primary position-absolute top-0 start-0 m-1">Ảnh chính</span>
                      </div>
                    </div>
                  </div>
                  <div class="col-md-7">
                    <h6 class="fw-bold mb-3">Thông Số Kỹ Thuật Đầy Đủ</h6>
                    <div class="row g-2 small">
                      <div class="col-6 text-muted">Danh mục:</div>
                      <div class="col-6 fw-semibold">{{ selectedProduct.tenDanhMuc }}</div>

                      <div class="col-6 text-muted">Thương hiệu:</div>
                      <div class="col-6 fw-semibold">{{ selectedProduct.tenThuongHieu }}</div>

                      <div class="col-6 text-muted">Chất liệu:</div>
                      <div class="col-6 fw-semibold">{{ selectedProduct.tenChatLieu }}</div>

                      <div class="col-6 text-muted">Môn thể thao:</div>
                      <div class="col-6 fw-semibold">{{ selectedProduct.tenMonTheThao }}</div>

                      <div class="col-6 text-muted">Kiểu dáng:</div>
                      <div class="col-6 fw-semibold">{{ selectedProduct.tenKieuDang }}</div>

                      <div class="col-6 text-muted">Xuất xứ:</div>
                      <div class="col-6 fw-semibold">{{ selectedProduct.tenXuatXu }}</div>

                      <div class="col-6 text-muted">Đối tượng mặc:</div>
                      <div class="col-6 fw-semibold">{{ selectedProduct.doiTuong }}</div>

                      <div class="col-6 text-muted">Tính năng nổi bật:</div>
                      <div class="col-6 fw-semibold text-primary">{{ selectedProduct.tinhNang || 'Chưa cập nhật' }}</div>

                      <div class="col-12 mt-3">
                        <div class="text-muted mb-1">Mô tả chi tiết:</div>
                        <div class="p-3 bg-light rounded-3 text-secondary">{{ selectedProduct.moTaChiTiet || 'Không có mô tả chi tiết' }}</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer border-top px-4 py-3">
            <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">Đóng</button>
          </div>
        </div>
      </div>
    </div>

    <!-- MODAL 2: CREATE / EDIT PRODUCT WITH VARIANT MATRIX GENERATOR -->
    <div class="modal fade" id="productFormModal" tabindex="-1" aria-hidden="true" ref="productFormModalRef">
      <div class="modal-dialog modal-xl modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content border-0 shadow-lg rounded-4">
          <div class="modal-header border-bottom bg-light px-4 py-3">
            <h5 class="modal-title fw-bold text-dark">
              <i :class="isEditMode ? 'bi bi-pencil-square text-primary me-2' : 'bi bi-plus-circle-fill text-primary me-2'"></i>
              {{ isEditMode ? 'Cập Nhật Mẫu Áo Khoác Gió' : 'Tạo Mới Mẫu Áo Khoác Gió WindFit' }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>

          <div class="modal-body p-4">
            <form @submit.prevent="submitProductForm">
              <!-- SECTION 1: MASTER INFO -->
              <h6 class="fw-bold text-primary mb-3 d-flex align-items-center gap-2">
                <i class="bi bi-1-circle-fill"></i> Thông Tin Áo Khoác Gió
              </h6>

              <div class="row g-3 mb-4">
                <div class="col-md-4">
                  <label class="form-label small fw-semibold">Mã Sản Phẩm (Tự sinh nếu trống)</label>
                  <input 
                    type="text" 
                    v-model="productForm.maSanPham" 
                    class="form-control font-monospace" 
                    placeholder="VD: SP005 hoặc WF-JK01"
                    :disabled="isEditMode"
                  />
                </div>

                <div class="col-md-8">
                  <label class="form-label small fw-semibold">Tên Sản Phẩm Áo Khoác <span class="text-danger">*</span></label>
                  <input 
                    type="text" 
                    v-model="productForm.tenSanPham" 
                    required 
                    class="form-control" 
                    placeholder="VD: Áo khoác gió WindFit UltraLight 2026..."
                  />
                </div>

                <div class="col-md-4">
                  <label class="form-label small fw-semibold">Danh Mục <span class="text-danger">*</span></label>
                  <select v-model="productForm.idDanhMuc" required class="form-select">
                    <option value="" disabled>-- Chọn danh mục --</option>
                    <option v-for="opt in attributes.danhMuc" :key="opt.id" :value="opt.id">{{ opt.label }}</option>
                  </select>
                </div>

                <div class="col-md-4">
                  <label class="form-label small fw-semibold">Thương Hiệu <span class="text-danger">*</span></label>
                  <select v-model="productForm.idThuongHieu" required class="form-select">
                    <option value="" disabled>-- Chọn thương hiệu --</option>
                    <option v-for="opt in attributes.thuongHieu" :key="opt.id" :value="opt.id">{{ opt.label }}</option>
                  </select>
                </div>

                <div class="col-md-4">
                  <label class="form-label small fw-semibold">Chất Liệu <span class="text-danger">*</span></label>
                  <select v-model="productForm.idChatLieu" required class="form-select">
                    <option value="" disabled>-- Chọn chất liệu --</option>
                    <option v-for="opt in attributes.chatLieu" :key="opt.id" :value="opt.id">{{ opt.label }}</option>
                  </select>
                </div>

                <div class="col-md-4">
                  <label class="form-label small fw-semibold">Môn Thể Thao <span class="text-danger">*</span></label>
                  <select v-model="productForm.idMonTheThao" required class="form-select">
                    <option value="" disabled>-- Chọn môn thể thao --</option>
                    <option v-for="opt in attributes.monTheThao" :key="opt.id" :value="opt.id">{{ opt.label }}</option>
                  </select>
                </div>

                <div class="col-md-4">
                  <label class="form-label small fw-semibold">Kiểu Dáng <span class="text-danger">*</span></label>
                  <select v-model="productForm.idKieuDang" required class="form-select">
                    <option value="" disabled>-- Chọn kiểu dáng --</option>
                    <option v-for="opt in attributes.kieuDang" :key="opt.id" :value="opt.id">{{ opt.label }}</option>
                  </select>
                </div>

                <div class="col-md-4">
                  <label class="form-label small fw-semibold">Xuất Xứ <span class="text-danger">*</span></label>
                  <select v-model="productForm.idXuatXu" required class="form-select">
                    <option value="" disabled>-- Chọn xuất xứ --</option>
                    <option v-for="opt in attributes.xuatXu" :key="opt.id" :value="opt.id">{{ opt.label }}</option>
                  </select>
                </div>

                <div class="col-md-4">
                  <label class="form-label small fw-semibold">Đối Tượng Mặc</label>
                  <select v-model="productForm.doiTuong" class="form-select">
                    <option value="Unisex">Unisex (Cả nam & nữ)</option>
                    <option value="Nam">Nam</option>
                    <option value="Nữ">Nữ</option>
                    <option value="Trẻ em">Trẻ em</option>
                  </select>
                </div>

                <div class="col-md-8">
                  <label class="form-label small fw-semibold">Tính Năng Kỹ Thuật</label>
                  <input 
                    type="text" 
                    v-model="productForm.tinhNang" 
                    class="form-control" 
                    placeholder="VD: Cản gió 100%, chống thấm nước 10.000mm, phản quang đêm..."
                  />
                </div>

                <div class="col-12">
                  <label class="form-label small fw-semibold">Mô Tả Chi Tiết Sản Phẩm</label>
                  <textarea 
                    v-model="productForm.moTaChiTiet" 
                    rows="2" 
                    class="form-control" 
                    placeholder="Giới thiệu về mẫu áo khoác gió, độ bền, hướng dẫn bảo quản..."
                  ></textarea>
                </div>
              </div>

              <!-- SECTION 2: IMAGES -->
              <h6 class="fw-bold text-primary mb-3 d-flex align-items-center gap-2">
                <i class="bi bi-2-circle-fill"></i> Thư Viện Hình Ảnh Áo Khoác
              </h6>
              <div class="mb-4">
                <div class="input-group mb-2">
                  <span class="input-group-text bg-light"><i class="bi bi-image"></i></span>
                  <input 
                    type="url" 
                    v-model="newImageUrl" 
                    class="form-control" 
                    placeholder="Nhập đường dẫn URL ảnh (VD: https://images.unsplash.com/...)"
                  />
                  <button type="button" class="btn btn-outline-primary" @click="addImageUrl">
                    <i class="bi bi-plus-lg"></i> Thêm ảnh
                  </button>
                </div>

                <!-- Image preview tags -->
                <div class="d-flex flex-wrap gap-2 mt-2">
                  <div 
                    v-for="(img, idx) in productForm.danhSachAnh" 
                    :key="idx" 
                    class="position-relative border rounded-3 overflow-hidden shadow-sm image-thumb-item"
                    style="width: 100px; height: 100px;"
                  >
                    <img :src="img" class="w-100 h-100 object-fit-cover" alt="Image" />
                    <button 
                      type="button" 
                      class="btn btn-danger btn-sm p-0 position-absolute top-0 end-0 rounded-circle m-1 d-flex align-items-center justify-content-center"
                      style="width: 22px; height: 22px;"
                      @click="removeImageUrl(idx)"
                    >
                      <i class="bi bi-x"></i>
                    </button>
                    <span 
                      v-if="productForm.anhChinhUrl === img || (idx === 0 && !productForm.anhChinhUrl)" 
                      class="badge bg-success position-absolute bottom-0 start-0 m-1 small"
                    >
                      Chính
                    </span>
                    <button 
                      v-else
                      type="button" 
                      class="badge bg-dark bg-opacity-75 position-absolute bottom-0 start-0 m-1 border-0 text-white small"
                      @click="productForm.anhChinhUrl = img"
                    >
                      Đặt chính
                    </button>
                  </div>
                </div>
              </div>

              <!-- SECTION 3: VARIANT MATRIX GENERATOR -->
              <h6 class="fw-bold text-primary mb-3 d-flex align-items-center gap-2">
                <i class="bi bi-3-circle-fill"></i> Bộ Sinh Ma Trận Biến Thể SPCT (Màu Sắc x Kích Cỡ)
              </h6>

              <div class="card bg-light border-0 p-3 rounded-4 mb-3" v-if="!isEditMode">
                <div class="row g-3">
                  <!-- Select Colors -->
                  <div class="col-md-6">
                    <label class="form-label small fw-bold text-dark mb-2">
                      1. Chọn các Màu Sắc: ({{ matrixGenerator.selectedColors.length }})
                    </label>
                    <div class="d-flex flex-wrap gap-2 p-2 bg-white rounded-3 border">
                      <div 
                        v-for="color in attributes.mauSac" 
                        :key="color.id" 
                        class="form-check form-check-inline m-0"
                      >
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :id="'c-' + color.id" 
                          :value="color.id" 
                          v-model="matrixGenerator.selectedColors" 
                        />
                        <label class="form-check-label small" :for="'c-' + color.id">
                          {{ color.label }}
                        </label>
                      </div>
                    </div>
                  </div>

                  <!-- Select Sizes -->
                  <div class="col-md-6">
                    <label class="form-label small fw-bold text-dark mb-2">
                      2. Chọn các Kích Cỡ: ({{ matrixGenerator.selectedSizes.length }})
                    </label>
                    <div class="d-flex flex-wrap gap-2 p-2 bg-white rounded-3 border">
                      <div 
                        v-for="size in attributes.kichCo" 
                        :key="size.id" 
                        class="form-check form-check-inline m-0"
                      >
                        <input 
                          class="form-check-input" 
                          type="checkbox" 
                          :id="'s-' + size.id" 
                          :value="size.id" 
                          v-model="matrixGenerator.selectedSizes" 
                        />
                        <label class="form-check-label small" :for="'s-' + size.id">
                          {{ size.label }}
                        </label>
                      </div>
                    </div>
                  </div>

                  <!-- Base Price & Default Stock -->
                  <div class="col-md-4">
                    <label class="form-label small fw-semibold">Giá bán mặc định (VNĐ)</label>
                    <input 
                      type="number" 
                      v-model.number="matrixGenerator.defaultPrice" 
                      class="form-control" 
                      min="1000" 
                      step="5000"
                    />
                  </div>

                  <div class="col-md-4">
                    <label class="form-label small fw-semibold">Số lượng tồn ban đầu mỗi loại</label>
                    <input 
                      type="number" 
                      v-model.number="matrixGenerator.defaultStock" 
                      class="form-control" 
                      min="0"
                    />
                  </div>

                  <div class="col-md-4 d-flex align-items-end">
                    <button 
                      type="button" 
                      class="btn btn-dark w-100 d-flex align-items-center justify-content-center gap-2"
                      @click="generateMatrix"
                    >
                      <i class="bi bi-magic"></i>
                      <span>Tạo Ma Trận Biến Thể</span>
                    </button>
                  </div>
                </div>
              </div>

              <!-- Matrix Preview & Edit Table -->
              <div v-if="productForm.bienTheList.length > 0">
                <div class="d-flex justify-content-between align-items-center mb-2">
                  <span class="small fw-semibold text-secondary">
                    Danh sách biến thể ({{ productForm.bienTheList.length }} biến thể):
                  </span>
                  <button type="button" class="btn btn-sm btn-outline-danger" @click="productForm.bienTheList = []">
                    <i class="bi bi-trash"></i> Xóa tất cả biến thể
                  </button>
                </div>

                <div class="table-responsive border rounded-3" style="max-height: 280px; overflow-y: auto;">
                  <table class="table table-sm table-hover align-middle mb-0">
                    <thead class="table-light sticky-top">
                      <tr>
                        <th>#</th>
                        <th>Màu sắc</th>
                        <th>Kích cỡ</th>
                        <th>Mã SKU</th>
                        <th style="width: 160px;">Giá bán</th>
                        <th style="width: 120px;">Tồn kho</th>
                        <th class="text-center" style="width: 50px;">Xóa</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(item, idx) in productForm.bienTheList" :key="idx">
                        <td class="text-muted small">{{ idx + 1 }}</td>
                        <td>
                          <span class="badge bg-dark">{{ getColorName(item.idMauSac) }}</span>
                        </td>
                        <td>
                          <span class="badge bg-secondary">{{ getSizeName(item.idKichCo) }}</span>
                        </td>
                        <td>
                          <input 
                            type="text" 
                            v-model="item.maSpct" 
                            class="form-control form-control-sm font-monospace" 
                            placeholder="Tự sinh nếu trống"
                          />
                        </td>
                        <td>
                          <input 
                            type="number" 
                            v-model.number="item.giaBan" 
                            class="form-control form-control-sm fw-bold text-primary" 
                            min="0"
                            step="5000"
                            required
                          />
                        </td>
                        <td>
                          <input 
                            type="number" 
                            v-model.number="item.soLuongTon" 
                            class="form-control form-control-sm fw-bold text-center" 
                            min="0"
                            required
                          />
                        </td>
                        <td class="text-center">
                          <button 
                            type="button" 
                            class="btn btn-sm btn-link text-danger p-0" 
                            @click="productForm.bienTheList.splice(idx, 1)"
                          >
                            <i class="bi bi-x-circle-fill"></i>
                          </button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
              <div v-else class="alert alert-warning py-2 px-3 small rounded-3">
                <i class="bi bi-exclamation-triangle me-1"></i> Chưa có biến thể nào được tạo. Hãy chọn Màu Sắc và Kích Cỡ phía trên rồi bấm "Tạo Ma Trận Biến Thể".
              </div>

              <!-- Submit Buttons -->
              <div class="d-flex justify-content-end gap-2 mt-4 pt-3 border-top">
                <button type="button" class="btn btn-light px-4 border" data-bs-dismiss="modal">Hủy</button>
                <button 
                  type="submit" 
                  class="btn btn-primary px-4 fw-medium shadow-sm"
                  :disabled="submitting || productForm.bienTheList.length === 0"
                >
                  <span class="spinner-border spinner-border-sm me-1" v-if="submitting"></span>
                  <span>{{ isEditMode ? 'Lưu Thay Đổi' : 'Tạo Áo Khoác' }}</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- MODAL 3: BARCODE SCANNER TESTER -->
    <div class="modal fade" id="barcodeModal" tabindex="-1" aria-hidden="true" ref="barcodeModalRef">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
          <div class="modal-header border-bottom bg-light px-4 py-3">
            <h5 class="modal-title fw-bold text-dark d-flex align-items-center gap-2">
              <i class="bi bi-upc-scan text-primary"></i>
              <span>Tra Cứu Mã Vạch / SKU</span>
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body p-4">
            <div class="input-group mb-3">
              <span class="input-group-text bg-white"><i class="bi bi-barcode"></i></span>
              <input 
                type="text" 
                v-model="barcodeQuery" 
                @keyup.enter="handleScanBarcode" 
                class="form-control form-control-lg font-monospace" 
                placeholder="Nhập hoặc quét mã vạch (VD: 893000000001)..." 
                autofocus
              />
              <button class="btn btn-primary" @click="handleScanBarcode">
                <i class="bi bi-search"></i>
              </button>
            </div>

            <!-- Scanned Result Card -->
            <div v-if="scannedResult" class="card border rounded-4 p-3 bg-light">
              <div class="badge bg-success-subtle text-success w-auto align-self-start px-2 py-1 mb-2">Tìm Thấy Sản Phẩm</div>
              <h6 class="fw-bold mb-1">{{ scannedResult.tenSanPham }}</h6>
              <div class="text-muted small font-monospace mb-2">SKU: {{ scannedResult.maSpct }} | Mã vạch: {{ scannedResult.maVach }}</div>

              <div class="d-flex align-items-center gap-2 mb-2">
                <span class="badge bg-dark">{{ scannedResult.tenMauSac }}</span>
                <span class="badge bg-secondary">{{ scannedResult.tenKichCo }}</span>
                <span class="badge bg-primary fs-6">{{ formatCurrency(scannedResult.giaBan) }}</span>
              </div>

              <div class="d-flex align-items-center justify-content-between pt-2 border-top">
                <span class="small text-muted">Số lượng tồn kho:</span>
                <span class="fw-bold text-success">{{ scannedResult.soLuongTon }} chiếc</span>
              </div>
            </div>

            <div v-else-if="barcodeError" class="alert alert-danger py-2 px-3 small rounded-3 mt-2">
              <i class="bi bi-exclamation-circle me-1"></i> {{ barcodeError }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- TOAST NOTIFICATION -->
    <div class="position-fixed bottom-0 end-0 p-3" style="z-index: 1080;">
      <div 
        id="liveToast" 
        class="toast align-items-center text-white border-0 shadow-lg" 
        :class="toastType === 'success' ? 'bg-success' : 'bg-danger'" 
        role="alert" 
        aria-live="assertive" 
        aria-atomic="true" 
        ref="toastRef"
      >
        <div class="d-flex">
          <div class="toast-body d-flex align-items-center gap-2">
            <i :class="toastType === 'success' ? 'bi bi-check-circle-fill fs-5' : 'bi bi-exclamation-triangle-fill fs-5'"></i>
            <span>{{ toastMessage }}</span>
          </div>
          <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Modal, Toast } from 'bootstrap'
import { productService, attributeService } from '@/services/api'

// Data States
const products = ref([])
const loading = ref(false)
const submitting = ref(false)
const totalElements = ref(0)
const totalPages = ref(0)
const page = ref(0)
const size = ref(10)

// Filters
const filters = reactive({
  keyword: '',
  idDanhMuc: '',
  idThuongHieu: '',
  idMonTheThao: '',
  trangThai: ''
})

// Dynamic Attributes
const attributes = reactive({
  danhMuc: [],
  thuongHieu: [],
  chatLieu: [],
  xuatXu: [],
  kieuDang: [],
  monTheThao: [],
  mauSac: [],
  kichCo: []
})

// Modals & Toasts
const variantMatrixModalRef = ref(null)
const productFormModalRef = ref(null)
const barcodeModalRef = ref(null)
const toastRef = ref(null)

let variantModalInstance = null
let formModalInstance = null
let barcodeModalInstance = null
let toastInstance = null

const toastMessage = ref('')
const toastType = ref('success')

// Selected Product for Variant Matrix
const selectedProduct = ref(null)
const selectedVariants = ref([])

// Form Mode
const isEditMode = ref(false)
const editProductId = ref(null)
const newImageUrl = ref('')

const productForm = reactive({
  maSanPham: '',
  tenSanPham: '',
  idDanhMuc: '',
  idThuongHieu: '',
  idChatLieu: '',
  idXuatXu: '',
  idKieuDang: '',
  idMonTheThao: '',
  doiTuong: 'Unisex',
  tinhNang: '',
  moTaChiTiet: '',
  videoUrl: '',
  trangThai: 1,
  anhChinhUrl: '',
  danhSachAnh: [],
  bienTheList: []
})

// Matrix Generator State
const matrixGenerator = reactive({
  selectedColors: [],
  selectedSizes: [],
  defaultPrice: 590000,
  defaultStock: 25
})

// Barcode Lookup State
const barcodeQuery = ref('')
const scannedResult = ref(null)
const barcodeError = ref('')

// Computed Statistics
const activeCount = computed(() => products.value.filter(p => p.trangThai === 1).length)
const totalStockCount = computed(() => products.value.reduce((acc, p) => acc + (p.tongSoLuongTon || 0), 0))
const lowStockCount = computed(() => {
  let count = 0
  products.value.forEach(p => {
    if (p.danhSachBienThe) {
      count += p.danhSachBienThe.filter(v => v.soLuongTon <= 10).length
    }
  })
  return count
})

const showToast = (message, type = 'success') => {
  toastMessage.value = message
  toastType.value = type
  if (toastInstance) toastInstance.show()
}

const formatCurrency = (val) => {
  if (val === null || val === undefined) return '0 đ'
  return Number(val).toLocaleString('vi-VN') + ' đ'
}

const getStockBadgeClass = (stock) => {
  if (stock === 0) return 'bg-danger-subtle text-danger'
  if (stock <= 20) return 'bg-warning-subtle text-warning'
  return 'bg-success-subtle text-success'
}

// Load dynamic attributes
const loadAttributes = async () => {
  try {
    const res = await attributeService.getAllOptions()
    if (res.data && res.data.data) {
      const data = res.data.data
      attributes.danhMuc = data.danhMuc || []
      attributes.thuongHieu = data.thuongHieu || []
      attributes.chatLieu = data.chatLieu || []
      attributes.xuatXu = data.xuatXu || []
      attributes.kieuDang = data.kieuDang || []
      attributes.monTheThao = data.monTheThao || []
      attributes.mauSac = data.mauSac || []
      attributes.kichCo = data.kichCo || []
    }
  } catch (err) {
    console.error('Lỗi khi tải thuộc tính:', err)
  }
}

// Load Products
const loadProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value,
      keyword: filters.keyword || undefined,
      idDanhMuc: filters.idDanhMuc || undefined,
      idThuongHieu: filters.idThuongHieu || undefined,
      idMonTheThao: filters.idMonTheThao || undefined,
      trangThai: filters.trangThai !== '' ? filters.trangThai : undefined
    }

    const res = await productService.getAll(params)
    if (res.data && res.data.data) {
      const pData = res.data.data
      products.value = pData.content || []
      totalElements.value = pData.totalElements || 0
      totalPages.value = pData.totalPages || 0
    }
  } catch (err) {
    showToast('Lỗi khi tải danh sách sản phẩm', 'danger')
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.keyword = ''
  filters.idDanhMuc = ''
  filters.idThuongHieu = ''
  filters.idMonTheThao = ''
  filters.trangThai = ''
  page.value = 0
  loadProducts()
}

const changePage = (newPage) => {
  if (newPage >= 0 && newPage < totalPages.value) {
    page.value = newPage
    loadProducts()
  }
}

// View & Quick Edit Variants
const viewVariants = (product) => {
  selectedProduct.value = product
  selectedVariants.value = (product.danhSachBienThe || []).map(v => ({
    ...v,
    saving: false
  }))
  if (variantModalInstance) variantModalInstance.show()
}

const saveQuickVariant = async (variant) => {
  variant.saving = true
  try {
    await productService.updateVariant(variant.id, {
      giaBan: variant.giaBan,
      soLuongTon: variant.soLuongTon,
      trangThai: variant.trangThai,
      maVach: variant.maVach
    })
    showToast(`Đã lưu biến thể ${variant.maSpct}!`, 'success')
    loadProducts()
  } catch (err) {
    showToast('Lỗi khi lưu biến thể', 'danger')
  } finally {
    variant.saving = false
  }
}

const toggleVariantStatus = async (variant) => {
  try {
    await productService.toggleVariantStatus(variant.id)
    variant.trangThai = variant.trangThai === 1 ? 0 : 1
    showToast(`Đã đổi trạng thái biến thể ${variant.maSpct}`, 'success')
    loadProducts()
  } catch (err) {
    showToast('Lỗi khi đổi trạng thái biến thể', 'danger')
  }
}

const toggleProductStatus = async (product) => {
  try {
    await productService.toggleStatus(product.id)
    product.trangThai = product.trangThai === 1 ? 0 : 1
    showToast(`Đã đổi trạng thái sản phẩm ${product.maSanPham}`, 'success')
  } catch (err) {
    showToast('Lỗi khi đổi trạng thái sản phẩm', 'danger')
  }
}

// Image handling in form
const addImageUrl = () => {
  if (!newImageUrl.value.trim()) return
  productForm.danhSachAnh.push(newImageUrl.value.trim())
  if (!productForm.anhChinhUrl) {
    productForm.anhChinhUrl = newImageUrl.value.trim()
  }
  newImageUrl.value = ''
}

const removeImageUrl = (index) => {
  const removed = productForm.danhSachAnh.splice(index, 1)[0]
  if (productForm.anhChinhUrl === removed) {
    productForm.anhChinhUrl = productForm.danhSachAnh[0] || ''
  }
}

// Matrix Generator
const getColorName = (id) => {
  const c = attributes.mauSac.find(x => x.id === id)
  return c ? c.label : id
}

const getSizeName = (id) => {
  const s = attributes.kichCo.find(x => x.id === id)
  return s ? s.label : id
}

const generateMatrix = () => {
  if (matrixGenerator.selectedColors.length === 0 || matrixGenerator.selectedSizes.length === 0) {
    showToast('Vui lòng chọn ít nhất 1 màu sắc và 1 kích cỡ để sinh ma trận!', 'danger')
    return
  }

  const generated = []
  matrixGenerator.selectedColors.forEach(cId => {
    matrixGenerator.selectedSizes.forEach(sId => {
      generated.push({
        idMauSac: cId,
        idKichCo: sId,
        maSpct: '',
        giaBan: matrixGenerator.defaultPrice || 590000,
        soLuongTon: matrixGenerator.defaultStock !== undefined ? matrixGenerator.defaultStock : 20,
        trangThai: 1
      })
    })
  })

  productForm.bienTheList = generated
  showToast(`Đã sinh ${generated.length} biến thể thành công!`, 'success')
}

// Create & Edit Modal
const openCreateModal = () => {
  isEditMode.value = false
  editProductId.value = null
  Object.assign(productForm, {
    maSanPham: '',
    tenSanPham: '',
    idDanhMuc: attributes.danhMuc[0]?.id || '',
    idThuongHieu: attributes.thuongHieu[0]?.id || '',
    idChatLieu: attributes.chatLieu[0]?.id || '',
    idXuatXu: attributes.xuatXu[0]?.id || '',
    idKieuDang: attributes.kieuDang[0]?.id || '',
    idMonTheThao: attributes.monTheThao[0]?.id || '',
    doiTuong: 'Unisex',
    tinhNang: 'Chống thấm nước, cản gió tuyệt đối',
    moTaChiTiet: '',
    videoUrl: '',
    trangThai: 1,
    anhChinhUrl: 'https://images.unsplash.com/photo-1544441893-675973e31985?w=500',
    danhSachAnh: ['https://images.unsplash.com/photo-1544441893-675973e31985?w=500'],
    bienTheList: []
  })

  // Pre-select first 2 colors and sizes for user convenience
  matrixGenerator.selectedColors = attributes.mauSac.slice(0, 2).map(x => x.id)
  matrixGenerator.selectedSizes = attributes.kichCo.slice(0, 3).map(x => x.id)
  generateMatrix()

  if (formModalInstance) formModalInstance.show()
}

const openEditModal = (p) => {
  isEditMode.value = true
  editProductId.value = p.id
  Object.assign(productForm, {
    maSanPham: p.maSanPham,
    tenSanPham: p.tenSanPham,
    idDanhMuc: p.idDanhMuc,
    idThuongHieu: p.idThuongHieu,
    idChatLieu: p.idChatLieu,
    idXuatXu: p.idXuatXu,
    idKieuDang: p.idKieuDang,
    idMonTheThao: p.idMonTheThao,
    doiTuong: p.doiTuong,
    tinhNang: p.tinhNang,
    moTaChiTiet: p.moTaChiTiet,
    videoUrl: p.videoUrl,
    trangThai: p.trangThai,
    anhChinhUrl: p.anhChinh,
    danhSachAnh: (p.danhSachAnh || []).map(img => img.urlAnh),
    bienTheList: (p.danhSachBienThe || []).map(v => ({
      id: v.id,
      idMauSac: v.idMauSac,
      idKichCo: v.idKichCo,
      maSpct: v.maSpct,
      maVach: v.maVach,
      giaBan: v.giaBan,
      soLuongTon: v.soLuongTon,
      trangThai: v.trangThai
    }))
  })

  if (formModalInstance) formModalInstance.show()
}

const submitProductForm = async () => {
  submitting.value = true
  try {
    if (isEditMode.value) {
      await productService.update(editProductId.value, productForm)
      showToast('Cập nhật sản phẩm thành công!', 'success')
    } else {
      await productService.create(productForm)
      showToast('Tạo mới áo khoác gió thành công!', 'success')
    }

    if (formModalInstance) formModalInstance.hide()
    loadProducts()
  } catch (err) {
    const msg = err.response?.data?.message || 'Có lỗi xảy ra khi lưu sản phẩm'
    showToast(msg, 'danger')
  } finally {
    submitting.value = false
  }
}

// Barcode Lookup Modal
const openBarcodeModal = () => {
  barcodeQuery.value = ''
  scannedResult.value = null
  barcodeError.value = ''
  if (barcodeModalInstance) barcodeModalInstance.show()
}

const handleScanBarcode = async () => {
  if (!barcodeQuery.value.trim()) return
  barcodeError.value = ''
  scannedResult.value = null
  try {
    const res = await productService.scanBarcode(barcodeQuery.value.trim())
    if (res.data && res.data.data) {
      scannedResult.value = res.data.data
    }
  } catch (err) {
    barcodeError.value = 'Không tìm thấy sản phẩm với mã barcode / SKU: ' + barcodeQuery.value
  }
}

onMounted(async () => {
  if (variantMatrixModalRef.value) variantModalInstance = new Modal(variantMatrixModalRef.value)
  if (productFormModalRef.value) formModalInstance = new Modal(productFormModalRef.value)
  if (barcodeModalRef.value) barcodeModalInstance = new Modal(barcodeModalRef.value)
  if (toastRef.value) toastInstance = new Toast(toastRef.value, { delay: 3000 })

  await loadAttributes()
  await loadProducts()
})
</script>

<style scoped>
.product-management-page {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
}

.stat-card {
  transition: transform 0.2s, box-shadow 0.2s;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.08) !important;
}

.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-input-group .form-control:focus {
  background-color: #fff !important;
}

.product-thumb-wrapper {
  width: 52px;
  height: 52px;
}
.product-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.custom-table th {
  background-color: #f8fafc;
  color: #475569;
  font-size: 0.78rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-weight: 700;
  border-top: none;
}

.custom-pills .nav-link {
  color: #64748b;
  border-radius: 9999px;
  padding: 0.5rem 1.25rem;
  font-weight: 600;
  font-size: 0.88rem;
}
.custom-pills .nav-link.active {
  background-color: #0f172a;
  color: #fff;
}
</style>

<template>
  <div class="campaign-management-page">
    <!-- Header -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
      <div>
        <h4 class="fw-bold mb-1 d-flex align-items-center gap-2">
          <i class="bi bi-fire text-danger"></i>
          <span>Quản Lý Đợt Giảm Giá (Sale)</span>
          <span class="badge bg-danger-subtle text-danger rounded-pill fs-6 px-3 py-1">{{ totalElements }} chương trình</span>
        </h4>
        <p class="text-muted mb-0 small">
          Tạo các chiến dịch flash sale, giảm giá trực tiếp theo từng biến thể áo khoác gió (SPCT)
        </p>
      </div>
      <div class="d-flex align-items-center gap-2">
        <button class="btn btn-primary d-flex align-items-center gap-2 px-3 shadow-sm fw-medium rounded-3 btn-sm" @click="openCreateModal">
          <i class="bi bi-plus-circle-fill"></i>
          <span>Tạo Đợt Giảm Giá Mới</span>
        </button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="row g-3 mb-4">
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Tổng số chiến dịch</div>
              <div class="fs-4 fw-bold text-dark mt-1">{{ totalElements }}</div>
            </div>
            <div class="stat-icon bg-danger-subtle text-danger rounded-3 d-flex align-items-center justify-content-center" style="width: 44px; height: 44px;">
              <i class="bi bi-fire fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Đang diễn ra</div>
              <div class="fs-4 fw-bold text-success mt-1">{{ activeCount }}</div>
            </div>
            <div class="stat-icon bg-success-subtle text-success rounded-3 d-flex align-items-center justify-content-center" style="width: 44px; height: 44px;">
              <i class="bi bi-play-circle-fill fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Sắp diễn ra</div>
              <div class="fs-4 fw-bold text-primary mt-1">{{ upcomingCount }}</div>
            </div>
            <div class="stat-icon bg-primary-subtle text-primary rounded-3 d-flex align-items-center justify-content-center" style="width: 44px; height: 44px;">
              <i class="bi bi-calendar2-week-fill fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Đã khóa / Kết thúc</div>
              <div class="fs-4 fw-bold text-secondary mt-1">{{ endedCount }}</div>
            </div>
            <div class="stat-icon bg-secondary-subtle text-secondary rounded-3 d-flex align-items-center justify-content-center" style="width: 44px; height: 44px;">
              <i class="bi bi-archive-fill fs-4"></i>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Filter Bar (Component dùng chung) -->
    <FilterBar
      v-model:keyword="filters.keyword"
      searchPlaceholder="Tìm theo mã đợt, tên chương trình sale..."
      @search="onSearch"
      @reset="resetFilters"
    >
      <!-- Filter Trạng thái -->
      <div class="col-6 col-md-auto">
        <select class="form-select form-select-sm bg-light" v-model="filters.trangThai" @change="onFilterChange">
          <option value="">-- Tất cả trạng thái --</option>
          <option value="1">Đang hoạt động</option>
          <option value="0">Tạm khóa / Ẩn</option>
        </select>
      </div>

      <!-- Filter Từ ngày -->
      <div class="col-6 col-md-auto d-flex align-items-center gap-1">
        <span class="small text-muted text-nowrap">Từ:</span>
        <input type="date" class="form-control form-control-sm bg-light" v-model="filters.tuNgay" @change="onFilterChange" />
      </div>

      <!-- Filter Đến ngày -->
      <div class="col-6 col-md-auto d-flex align-items-center gap-1">
        <span class="small text-muted text-nowrap">Đến:</span>
        <input type="date" class="form-control form-control-sm bg-light" v-model="filters.denNgay" @change="onFilterChange" />
      </div>
    </FilterBar>

    <!-- Main Card & Table -->
    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
      <!-- Loading State -->
      <LoadingState v-if="loading" text="Đang tải danh sách đợt giảm giá..." />

      <!-- Error State -->
      <div v-else-if="errorMessage" class="text-center p-5 rounded-4 bg-light border border-danger my-3">
        <i class="bi bi-exclamation-triangle-fill text-danger fs-1 mb-2"></i>
        <h6 class="fw-bold text-dark">Không thể tải dữ liệu</h6>
        <p class="text-danger small mb-3">{{ errorMessage }}</p>
        <button class="btn btn-outline-primary btn-sm px-3 rounded-3" @click="loadCampaigns">
          <i class="bi bi-arrow-clockwise me-1"></i> Thử lại
        </button>
      </div>

      <!-- Empty State -->
      <EmptyState
        v-else-if="campaigns.length === 0"
        title="Không tìm thấy đợt giảm giá"
        description="Không có đợt giảm giá nào phù hợp với bộ lọc hiện tại. Thử đặt lại bộ lọc hoặc tạo đợt mới."
      >
        <template #action>
          <button class="btn btn-primary btn-sm rounded-3 px-3" @click="openCreateModal">
            <i class="bi bi-plus-circle me-1"></i> Tạo Đợt Giảm Giá Mới
          </button>
        </template>
      </EmptyState>

      <!-- Data Table -->
      <div v-else class="table-responsive">
        <table class="table table-hover align-middle mb-0 custom-table">
          <thead class="table-light text-secondary small text-uppercase">
            <tr>
              <th style="width: 50px;">#</th>
              <th style="width: 140px;">Mã Đợt</th>
              <th>Tên Chương Trình</th>
              <th>Mô Tả / Điều Kiện</th>
              <th style="width: 130px;">SP Áp Dụng</th>
              <th>Thời Gian Hiệu Lực</th>
              <th class="text-center">Tình Trạng</th>
              <th class="text-center" style="width: 130px;">Thao Tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(c, idx) in campaigns" :key="c.id">
              <td class="text-muted small">{{ page * size + idx + 1 }}</td>
              <td>
                <div class="d-flex align-items-center gap-1">
                  <span class="badge bg-danger text-white font-monospace fs-6 px-2 py-1 shadow-sm">{{ c.maDotGiamGia }}</span>
                  <button class="btn btn-sm btn-link text-muted p-0" @click="copyCode(c.maDotGiamGia)" title="Copy mã">
                    <i class="bi bi-clipboard"></i>
                  </button>
                </div>
              </td>
              <td>
                <div class="fw-bold text-dark">{{ c.tenDotGiamGia }}</div>
              </td>
              <td>
                <div class="small text-muted text-truncate" style="max-width: 250px;" :title="c.moTa || c.dieuKienApDung">
                  {{ c.moTa || c.dieuKienApDung || '---' }}
                </div>
              </td>
              <td>
                <button
                  class="badge bg-info-subtle text-info border-info px-2.5 py-1.5 rounded-pill border fw-semibold d-inline-flex align-items-center gap-1 btn btn-sm"
                  @click="openDetailModal(c)"
                  title="Xem danh sách áo khoác trong đợt"
                >
                  <i class="bi bi-layers-fill"></i>
                  <span>{{ c.soLuongSanPham || 0 }} biến thể</span>
                </button>
              </td>
              <td>
                <div class="small text-muted">
                  <div><i class="bi bi-calendar-event me-1"></i>Từ: {{ formatDate(c.ngayBatDau) }}</div>
                  <div><i class="bi bi-calendar-x me-1"></i>Đến: {{ formatDate(c.ngayKetThuc) }}</div>
                </div>
              </td>
              <td class="text-center">
                <StatusBadge :status="c.tinhTrang" />
              </td>
              <td class="text-center">
                <div class="btn-group btn-group-sm">
                  <!-- Xem chi tiết -->
                  <button class="btn btn-outline-info" @click="openDetailModal(c)" title="Xem chi tiết">
                    <i class="bi bi-eye"></i>
                  </button>
                  <!-- Sửa -->
                  <button class="btn btn-outline-secondary" @click="openEditModal(c)" title="Chỉnh sửa">
                    <i class="bi bi-pencil"></i>
                  </button>
                  <!-- Đổi trạng thái (Soft Delete / Reactive) -->
                  <button
                    class="btn"
                    :class="c.trangThai === 1 ? 'btn-outline-danger' : 'btn-outline-success'"
                    @click="confirmToggleStatus(c)"
                    :title="c.trangThai === 1 ? 'Tạm ngưng đợt giảm giá' : 'Kích hoạt lại'"
                  >
                    <i :class="c.trangThai === 1 ? 'bi bi-slash-circle' : 'bi bi-arrow-repeat'"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- AppPagination Component -->
      <AppPagination
        v-if="!loading && !errorMessage && totalElements > 0"
        :currentPage="page"
        :pageSize="size"
        :totalElements="totalElements"
        :totalPages="totalPages"
        @page-change="onPageChange"
        @size-change="onSizeChange"
      />
    </div>

    <!-- Modal Form Campaign -->
    <div class="modal fade" id="campaignFormModal" tabindex="-1" aria-hidden="true" ref="campaignFormModalRef">
      <div class="modal-dialog modal-dialog-centered modal-xl">
        <div class="modal-content border-0 shadow-lg rounded-4">
          <div class="modal-header border-bottom bg-light px-4 py-3">
            <h5 class="modal-title fw-bold text-dark d-flex align-items-center gap-2">
              <i class="bi" :class="isEditMode ? 'bi-pencil-square text-warning' : 'bi-plus-circle-fill text-danger'"></i>
              <span>{{ isEditMode ? 'Chỉnh Sửa Đợt Giảm Giá' : 'Tạo Đợt Giảm Giá Mới' }}</span>
            </h5>
            <button type="button" class="btn-close" @click="handleCloseFormModal" aria-label="Close"></button>
          </div>
          <div class="modal-body p-4">
            <form @submit.prevent="submitForm">
              <div class="row g-3 mb-4">
                <!-- Mã Đợt -->
                <div class="col-md-4">
                  <label class="form-label small fw-semibold">Mã Đợt Giảm Giá</label>
                  <input
                    type="text"
                    v-model="form.maDotGiamGia"
                    class="form-control font-monospace text-uppercase"
                    placeholder="Hệ thống tự sinh (VD: DGG12345)"
                  />
                </div>

                <!-- Tên Đợt -->
                <div class="col-md-8">
                  <label class="form-label small fw-semibold">Tên Đợt Giảm Giá <span class="text-danger">*</span></label>
                  <input
                    type="text"
                    v-model="form.tenDotGiamGia"
                    class="form-control"
                    :class="{ 'is-invalid': errors.tenDotGiamGia }"
                    placeholder="VD: Flash Sale Áo Khoác Chống Nước Mùa Thu"
                  />
                  <div class="invalid-feedback" v-if="errors.tenDotGiamGia">{{ errors.tenDotGiamGia }}</div>
                </div>

                <!-- Ngày bắt đầu -->
                <div class="col-md-4">
                  <label class="form-label small fw-semibold">Ngày Bắt Đầu <span class="text-danger">*</span></label>
                  <input
                    type="datetime-local"
                    v-model="form.ngayBatDau"
                    class="form-control"
                    :class="{ 'is-invalid': errors.ngayBatDau }"
                  />
                  <div class="invalid-feedback" v-if="errors.ngayBatDau">{{ errors.ngayBatDau }}</div>
                </div>

                <!-- Ngày kết thúc -->
                <div class="col-md-4">
                  <label class="form-label small fw-semibold">Ngày Kết Thúc <span class="text-danger">*</span></label>
                  <input
                    type="datetime-local"
                    v-model="form.ngayKetThuc"
                    class="form-control"
                    :class="{ 'is-invalid': errors.ngayKetThuc }"
                  />
                  <div class="invalid-feedback" v-if="errors.ngayKetThuc">{{ errors.ngayKetThuc }}</div>
                </div>

                <!-- Trạng thái ban đầu -->
                <div class="col-md-4">
                  <label class="form-label small fw-semibold">Trạng Thái</label>
                  <select v-model="form.trangThai" class="form-select">
                    <option :value="1">Kích hoạt ngay</option>
                    <option :value="0">Tạm khóa / Ẩn</option>
                  </select>
                </div>

                <!-- Mô tả & Điều kiện -->
                <div class="col-md-6">
                  <label class="form-label small fw-semibold">Mô Tả Chiến Dịch</label>
                  <input type="text" v-model="form.moTa" class="form-control" placeholder="Mô tả ngắn gọn về chương trình sale" />
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-semibold">Điều Kiện Áp Dụng</label>
                  <input type="text" v-model="form.dieuKienApDung" class="form-control" placeholder="VD: Áp dụng trên toàn bộ chi nhánh và online" />
                </div>
              </div>

              <!-- Selected SPCT Section -->
              <div class="border rounded-4 p-3 bg-light bg-opacity-50">
                <div class="d-flex justify-content-between align-items-center mb-3">
                  <div class="d-flex align-items-center gap-2">
                    <h6 class="fw-bold mb-0 text-dark">Danh Sách Áo Khoác Gió Áp Dụng</h6>
                    <span class="badge bg-primary rounded-pill">{{ form.selectedItems.length }} biến thể</span>
                  </div>
                  <button
                    type="button"
                    class="btn btn-outline-primary btn-sm rounded-3 d-flex align-items-center gap-1.5"
                    @click="openProductPicker"
                  >
                    <i class="bi bi-plus-lg"></i>
                    <span>Chọn Thêm Biến Thể SPCT</span>
                  </button>
                </div>

                <div v-if="errors.selectedItems" class="alert alert-danger py-2 small mb-3">
                  <i class="bi bi-exclamation-circle-fill me-1"></i> {{ errors.selectedItems }}
                </div>

                <!-- Empty selected products -->
                <div v-if="form.selectedItems.length === 0" class="text-center py-4 bg-white rounded-3 border">
                  <i class="bi bi-bag-plus text-muted fs-3 mb-1 d-block"></i>
                  <span class="text-muted small">Chưa có sản phẩm nào được chọn vào đợt giảm giá này.</span>
                </div>

                <!-- Selected items table -->
                <div v-else class="table-responsive bg-white rounded-3 border" style="max-height: 280px; overflow-y: auto;">
                  <table class="table table-sm align-middle mb-0">
                    <thead class="table-light text-muted small sticky-top">
                      <tr>
                        <th>Mã SPCT</th>
                        <th>Tên Áo Khoác Gió</th>
                        <th>Phân Loại</th>
                        <th>Giá Gốc</th>
                        <th style="width: 140px;">% Giảm</th>
                        <th>Giá Sau Giảm</th>
                        <th style="width: 120px;">SL Áp Dụng</th>
                        <th style="width: 50px;" class="text-center">Xóa</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(item, idx) in form.selectedItems" :key="item.idSanPhamChiTiet">
                        <td><span class="badge bg-light text-dark border font-monospace">{{ item.maSpct }}</span></td>
                        <td class="fw-semibold text-dark">{{ item.tenSanPham }}</td>
                        <td>
                          <span class="badge bg-secondary-subtle text-secondary me-1">{{ item.tenMauSac }}</span>
                          <span class="badge bg-secondary-subtle text-secondary">{{ item.tenKichCo }}</span>
                        </td>
                        <td class="text-muted">{{ formatCurrency(item.giaGoc) }}</td>
                        <td>
                          <div class="input-group input-group-sm">
                            <input
                              type="number"
                              v-model.number="item.phanTramGiam"
                              class="form-control text-center fw-bold text-danger"
                              min="1"
                              max="99"
                              @input="recalculateDiscount(item)"
                            />
                            <span class="input-group-text">%</span>
                          </div>
                        </td>
                        <td>
                          <span class="fw-bold text-success">{{ formatCurrency(item.giaSauGiam) }}</span>
                        </td>
                        <td>
                          <input
                            type="number"
                            v-model.number="item.soLuong"
                            class="form-control form-control-sm text-center"
                            min="1"
                            :max="item.soLuongTonHienTai || 999"
                          />
                        </td>
                        <td class="text-center">
                          <button
                            type="button"
                            class="btn btn-sm btn-link text-danger p-0"
                            @click="removeSelectedItem(idx, item)"
                            title="Bỏ biến thể này"
                          >
                            <i class="bi bi-trash3"></i>
                          </button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <!-- Form Buttons -->
              <div class="d-flex justify-content-end gap-2 mt-4 pt-3 border-top">
                <button type="button" class="btn btn-light px-4 border rounded-3" @click="handleCloseFormModal">
                  Hủy
                </button>
                <button type="submit" class="btn btn-danger px-4 fw-medium shadow-sm rounded-3" :disabled="submitting">
                  <span class="spinner-border spinner-border-sm me-1" v-if="submitting"></span>
                  <span>{{ isEditMode ? 'Lưu Thay Đổi' : 'Tạo Đợt Giảm Giá' }}</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Chọn Biến Thể -->
    <div class="modal fade" id="productPickerModal" tabindex="-1" aria-hidden="true" ref="productPickerModalRef">
      <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content border-0 shadow-lg rounded-4">
          <div class="modal-header border-bottom bg-light px-4 py-3">
            <h5 class="modal-title fw-bold text-dark d-flex align-items-center gap-2">
              <i class="bi bi-box-seam-fill text-primary"></i>
              <span>Chọn Biến Thể Áo Khoác Gió Đưa Vào Đợt</span>
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body p-4">
            <!-- Search & Quick percentage input -->
            <div class="row g-2 mb-3 align-items-center">
              <div class="col-md-7">
                <div class="input-group">
                  <span class="input-group-text bg-light"><i class="bi bi-search"></i></span>
                  <input
                    type="text"
                    class="form-control"
                    placeholder="Tìm theo mã SPCT, tên áo khoác, barcode..."
                    v-model="spctSearchKw"
                    @input="onSpctSearchInput"
                  />
                </div>
              </div>
              <div class="col-md-5 d-flex align-items-center gap-2">
                <span class="small text-muted text-nowrap">Mức giảm chung:</span>
                <div class="input-group input-group-sm">
                  <input type="number" class="form-control text-center" v-model.number="defaultDiscountPercent" min="1" max="99" />
                  <span class="input-group-text">%</span>
                </div>
              </div>
            </div>

            <!-- SPCT Table with Server Pagination -->
            <div class="table-responsive" style="max-height: 320px; overflow-y: auto;">
              <table class="table table-sm table-hover align-middle mb-0">
                <thead class="table-light text-muted small sticky-top">
                  <tr>
                    <th style="width: 40px;">
                      <input
                        type="checkbox"
                        class="form-check-input"
                        :checked="isAllSpctOnPageSelected"
                        @change="toggleSelectAllSpctOnPage"
                      />
                    </th>
                    <th>Mã SPCT</th>
                    <th>Tên Sản Phẩm</th>
                    <th>Màu Sắc</th>
                    <th>Kích Cỡ</th>
                    <th>Giá Bán</th>
                    <th>Tồn Kho</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="spctLoading">
                    <td colspan="7" class="text-center py-4 text-muted small">Đang tải danh sách biến thể...</td>
                  </tr>
                  <tr v-else-if="spctList.length === 0">
                    <td colspan="7" class="text-center py-4 text-muted small">Không tìm thấy biến thể nào.</td>
                  </tr>
                  <tr v-for="s in spctList" :key="s.id" @click="toggleSelectSpct(s)" style="cursor: pointer;">
                    <td>
                      <input
                        type="checkbox"
                        class="form-check-input"
                        :checked="pickerSelectedSpctMap.has(s.id)"
                        @click.stop
                        @change="toggleSelectSpct(s)"
                      />
                    </td>
                    <td><span class="badge bg-light text-dark border font-monospace">{{ s.maSpct }}</span></td>
                    <td class="fw-semibold text-dark">{{ s.tenSanPham }}</td>
                    <td><span class="badge bg-secondary-subtle text-secondary">{{ s.tenMauSac }}</span></td>
                    <td><span class="badge bg-secondary-subtle text-secondary">{{ s.tenKichCo }}</span></td>
                    <td class="fw-bold text-dark">{{ formatCurrency(s.giaBan) }}</td>
                    <td>
                      <span class="badge" :class="s.soLuongTon > 0 ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'">
                        {{ s.soLuongTon }}
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- SPCT Pagination -->
            <div class="d-flex justify-content-between align-items-center mt-3 pt-2 border-top small text-muted">
              <span>Đã chọn: <strong class="text-danger">{{ pickerSelectedSpctMap.size }}</strong> biến thể</span>
              <div class="btn-group btn-group-sm">
                <button
                  class="btn btn-outline-secondary"
                  :disabled="spctPage === 0 || spctLoading"
                  @click="changeSpctPage(spctPage - 1)"
                >
                  <i class="bi bi-chevron-left"></i>
                </button>
                <button class="btn btn-light disabled">
                  {{ spctPage + 1 }} / {{ spctTotalPages || 1 }}
                </button>
                <button
                  class="btn btn-outline-secondary"
                  :disabled="spctPage >= spctTotalPages - 1 || spctLoading"
                  @click="changeSpctPage(spctPage + 1)"
                >
                  <i class="bi bi-chevron-right"></i>
                </button>
              </div>
            </div>

            <!-- Confirm Add Selected SPCT -->
            <div class="d-flex justify-content-end gap-2 mt-4 pt-3 border-top">
              <button type="button" class="btn btn-light px-4 border rounded-3" data-bs-dismiss="modal">Đóng</button>
              <button
                type="button"
                class="btn btn-primary px-4 fw-medium shadow-sm rounded-3"
                :disabled="pickerSelectedSpctMap.size === 0"
                @click="applySelectedSpctToForm"
              >
                <span>Thêm {{ pickerSelectedSpctMap.size }} Biến Thể Vào Đợt</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Chi Tiết Đợt Giảm Giá -->
    <div class="modal fade" id="campaignDetailModal" tabindex="-1" aria-hidden="true" ref="campaignDetailModalRef">
      <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content border-0 shadow-lg rounded-4">
          <div class="modal-header border-bottom bg-light px-4 py-3">
            <h5 class="modal-title fw-bold text-dark d-flex align-items-center gap-2">
              <i class="bi bi-info-circle-fill text-info"></i>
              <span>Chi Tiết Đợt Giảm Giá: {{ viewingCampaign?.tenDotGiamGia }}</span>
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body p-4">
            <div class="row g-2 mb-3 bg-light p-3 rounded-3 small">
              <div class="col-sm-6"><strong>Mã đợt:</strong> <code class="fs-6">{{ viewingCampaign?.maDotGiamGia }}</code></div>
              <div class="col-sm-6"><strong>Tình trạng:</strong> <StatusBadge :status="viewingCampaign?.tinhTrang || ''" /></div>
              <div class="col-sm-6"><strong>Bắt đầu:</strong> {{ formatDate(viewingCampaign?.ngayBatDau) }}</div>
              <div class="col-sm-6"><strong>Kết thúc:</strong> {{ formatDate(viewingCampaign?.ngayKetThuc) }}</div>
              <div class="col-12" v-if="viewingCampaign?.dieuKienApDung"><strong>Điều kiện:</strong> {{ viewingCampaign.dieuKienApDung }}</div>
            </div>

            <h6 class="fw-bold mb-2">Sản Phẩm Áo Khoác Gió Trong Đợt ({{ viewingCampaign?.danhSachChiTiet?.length || 0 }} biến thể)</h6>
            <div class="table-responsive" style="max-height: 280px; overflow-y: auto;">
              <table class="table table-sm table-hover align-middle mb-0">
                <thead class="table-light text-muted small sticky-top">
                  <tr>
                    <th>Mã SPCT</th>
                    <th>Tên Sản Phẩm</th>
                    <th>Màu / Size</th>
                    <th>Giá Gốc</th>
                    <th>% Giảm</th>
                    <th>Giá Sau Giảm</th>
                    <th>SL Ưu Đãi</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in viewingCampaign?.danhSachChiTiet" :key="item.id">
                    <td><span class="badge bg-light text-dark border font-monospace">{{ item.maSpct }}</span></td>
                    <td class="fw-semibold text-dark">{{ item.tenSanPham }}</td>
                    <td>
                      <span class="badge bg-secondary-subtle text-secondary me-1">{{ item.tenMauSac }}</span>
                      <span class="badge bg-secondary-subtle text-secondary">{{ item.tenKichCo }}</span>
                    </td>
                    <td class="text-muted text-decoration-line-through">{{ formatCurrency(item.giaGoc) }}</td>
                    <td><span class="badge bg-danger text-white">-{{ item.phanTramGiam }}%</span></td>
                    <td class="fw-bold text-success">{{ formatCurrency(item.giaSauGiam) }}</td>
                    <td><span class="badge bg-light text-dark border">{{ item.soLuong }}</span></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div class="modal-footer border-top bg-light">
            <button type="button" class="btn btn-secondary btn-sm px-3 rounded-3" data-bs-dismiss="modal">Đóng</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Confirm Dialog -->
    <ConfirmDialog
      v-model="confirmDialog.show"
      :title="confirmDialog.title"
      :message="confirmDialog.message"
      :targetName="confirmDialog.targetName"
      :type="confirmDialog.type"
      :confirmText="confirmDialog.confirmText"
      :cancelText="confirmDialog.cancelText"
      :loading="confirmDialog.loading"
      @confirm="onConfirmDialogAction"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import { discountService } from '@/services/api'
import { useToast } from '@/composables/useToast'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import AppPagination from '@/components/common/AppPagination.vue'
import FilterBar from '@/components/common/FilterBar.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import LoadingState from '@/components/common/LoadingState.vue'

const toast = useToast()

const campaigns = ref([])
const loading = ref(false)
const submitting = ref(false)
const errorMessage = ref('')
const totalElements = ref(0)
const totalPages = ref(0)
const page = ref(0)
const size = ref(10)

const filters = reactive({
  keyword: '',
  trangThai: '',
  tuNgay: '',
  denNgay: ''
})

const activeCount = computed(() => campaigns.value.filter(c => c.trangThai === 1 && c.tinhTrang === 'DANG_DIEN_RA').length)
const upcomingCount = computed(() => campaigns.value.filter(c => c.tinhTrang === 'SAP_DIEN_RA').length)
const endedCount = computed(() => campaigns.value.filter(c => c.trangThai === 0 || c.tinhTrang === 'DA_KET_THUC').length)

const campaignFormModalRef = ref(null)
const productPickerModalRef = ref(null)
const campaignDetailModalRef = ref(null)
let formModalInstance = null
let productPickerModalInstance = null
let detailModalInstance = null

const isEditMode = ref(false)
const currentCampaignId = ref(null)
const form = reactive({
  maDotGiamGia: '',
  tenDotGiamGia: '',
  moTa: '',
  dieuKienApDung: '',
  ngayBatDau: '',
  ngayKetThuc: '',
  trangThai: 1,
  selectedItems: []
})
const errors = reactive({})

const spctList = ref([])
const spctLoading = ref(false)
const spctSearchKw = ref('')
const spctPage = ref(0)
const spctTotalPages = ref(0)
const defaultDiscountPercent = ref(15)
const pickerSelectedSpctMap = reactive(new Map())
let spctDebounceTimer = null

const viewingCampaign = ref(null)

const confirmDialog = reactive({
  show: false,
  actionType: '',
  title: '',
  message: '',
  targetName: '',
  type: 'warning',
  confirmText: 'Xác nhận',
  cancelText: 'Hủy',
  loading: false,
  payload: null
})

async function loadCampaigns() {
  loading.value = true
  errorMessage.value = ''
  try {
    const params = {
      page: page.value,
      size: size.value
    }
    if (filters.keyword && filters.keyword.trim()) params.keyword = filters.keyword.trim()
    if (filters.trangThai !== '') params.trangThai = parseInt(filters.trangThai, 10)
    if (filters.tuNgay) params.tuNgay = `${filters.tuNgay}T00:00:00`
    if (filters.denNgay) params.denNgay = `${filters.denNgay}T23:59:59`

    const res = await discountService.getCampaigns(params)
    if (res && res.success && res.data) {
      campaigns.value = res.data.content || []
      totalElements.value = res.data.totalElements || 0
      totalPages.value = res.data.totalPages || 0
    } else {
      errorMessage.value = res.message || 'Không thể tải danh sách đợt giảm giá'
    }
  } catch (err) {
    errorMessage.value = err.message || 'Lỗi kết nối máy chủ'
    toast.error(errorMessage.value, 'Lỗi nạp dữ liệu')
  } finally {
    loading.value = false
  }
}

function onSearch(kw) {
  filters.keyword = kw
  page.value = 0
  loadCampaigns()
}

function onFilterChange() {
  page.value = 0
  loadCampaigns()
}

function resetFilters() {
  filters.keyword = ''
  filters.trangThai = ''
  filters.tuNgay = ''
  filters.denNgay = ''
  page.value = 0
  loadCampaigns()
  toast.info('Đã đặt lại toàn bộ bộ lọc', 'Thông báo')
}

function onPageChange(newPage) {
  page.value = newPage
  loadCampaigns()
}

function onSizeChange(newSize) {
  size.value = newSize
  page.value = 0
  loadCampaigns()
}

function resetForm() {
  form.maDotGiamGia = ''
  form.tenDotGiamGia = ''
  form.moTa = ''
  form.dieuKienApDung = ''
  form.trangThai = 1
  form.selectedItems = []

  const now = new Date()
  const sevenDaysLater = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000)
  form.ngayBatDau = formatDateTimeLocal(now)
  form.ngayKetThuc = formatDateTimeLocal(sevenDaysLater)

  clearErrors()
}

function clearErrors() {
  Object.keys(errors).forEach(key => delete errors[key])
}

function validateForm() {
  clearErrors()
  let valid = true

  if (!form.tenDotGiamGia || !form.tenDotGiamGia.trim()) {
    errors.tenDotGiamGia = 'Tên đợt giảm giá không được để trống'
    valid = false
  }

  if (!form.ngayBatDau) {
    errors.ngayBatDau = 'Ngày bắt đầu không được để trống'
    valid = false
  }

  if (!form.ngayKetThuc) {
    errors.ngayKetThuc = 'Ngày kết thúc không được để trống'
    valid = false
  }

  if (form.ngayBatDau && form.ngayKetThuc) {
    const start = new Date(form.ngayBatDau)
    const end = new Date(form.ngayKetThuc)
    if (end < start) {
      errors.ngayKetThuc = 'Ngày kết thúc phải diễn ra sau ngày bắt đầu'
      valid = false
    }
  }

  if (form.selectedItems.length === 0) {
    errors.selectedItems = 'Đợt giảm giá phải có ít nhất 1 sản phẩm biến thể áo khoác gió'
    valid = false
  }

  return valid
}

function openCreateModal() {
  isEditMode.value = false
  currentCampaignId.value = null
  resetForm()
  formModalInstance.show()
}

async function openEditModal(c) {
  isEditMode.value = true
  currentCampaignId.value = c.id
  resetForm()

  try {
    const res = await discountService.getCampaignById(c.id)
    if (res.success && res.data) {
      const d = res.data
      form.maDotGiamGia = d.maDotGiamGia
      form.tenDotGiamGia = d.tenDotGiamGia
      form.moTa = d.moTa || ''
      form.dieuKienApDung = d.dieuKienApDung || ''
      form.ngayBatDau = formatDateTimeLocal(new Date(d.ngayBatDau))
      form.ngayKetThuc = formatDateTimeLocal(new Date(d.ngayKetThuc))
      form.trangThai = d.trangThai

      form.selectedItems = (d.danhSachChiTiet || []).map(item => ({
        idSanPhamChiTiet: item.idSanPhamChiTiet,
        maSpct: item.maSpct,
        tenSanPham: item.tenSanPham,
        tenMauSac: item.tenMauSac,
        tenKichCo: item.tenKichCo,
        giaGoc: item.giaGoc,
        phanTramGiam: item.phanTramGiam,
        giaSauGiam: item.giaSauGiam,
        soLuong: item.soLuong,
        soLuongTonHienTai: item.soLuongTonHienTai
      }))
    }
    formModalInstance.show()
  } catch (err) {
    toast.error('Không thể lấy chi tiết đợt: ' + (err.message || 'Lỗi server'))
  }
}

function handleCloseFormModal() {
  formModalInstance.hide()
}

async function submitForm() {
  if (!validateForm()) {
    toast.warning('Vui lòng kiểm tra lại các trường thông tin!', 'Dữ liệu chưa hợp lệ')
    return
  }

  submitting.value = true
  try {
    const payload = {
      maDotGiamGia: form.maDotGiamGia ? form.maDotGiamGia.trim().toUpperCase() : null,
      tenDotGiamGia: form.tenDotGiamGia.trim(),
      moTa: form.moTa,
      dieuKienApDung: form.dieuKienApDung,
      ngayBatDau: form.ngayBatDau,
      ngayKetThuc: form.ngayKetThuc,
      trangThai: form.trangThai,
      danhSachChiTiet: form.selectedItems.map(item => ({
        idSanPhamChiTiet: item.idSanPhamChiTiet,
        phanTramGiam: item.phanTramGiam,
        soLuong: item.soLuong
      }))
    }

    if (isEditMode.value) {
      await discountService.updateCampaign(currentCampaignId.value, payload)
      toast.success('Cập nhật đợt giảm giá thành công!')
    } else {
      await discountService.createCampaign(payload)
      toast.success('Tạo đợt giảm giá mới thành công!')
    }

    formModalInstance.hide()
    loadCampaigns()
  } catch (err) {
    toast.error(err.message || 'Không thể lưu đợt giảm giá', 'Lỗi lưu dữ liệu')
  } finally {
    submitting.value = false
  }
}

function recalculateDiscount(item) {
  let percent = item.phanTramGiam || 0
  if (percent > 99) percent = 99
  if (percent < 1) percent = 1
  item.phanTramGiam = percent

  const original = item.giaGoc || 0
  item.giaSauGiam = Math.round(original * (100 - percent) / 100)
}

function removeSelectedItem(idx, item) {
  form.selectedItems.splice(idx, 1)
  toast.info(`Đã gỡ biến thể '${item.maSpct}' khỏi đợt`)
}

function openProductPicker() {
  pickerSelectedSpctMap.clear()
  spctPage.value = 0
  spctSearchKw.value = ''
  loadSpctList()
  productPickerModalInstance.show()
}

async function loadSpctList() {
  spctLoading.value = true
  try {
    const params = {
      page: spctPage.value,
      size: 10
    }
    if (spctSearchKw.value.trim()) {
      params.keyword = spctSearchKw.value.trim()
    }
    const res = await discountService.getSanPhamChiTiet(params)
    if (res.success && res.data) {
      spctList.value = res.data.content || []
      spctTotalPages.value = res.data.totalPages || 0
    }
  } catch (err) {
    toast.error('Không thể tải danh sách áo khoác gió')
  } finally {
    spctLoading.value = false
  }
}

function onSpctSearchInput() {
  if (spctDebounceTimer) clearTimeout(spctDebounceTimer)
  spctDebounceTimer = setTimeout(() => {
    spctPage.value = 0
    loadSpctList()
  }, 300)
}

function changeSpctPage(p) {
  spctPage.value = p
  loadSpctList()
}

function toggleSelectSpct(s) {
  if (pickerSelectedSpctMap.has(s.id)) {
    pickerSelectedSpctMap.delete(s.id)
  } else {
    pickerSelectedSpctMap.set(s.id, s)
  }
}

const isAllSpctOnPageSelected = computed(() => {
  if (spctList.value.length === 0) return false
  return spctList.value.every(s => pickerSelectedSpctMap.has(s.id))
})

function toggleSelectAllSpctOnPage() {
  if (isAllSpctOnPageSelected.value) {
    spctList.value.forEach(s => pickerSelectedSpctMap.delete(s.id))
  } else {
    spctList.value.forEach(s => pickerSelectedSpctMap.set(s.id, s))
  }
}

function applySelectedSpctToForm() {
  let countAdded = 0
  const defaultPercent = defaultDiscountPercent.value || 15

  pickerSelectedSpctMap.forEach(s => {
    const exists = form.selectedItems.some(item => item.idSanPhamChiTiet === s.id)
    if (!exists) {
      const giaGoc = s.giaBan || 0
      const giaSauGiam = Math.round(giaGoc * (100 - defaultPercent) / 100)
      form.selectedItems.push({
        idSanPhamChiTiet: s.id,
        maSpct: s.maSpct,
        tenSanPham: s.tenSanPham,
        tenMauSac: s.tenMauSac,
        tenKichCo: s.tenKichCo,
        giaGoc: giaGoc,
        phanTramGiam: defaultPercent,
        giaSauGiam: giaSauGiam,
        soLuong: Math.min(s.soLuongTon || 10, 50),
        soLuongTonHienTai: s.soLuongTon
      })
      countAdded++
    }
  })

  productPickerModalInstance.hide()
  toast.success(`Đã thêm ${countAdded} biến thể áo khoác vào đợt giảm giá!`)
}

async function openDetailModal(c) {
  try {
    const res = await discountService.getCampaignById(c.id)
    if (res.success && res.data) {
      viewingCampaign.value = res.data
      detailModalInstance.show()
    }
  } catch (err) {
    toast.error('Không thể lấy chi tiết đợt giảm giá')
  }
}

function confirmToggleStatus(campaign) {
  const isDeactivating = campaign.trangThai === 1
  confirmDialog.actionType = 'toggle-status'
  confirmDialog.title = isDeactivating ? 'Tạm ngưng đợt giảm giá' : 'Kích hoạt lại đợt giảm giá'
  confirmDialog.message = isDeactivating
    ? `Bạn có chắc muốn tạm ngưng đợt giảm giá này? Các sản phẩm trong đợt sẽ trở về giá bán gốc.`
    : `Kích hoạt lại chiến dịch giảm giá này để áp dụng giá ưu đãi cho khách mua?`
  confirmDialog.targetName = `${campaign.maDotGiamGia} — ${campaign.tenDotGiamGia}`
  confirmDialog.type = isDeactivating ? 'warning' : 'success'
  confirmDialog.confirmText = isDeactivating ? 'Ngưng áp dụng' : 'Kích hoạt'
  confirmDialog.cancelText = 'Hủy bỏ'
  confirmDialog.loading = false
  confirmDialog.payload = campaign
  confirmDialog.show = true
}

async function onConfirmDialogAction() {
  if (confirmDialog.actionType === 'toggle-status') {
    const c = confirmDialog.payload
    confirmDialog.loading = true
    try {
      await discountService.toggleCampaignStatus(c.id)
      confirmDialog.show = false
      toast.success(`Đã ${c.trangThai === 1 ? 'tạm ngưng' : 'kích hoạt lại'} đợt ${c.maDotGiamGia}!`)
      loadCampaigns()
    } catch (err) {
      toast.error(err.message || 'Lỗi cập nhật trạng thái')
    } finally {
      confirmDialog.loading = false
    }
  }
}

function copyCode(code) {
  navigator.clipboard.writeText(code)
  toast.info(`Đã sao chép mã '${code}' vào clipboard!`)
}

function formatCurrency(val) {
  if (!val && val !== 0) return '0 đ'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}

function formatDate(dateStr) {
  if (!dateStr) return '---'
  const d = new Date(dateStr)
  return d.toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function formatDateTimeLocal(date) {
  const pad = n => (n < 10 ? '0' + n : n)
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`
}

onMounted(() => {
  formModalInstance = new Modal(campaignFormModalRef.value)
  productPickerModalInstance = new Modal(productPickerModalRef.value)
  detailModalInstance = new Modal(campaignDetailModalRef.value)
  loadCampaigns()
})
</script>

<style scoped>
.campaign-management-page {
  animation: fadeIn 0.25s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(4px); }
  to { opacity: 1; transform: translateY(0); }
}

.custom-table th {
  font-weight: 600;
  border-bottom: 2px solid #e2e8f0;
  padding: 0.75rem 1rem;
}

.custom-table td {
  padding: 0.85rem 1rem;
  border-bottom: 1px solid #f1f5f9;
}
</style>

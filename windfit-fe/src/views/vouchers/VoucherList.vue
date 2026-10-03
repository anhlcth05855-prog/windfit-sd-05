<template>
  <div class="voucher-management-page">
    <!-- Header -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
      <div>
        <h4 class="fw-bold mb-1 d-flex align-items-center gap-2">
          <i class="bi bi-ticket-perforated-fill text-primary"></i>
          <span>Quản Lý Phiếu Giảm Giá (Voucher)</span>
          <span class="badge bg-primary-subtle text-primary rounded-pill fs-6 px-3 py-1">{{ totalElements }} phiếu</span>
        </h4>
        <p class="text-muted mb-0 small">
          Tạo và quản lý các mã coupon ưu đãi, chiết khấu theo đơn hàng tại quầy và online WindFit
        </p>
      </div>
      <div class="d-flex align-items-center gap-2">
        <button class="btn btn-outline-dark d-flex align-items-center gap-2 px-3 shadow-sm rounded-3 btn-sm" @click="openSimulatorModal">
          <i class="bi bi-calculator"></i>
          <span>Thử Nghiệm Áp Mã</span>
        </button>
        <button class="btn btn-primary d-flex align-items-center gap-2 px-3 shadow-sm fw-medium rounded-3 btn-sm" @click="openCreateModal">
          <i class="bi bi-plus-circle-fill"></i>
          <span>Tạo Voucher Mới</span>
        </button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="row g-3 mb-4">
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Tổng số voucher</div>
              <div class="fs-4 fw-bold text-dark mt-1">{{ totalElements }}</div>
            </div>
            <div class="stat-icon bg-primary-subtle text-primary rounded-3 d-flex align-items-center justify-content-center" style="width: 44px; height: 44px;">
              <i class="bi bi-ticket-perforated-fill fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Đang áp dụng</div>
              <div class="fs-4 fw-bold text-success mt-1">{{ activeCount }}</div>
            </div>
            <div class="stat-icon bg-success-subtle text-success rounded-3 d-flex align-items-center justify-content-center" style="width: 44px; height: 44px;">
              <i class="bi bi-broadcast fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Lượt đã sử dụng</div>
              <div class="fs-4 fw-bold text-info mt-1">{{ totalUsedCount }}</div>
            </div>
            <div class="stat-icon bg-info-subtle text-info rounded-3 d-flex align-items-center justify-content-center" style="width: 44px; height: 44px;">
              <i class="bi bi-check2-all fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Đã khóa / Tạm ngưng</div>
              <div class="fs-4 fw-bold text-secondary mt-1">{{ lockedCount }}</div>
            </div>
            <div class="stat-icon bg-secondary-subtle text-secondary rounded-3 d-flex align-items-center justify-content-center" style="width: 44px; height: 44px;">
              <i class="bi bi-lock-fill fs-4"></i>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Filter Bar (Component dùng chung) -->
    <FilterBar
      v-model:keyword="filters.keyword"
      searchPlaceholder="Tìm theo mã voucher, tên chương trình..."
      @search="onSearch"
      @reset="resetFilters"
    >
      <!-- Filter Loại giảm giá -->
      <div class="col-6 col-md-auto">
        <select class="form-select form-select-sm bg-light" v-model="filters.loaiGiamGia" @change="onFilterChange">
          <option value="">-- Tất cả loại --</option>
          <option value="PHAN_TRAM">Phần trăm (%)</option>
          <option value="TIEN_MAT">Tiền mặt (VNĐ)</option>
        </select>
      </div>

      <!-- Filter Trạng thái -->
      <div class="col-6 col-md-auto">
        <select class="form-select form-select-sm bg-light" v-model="filters.trangThai" @change="onFilterChange">
          <option value="">-- Tất cả trạng thái --</option>
          <option value="1">Đang hoạt động</option>
          <option value="0">Tạm khóa / Ngưng</option>
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
      <LoadingState v-if="loading" text="Đang tải danh sách phiếu giảm giá..." />

      <!-- Error State -->
      <div v-else-if="errorMessage" class="text-center p-5 rounded-4 bg-light border border-danger my-3">
        <i class="bi bi-exclamation-triangle-fill text-danger fs-1 mb-2"></i>
        <h6 class="fw-bold text-dark">Không thể tải dữ liệu</h6>
        <p class="text-danger small mb-3">{{ errorMessage }}</p>
        <button class="btn btn-outline-primary btn-sm px-3 rounded-3" @click="loadVouchers">
          <i class="bi bi-arrow-clockwise me-1"></i> Thử lại
        </button>
      </div>

      <!-- Empty State -->
      <EmptyState
        v-else-if="vouchers.length === 0"
        title="Không tìm thấy phiếu giảm giá"
        description="Không có phiếu giảm giá nào phù hợp với bộ lọc hiện tại. Thử đặt lại bộ lọc hoặc tạo phiếu mới."
      >
        <template #action>
          <button class="btn btn-primary btn-sm rounded-3 px-3" @click="openCreateModal">
            <i class="bi bi-plus-circle me-1"></i> Tạo Phiếu Mới
          </button>
        </template>
      </EmptyState>

      <!-- Data Table -->
      <div v-else class="table-responsive">
        <table class="table table-hover align-middle mb-0 custom-table">
          <thead class="table-light text-secondary small text-uppercase">
            <tr>
              <th style="width: 50px;">#</th>
              <th style="width: 140px;">Mã Voucher</th>
              <th>Tên Chương Trình</th>
              <th>Loại</th>
              <th>Mức Ưu Đãi</th>
              <th>Đơn Tối Thiểu</th>
              <th style="width: 160px;">Tiến Độ Dùng</th>
              <th>Thời Gian Hiệu Lực</th>
              <th class="text-center">Tình Trạng</th>
              <th class="text-center" style="width: 140px;">Thao Tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(v, idx) in vouchers" :key="v.id">
              <td class="text-muted small">{{ page * size + idx + 1 }}</td>
              <td>
                <div class="d-flex align-items-center gap-1">
                  <span class="badge bg-primary text-white font-monospace fs-6 px-2 py-1 shadow-sm">{{ v.maPhieuGiamGia }}</span>
                  <button class="btn btn-sm btn-link text-muted p-0" @click="copyCode(v.maPhieuGiamGia)" title="Copy mã">
                    <i class="bi bi-clipboard"></i>
                  </button>
                </div>
              </td>
              <td>
                <div class="fw-bold text-dark">{{ v.tenPhieuGiamGia }}</div>
              </td>
              <td>
                <StatusBadge :status="v.loaiGiamGia" />
              </td>
              <td>
                <div class="fw-bold text-success">
                  <span v-if="v.loaiGiamGia === 'PHAN_TRAM'">
                    Giảm {{ v.giaTriGiam }}%
                    <small class="text-muted d-block" v-if="v.giamToiDa">(Tối đa {{ formatCurrency(v.giamToiDa) }})</small>
                  </span>
                  <span v-else>
                    -{{ formatCurrency(v.giaTriGiam) }}
                  </span>
                </div>
              </td>
              <td>
                <span class="fw-semibold text-dark">{{ formatCurrency(v.donToiThieu) }}</span>
              </td>
              <td>
                <div class="d-flex justify-content-between small text-muted mb-1">
                  <span>{{ v.soLuongDaDung }} / {{ v.soLuong }}</span>
                  <span>{{ Math.round((v.soLuongDaDung / (v.soLuong || 1)) * 100) }}%</span>
                </div>
                <div class="progress" style="height: 6px;">
                  <div
                    class="progress-bar"
                    :class="v.soLuongDaDung >= v.soLuong ? 'bg-danger' : 'bg-primary'"
                    :style="{ width: Math.min(100, Math.round((v.soLuongDaDung / (v.soLuong || 1)) * 100)) + '%' }"
                  ></div>
                </div>
              </td>
              <td>
                <div class="small text-muted">
                  <div><i class="bi bi-calendar-event me-1"></i>Từ: {{ formatDate(v.ngayBatDau) }}</div>
                  <div><i class="bi bi-calendar-x me-1"></i>Đến: {{ formatDate(v.ngayKetThuc) }}</div>
                </div>
              </td>
              <td class="text-center">
                <StatusBadge :status="v.tinhTrang" />
              </td>
              <td class="text-center">
                <div class="btn-group btn-group-sm">
                  <!-- Gán Khách Hàng -->
                  <button class="btn btn-outline-info" @click="openCustomerAssignModal(v)" title="Gán khách hàng cá nhân">
                    <i class="bi bi-people"></i>
                  </button>
                  <!-- Sửa -->
                  <button class="btn btn-outline-secondary" @click="openEditModal(v)" title="Chỉnh sửa">
                    <i class="bi bi-pencil"></i>
                  </button>
                  <!-- Đổi trạng thái (Soft Delete / Reactive) -->
                  <button
                    class="btn"
                    :class="v.trangThai === 1 ? 'btn-outline-danger' : 'btn-outline-success'"
                    @click="confirmToggleStatus(v)"
                    :title="v.trangThai === 1 ? 'Tạm ngưng voucher' : 'Kích hoạt lại'"
                  >
                    <i :class="v.trangThai === 1 ? 'bi bi-slash-circle' : 'bi bi-arrow-repeat'"></i>
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

    <!-- MODAL 1: CREATE / EDIT VOUCHER -->
    <div class="modal fade" id="voucherFormModal" tabindex="-1" aria-hidden="true" ref="voucherFormModalRef">
      <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content border-0 shadow-lg rounded-4">
          <div class="modal-header border-bottom bg-light px-4 py-3">
            <h5 class="modal-title fw-bold text-dark d-flex align-items-center gap-2">
              <i class="bi" :class="isEditMode ? 'bi-pencil-square text-warning' : 'bi-plus-circle-fill text-primary'"></i>
              <span>{{ isEditMode ? 'Chỉnh Sửa Phiếu Giảm Giá' : 'Tạo Phiếu Giảm Giá Mới' }}</span>
            </h5>
            <button type="button" class="btn-close" @click="handleCloseFormModal" aria-label="Close"></button>
          </div>
          <div class="modal-body p-4">
            <form @submit.prevent="submitForm">
              <div class="row g-3">
                <!-- Mã Voucher -->
                <div class="col-md-6">
                  <label class="form-label small fw-semibold">Mã Phiếu Giảm Giá</label>
                  <input
                    type="text"
                    v-model="form.maPhieuGiamGia"
                    class="form-control font-monospace text-uppercase"
                    :class="{ 'is-invalid': errors.maPhieuGiamGia }"
                    placeholder="Để trống hệ thống tự sinh mã (VD: WF12345)"
                  />
                  <div class="invalid-feedback" v-if="errors.maPhieuGiamGia">{{ errors.maPhieuGiamGia }}</div>
                </div>

                <!-- Tên Voucher -->
                <div class="col-md-6">
                  <label class="form-label small fw-semibold">Tên Chương Trình <span class="text-danger">*</span></label>
                  <input
                    type="text"
                    v-model="form.tenPhieuGiamGia"
                    class="form-control"
                    :class="{ 'is-invalid': errors.tenPhieuGiamGia }"
                    placeholder="VD: Tri ân khách hàng tháng 10"
                  />
                  <div class="invalid-feedback" v-if="errors.tenPhieuGiamGia">{{ errors.tenPhieuGiamGia }}</div>
                </div>

                <!-- Loại giảm giá -->
                <div class="col-md-6">
                  <label class="form-label small fw-semibold">Hình Thức Giảm <span class="text-danger">*</span></label>
                  <select v-model="form.loaiGiamGia" class="form-select" :class="{ 'is-invalid': errors.loaiGiamGia }">
                    <option value="PHAN_TRAM">Giảm theo Phần Trăm (%)</option>
                    <option value="TIEN_MAT">Giảm theo Số Tiền Cố Định (VNĐ)</option>
                  </select>
                  <div class="invalid-feedback" v-if="errors.loaiGiamGia">{{ errors.loaiGiamGia }}</div>
                </div>

                <!-- Giá trị giảm -->
                <div class="col-md-6">
                  <label class="form-label small fw-semibold">
                    Mức Giảm <span class="text-danger">*</span>
                    <span v-if="form.loaiGiamGia === 'PHAN_TRAM'">(%)</span>
                    <span v-else>(VNĐ)</span>
                  </label>
                  <div class="input-group">
                    <input
                      type="number"
                      v-model.number="form.giaTriGiam"
                      class="form-control"
                      :class="{ 'is-invalid': errors.giaTriGiam }"
                      :min="form.loaiGiamGia === 'PHAN_TRAM' ? 1 : 1000"
                      :max="form.loaiGiamGia === 'PHAN_TRAM' ? 100 : undefined"
                      step="any"
                    />
                    <span class="input-group-text">{{ form.loaiGiamGia === 'PHAN_TRAM' ? '%' : 'đ' }}</span>
                  </div>
                  <div class="invalid-feedback d-block" v-if="errors.giaTriGiam">{{ errors.giaTriGiam }}</div>
                </div>

                <!-- Giảm tối đa -->
                <div class="col-md-6" v-if="form.loaiGiamGia === 'PHAN_TRAM'">
                  <label class="form-label small fw-semibold">Giảm Tối Đa (VNĐ)</label>
                  <div class="input-group">
                    <input
                      type="number"
                      v-model.number="form.giamToiDa"
                      class="form-control"
                      placeholder="Không giới hạn"
                      step="1000"
                      min="0"
                    />
                    <span class="input-group-text">đ</span>
                  </div>
                </div>

                <!-- Đơn tối thiểu -->
                <div class="col-md-6">
                  <label class="form-label small fw-semibold">Giá Trị Đơn Tối Thiểu <span class="text-danger">*</span></label>
                  <div class="input-group">
                    <input
                      type="number"
                      v-model.number="form.donToiThieu"
                      class="form-control"
                      :class="{ 'is-invalid': errors.donToiThieu }"
                      step="50000"
                      min="0"
                    />
                    <span class="input-group-text">đ</span>
                  </div>
                  <div class="invalid-feedback d-block" v-if="errors.donToiThieu">{{ errors.donToiThieu }}</div>
                </div>

                <!-- Số lượng -->
                <div class="col-md-6">
                  <label class="form-label small fw-semibold">Tổng Số Lượt Áp Dụng <span class="text-danger">*</span></label>
                  <input
                    type="number"
                    v-model.number="form.soLuong"
                    class="form-control"
                    :class="{ 'is-invalid': errors.soLuong }"
                    min="1"
                  />
                  <div class="invalid-feedback" v-if="errors.soLuong">{{ errors.soLuong }}</div>
                </div>

                <!-- Trạng thái -->
                <div class="col-md-6">
                  <label class="form-label small fw-semibold">Trạng Thái Ban Đầu</label>
                  <select v-model="form.trangThai" class="form-select">
                    <option :value="1">Kích hoạt ngay</option>
                    <option :value="0">Tạm khóa / Ẩn</option>
                  </select>
                </div>

                <!-- Ngày bắt đầu -->
                <div class="col-md-6">
                  <label class="form-label small fw-semibold">Ngày & Giờ Bắt Đầu <span class="text-danger">*</span></label>
                  <input
                    type="datetime-local"
                    v-model="form.ngayBatDau"
                    class="form-control"
                    :class="{ 'is-invalid': errors.ngayBatDau }"
                  />
                  <div class="invalid-feedback" v-if="errors.ngayBatDau">{{ errors.ngayBatDau }}</div>
                </div>

                <!-- Ngày kết thúc -->
                <div class="col-md-6">
                  <label class="form-label small fw-semibold">Ngày & Giờ Kết Thúc <span class="text-danger">*</span></label>
                  <input
                    type="datetime-local"
                    v-model="form.ngayKetThuc"
                    class="form-control"
                    :class="{ 'is-invalid': errors.ngayKetThuc }"
                  />
                  <div class="invalid-feedback" v-if="errors.ngayKetThuc">{{ errors.ngayKetThuc }}</div>
                </div>
              </div>

              <!-- Form Buttons -->
              <div class="d-flex justify-content-end gap-2 mt-4 pt-3 border-top">
                <button type="button" class="btn btn-light px-4 border rounded-3" @click="handleCloseFormModal">
                  Hủy
                </button>
                <button type="submit" class="btn btn-primary px-4 fw-medium shadow-sm rounded-3" :disabled="submitting">
                  <span class="spinner-border spinner-border-sm me-1" v-if="submitting"></span>
                  <span>{{ isEditMode ? 'Lưu Thay Đổi' : 'Tạo Phiếu' }}</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- MODAL 2: VOUCHER SIMULATOR -->
    <div class="modal fade" id="simulatorModal" tabindex="-1" aria-hidden="true" ref="simulatorModalRef">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
          <div class="modal-header border-bottom bg-light px-4 py-3">
            <h5 class="modal-title fw-bold text-dark d-flex align-items-center gap-2">
              <i class="bi bi-calculator text-primary"></i>
              <span>Thử Nghiệm Tính Toán Áp Voucher</span>
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body p-4">
            <div class="mb-3">
              <label class="form-label small fw-semibold">Nhập mã voucher</label>
              <input
                type="text"
                v-model="sim.code"
                class="form-control font-monospace text-uppercase"
                placeholder="VD: WINDFIT10"
              />
            </div>
            <div class="mb-3">
              <label class="form-label small fw-semibold">Giả lập tổng tiền đơn hàng (VNĐ)</label>
              <div class="input-group">
                <input
                  type="number"
                  v-model.number="sim.orderAmount"
                  class="form-control fw-bold"
                  step="50000"
                  min="0"
                />
                <span class="input-group-text">đ</span>
              </div>
            </div>

            <button class="btn btn-primary w-100 py-2 mb-3 fw-medium rounded-3" @click="runSimulation" :disabled="sim.loading">
              <span class="spinner-border spinner-border-sm me-1" v-if="sim.loading"></span>
              <span>Kiểm Tra & Tính Giảm Giá</span>
            </button>

            <!-- Sim Result -->
            <div v-if="sim.result" class="card border rounded-4 p-3 bg-light">
              <div
                class="badge w-auto align-self-start px-2 py-1 mb-2"
                :class="sim.result.valid ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'"
              >
                {{ sim.result.valid ? 'Hợp lệ để áp dụng' : 'Không thể áp dụng' }}
              </div>
              <p class="small mb-2" :class="sim.result.valid ? 'text-dark' : 'text-danger fw-semibold'">
                {{ sim.result.message }}
              </p>

              <div v-if="sim.result.valid" class="border-top pt-2 mt-1 small">
                <div class="d-flex justify-content-between text-muted mb-1">
                  <span>Chương trình:</span>
                  <span class="fw-semibold text-dark">{{ sim.result.tenVoucher }}</span>
                </div>
                <div class="d-flex justify-content-between text-muted mb-1">
                  <span>Số tiền được giảm:</span>
                  <span class="fw-bold text-success">-{{ formatCurrency(sim.result.soTienGiam) }}</span>
                </div>
                <div class="d-flex justify-content-between text-muted pt-1 border-top">
                  <span>Khách cần trả:</span>
                  <span class="fw-bold text-primary fs-6">{{ formatCurrency(sim.result.thanhTienSauGiam) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- MODAL 3: GÁN KHÁCH HÀNG VOUCHER CÁ NHÂN -->
    <div class="modal fade" id="customerAssignModal" tabindex="-1" aria-hidden="true" ref="customerAssignModalRef">
      <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content border-0 shadow-lg rounded-4">
          <div class="modal-header border-bottom bg-light px-4 py-3">
            <h5 class="modal-title fw-bold text-dark d-flex align-items-center gap-2">
              <i class="bi bi-people-fill text-info"></i>
              <span>Gán Voucher Cho Khách Hàng Cá Nhân</span>
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body p-4">
            <div class="alert alert-info py-2 small mb-3">
              Voucher: <strong>{{ currentVoucherForCustomer?.tenPhieuGiamGia }}</strong> (Mã: <code>{{ currentVoucherForCustomer?.maPhieuGiamGia }}</code>)
            </div>

            <!-- Search customers with debounce -->
            <div class="input-group mb-3">
              <span class="input-group-text bg-light"><i class="bi bi-search"></i></span>
              <input
                type="text"
                class="form-control"
                placeholder="Tìm khách hàng theo tên, SĐT, email..."
                v-model="customerSearchKw"
                @input="onCustomerSearchInput"
              />
            </div>

            <!-- Customer list table -->
            <div class="table-responsive" style="max-height: 320px; overflow-y: auto;">
              <table class="table table-sm table-hover align-middle">
                <thead class="table-light text-muted small sticky-top">
                  <tr>
                    <th style="width: 40px;">
                      <input
                        type="checkbox"
                        class="form-check-input"
                        :checked="isAllCustomersOnPageSelected"
                        @change="toggleSelectAllCustomersOnPage"
                      />
                    </th>
                    <th>Mã KH</th>
                    <th>Họ Tên</th>
                    <th>Số Điện Thoại</th>
                    <th>Email</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="customersLoading">
                    <td colspan="5" class="text-center py-4 text-muted small">Đang tải khách hàng...</td>
                  </tr>
                  <tr v-else-if="customersList.length === 0">
                    <td colspan="5" class="text-center py-4 text-muted small">Không tìm thấy khách hàng nào.</td>
                  </tr>
                  <tr v-for="c in customersList" :key="c.id" @click="toggleSelectCustomer(c.id)" style="cursor: pointer;">
                    <td>
                      <input
                        type="checkbox"
                        class="form-check-input"
                        :checked="selectedCustomerIds.includes(c.id)"
                        @click.stop
                        @change="toggleSelectCustomer(c.id)"
                      />
                    </td>
                    <td><span class="badge bg-light text-dark border">{{ c.maKhachHang }}</span></td>
                    <td class="fw-semibold text-dark">{{ c.hoTen }}</td>
                    <td>{{ c.soDienThoai }}</td>
                    <td class="text-muted small">{{ c.email || '---' }}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Customer Pagination -->
            <div class="d-flex justify-content-between align-items-center mt-3 pt-2 border-top small text-muted">
              <span>Đã chọn: <strong class="text-primary">{{ selectedCustomerIds.length }}</strong> khách hàng</span>
              <div class="btn-group btn-group-sm">
                <button
                  class="btn btn-outline-secondary"
                  :disabled="customerPage === 0 || customersLoading"
                  @click="changeCustomerPage(customerPage - 1)"
                >
                  <i class="bi bi-chevron-left"></i>
                </button>
                <button class="btn btn-light disabled">
                  {{ customerPage + 1 }} / {{ customerTotalPages || 1 }}
                </button>
                <button
                  class="btn btn-outline-secondary"
                  :disabled="customerPage >= customerTotalPages - 1 || customersLoading"
                  @click="changeCustomerPage(customerPage + 1)"
                >
                  <i class="bi bi-chevron-right"></i>
                </button>
              </div>
            </div>

            <!-- Save Customer Assignments -->
            <div class="d-flex justify-content-end gap-2 mt-4 pt-3 border-top">
              <button type="button" class="btn btn-light px-4 border rounded-3" data-bs-dismiss="modal">Đóng</button>
              <button
                type="button"
                class="btn btn-primary px-4 fw-medium shadow-sm rounded-3"
                :disabled="savingCustomers"
                @click="saveCustomerAssignments"
              >
                <span class="spinner-border spinner-border-sm me-1" v-if="savingCustomers"></span>
                <span>Lưu Danh Sách Gán</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- CONFIRM DIALOG: Toggle Status & Form Discard -->
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

// Data States
const vouchers = ref([])
const loading = ref(false)
const submitting = ref(false)
const errorMessage = ref('')
const totalElements = ref(0)
const totalPages = ref(0)
const page = ref(0)
const size = ref(10)

// Filters
const filters = reactive({
  keyword: '',
  loaiGiamGia: '',
  trangThai: '',
  tuNgay: '',
  denNgay: ''
})

// Stats Computed
const activeCount = computed(() => vouchers.value.filter(v => v.trangThai === 1 && v.tinhTrang === 'DANG_DIEN_RA').length)
const totalUsedCount = computed(() => vouchers.value.reduce((acc, cur) => acc + (cur.soLuongDaDung || 0), 0))
const lockedCount = computed(() => vouchers.value.filter(v => v.trangThai === 0).length)

// Modal References
const voucherFormModalRef = ref(null)
const simulatorModalRef = ref(null)
const customerAssignModalRef = ref(null)
let formModalInstance = null
let simulatorModalInstance = null
let customerModalInstance = null

// Form States
const isEditMode = ref(false)
const currentVoucherId = ref(null)
const form = reactive({
  maPhieuGiamGia: '',
  tenPhieuGiamGia: '',
  loaiGiamGia: 'PHAN_TRAM',
  giaTriGiam: null,
  giamToiDa: null,
  donToiThieu: 0,
  soLuong: 100,
  ngayBatDau: '',
  ngayKetThuc: '',
  trangThai: 1
})
const errors = reactive({})
let isFormDirty = false

// Simulator States
const sim = reactive({
  code: '',
  orderAmount: 500000,
  loading: false,
  result: null
})

// Customer Assignment States
const currentVoucherForCustomer = ref(null)
const customersList = ref([])
const customersLoading = ref(false)
const customerSearchKw = ref('')
const customerPage = ref(0)
const customerTotalPages = ref(0)
const selectedCustomerIds = ref([])
const savingCustomers = ref(false)
let customerDebounceTimer = null

// Confirm Dialog State
const confirmDialog = reactive({
  show: false,
  actionType: '', // 'toggle-status' | 'discard-form' | 'save-form'
  title: '',
  message: '',
  targetName: '',
  type: 'warning',
  confirmText: 'Xác nhận',
  cancelText: 'Hủy',
  loading: false,
  payload: null
})

// ---------------------- LOAD VOUCHERS ----------------------
async function loadVouchers() {
  loading.value = true
  errorMessage.value = ''
  try {
    const params = {
      page: page.value,
      size: size.value
    }
    if (filters.keyword && filters.keyword.trim()) params.keyword = filters.keyword.trim()
    if (filters.loaiGiamGia) params.loaiGiamGia = filters.loaiGiamGia
    if (filters.trangThai !== '') params.trangThai = parseInt(filters.trangThai, 10)
    if (filters.tuNgay) params.tuNgay = `${filters.tuNgay}T00:00:00`
    if (filters.denNgay) params.denNgay = `${filters.denNgay}T23:59:59`

    const res = await discountService.getVouchers(params)
    if (res && res.success && res.data) {
      vouchers.value = res.data.content || []
      totalElements.value = res.data.totalElements || 0
      totalPages.value = res.data.totalPages || 0
    } else {
      errorMessage.value = res.message || 'Không thể tải danh sách phiếu giảm giá'
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
  loadVouchers()
}

function onFilterChange() {
  page.value = 0
  loadVouchers()
}

function resetFilters() {
  filters.keyword = ''
  filters.loaiGiamGia = ''
  filters.trangThai = ''
  filters.tuNgay = ''
  filters.denNgay = ''
  page.value = 0
  loadVouchers()
  toast.info('Đã đặt lại toàn bộ bộ lọc', 'Thông báo')
}

function onPageChange(newPage) {
  page.value = newPage
  loadVouchers()
}

function onSizeChange(newSize) {
  size.value = newSize
  page.value = 0
  loadVouchers()
}

// ---------------------- FORM VALIDATION & ACTIONS ----------------------
function resetForm() {
  form.maPhieuGiamGia = ''
  form.tenPhieuGiamGia = ''
  form.loaiGiamGia = 'PHAN_TRAM'
  form.giaTriGiam = null
  form.giamToiDa = null
  form.donToiThieu = 0
  form.soLuong = 100
  form.trangThai = 1

  const now = new Date()
  const sevenDaysLater = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000)
  form.ngayBatDau = formatDateTimeLocal(now)
  form.ngayKetThuc = formatDateTimeLocal(sevenDaysLater)

  clearErrors()
  isFormDirty = false
}

function clearErrors() {
  Object.keys(errors).forEach(key => delete errors[key])
}

function validateForm() {
  clearErrors()
  let valid = true

  if (!form.tenPhieuGiamGia || !form.tenPhieuGiamGia.trim()) {
    errors.tenPhieuGiamGia = 'Tên chương trình không được để trống'
    valid = false
  }

  if (!form.loaiGiamGia) {
    errors.loaiGiamGia = 'Vui lòng chọn loại giảm giá'
    valid = false
  }

  if (form.giaTriGiam === null || form.giaTriGiam === undefined || form.giaTriGiam <= 0) {
    errors.giaTriGiam = 'Mức giảm giá phải lớn hơn 0'
    valid = false
  } else if (form.loaiGiamGia === 'PHAN_TRAM' && form.giaTriGiam > 100) {
    errors.giaTriGiam = 'Phần trăm giảm giá tối đa là 100%'
    valid = false
  }

  if (form.donToiThieu === null || form.donToiThieu === undefined || form.donToiThieu < 0) {
    errors.donToiThieu = 'Đơn tối thiểu không được nhỏ hơn 0'
    valid = false
  }

  if (!form.soLuong || form.soLuong < 1) {
    errors.soLuong = 'Số lượng áp dụng tối thiểu là 1'
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
      errors.ngayKetThuc = 'Ngày kết thúc phải sau hoặc bằng ngày bắt đầu'
      valid = false
    }
  }

  return valid
}

function openCreateModal() {
  isEditMode.value = false
  currentVoucherId.value = null
  resetForm()
  formModalInstance.show()
}

async function openEditModal(v) {
  isEditMode.value = true
  currentVoucherId.value = v.id
  resetForm()

  try {
    const res = await discountService.getVoucherById(v.id)
    if (res.success && res.data) {
      const d = res.data
      form.maPhieuGiamGia = d.maPhieuGiamGia
      form.tenPhieuGiamGia = d.tenPhieuGiamGia
      form.loaiGiamGia = d.loaiGiamGia
      form.giaTriGiam = d.giaTriGiam
      form.giamToiDa = d.giamToiDa
      form.donToiThieu = d.donToiThieu
      form.soLuong = d.soLuong
      form.ngayBatDau = formatDateTimeLocal(new Date(d.ngayBatDau))
      form.ngayKetThuc = formatDateTimeLocal(new Date(d.ngayKetThuc))
      form.trangThai = d.trangThai
    }
    formModalInstance.show()
  } catch (err) {
    toast.error('Không thể lấy chi tiết phiếu: ' + (err.message || 'Lỗi server'))
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
      maPhieuGiamGia: form.maPhieuGiamGia ? form.maPhieuGiamGia.trim().toUpperCase() : null,
      tenPhieuGiamGia: form.tenPhieuGiamGia.trim(),
      loaiGiamGia: form.loaiGiamGia,
      giaTriGiam: form.giaTriGiam,
      giamToiDa: form.loaiGiamGia === 'PHAN_TRAM' ? form.giamToiDa : null,
      donToiThieu: form.donToiThieu,
      soLuong: form.soLuong,
      ngayBatDau: form.ngayBatDau,
      ngayKetThuc: form.ngayKetThuc,
      trangThai: form.trangThai
    }

    if (isEditMode.value) {
      await discountService.updateVoucher(currentVoucherId.value, payload)
      toast.success('Cập nhật phiếu giảm giá thành công!')
    } else {
      await discountService.createVoucher(payload)
      toast.success('Tạo phiếu giảm giá mới thành công!')
    }

    formModalInstance.hide()
    loadVouchers()
  } catch (err) {
    toast.error(err.message || 'Không thể lưu phiếu giảm giá', 'Lỗi lưu dữ liệu')
  } finally {
    submitting.value = false
  }
}

// ---------------------- TOGGLE STATUS (CONFIRM DIALOG) ----------------------
function confirmToggleStatus(voucher) {
  const isDeactivating = voucher.trangThai === 1
  confirmDialog.actionType = 'toggle-status'
  confirmDialog.title = isDeactivating ? 'Tạm ngưng phiếu giảm giá' : 'Kích hoạt lại phiếu giảm giá'
  confirmDialog.message = isDeactivating
    ? `Bạn có chắc chắn muốn ngưng áp dụng phiếu giảm giá này? Khách hàng sẽ không thể sử dụng mã nữa.`
    : `Kích hoạt lại phiếu giảm giá này để áp dụng cho khách mua hàng?`
  confirmDialog.targetName = `${voucher.maPhieuGiamGia} — ${voucher.tenPhieuGiamGia}`
  confirmDialog.type = isDeactivating ? 'warning' : 'success'
  confirmDialog.confirmText = isDeactivating ? 'Ngưng áp dụng' : 'Kích hoạt'
  confirmDialog.cancelText = 'Hủy bỏ'
  confirmDialog.loading = false
  confirmDialog.payload = voucher
  confirmDialog.show = true
}

async function onConfirmDialogAction() {
  if (confirmDialog.actionType === 'toggle-status') {
    const v = confirmDialog.payload
    confirmDialog.loading = true
    try {
      await discountService.toggleVoucherStatus(v.id)
      confirmDialog.show = false
      toast.success(`Đã ${v.trangThai === 1 ? 'tạm ngưng' : 'kích hoạt lại'} phiếu ${v.maPhieuGiamGia}!`)
      loadVouchers()
    } catch (err) {
      toast.error(err.message || 'Lỗi cập nhật trạng thái')
    } finally {
      confirmDialog.loading = false
    }
  }
}

// ---------------------- CUSTOMER ASSIGNMENT ----------------------
async function openCustomerAssignModal(voucher) {
  currentVoucherForCustomer.value = voucher
  customerPage.value = 0
  customerSearchKw.value = ''
  selectedCustomerIds.value = []

  try {
    const assignedRes = await discountService.getAssignedCustomers(voucher.id)
    if (assignedRes.success && assignedRes.data) {
      selectedCustomerIds.value = assignedRes.data || []
    }
  } catch (err) {
    console.error(err)
  }

  loadCustomers()
  customerModalInstance.show()
}

async function loadCustomers() {
  customersLoading.value = true
  try {
    const params = {
      page: customerPage.value,
      size: 10
    }
    if (customerSearchKw.value.trim()) {
      params.keyword = customerSearchKw.value.trim()
    }
    const res = await discountService.getCustomers(params)
    if (res.success && res.data) {
      customersList.value = res.data.content || []
      customerTotalPages.value = res.data.totalPages || 0
    }
  } catch (err) {
    toast.error('Không thể tải danh sách khách hàng')
  } finally {
    customersLoading.value = false
  }
}

function onCustomerSearchInput() {
  if (customerDebounceTimer) clearTimeout(customerDebounceTimer)
  customerDebounceTimer = setTimeout(() => {
    customerPage.value = 0
    loadCustomers()
  }, 300)
}

function changeCustomerPage(p) {
  customerPage.value = p
  loadCustomers()
}

function toggleSelectCustomer(id) {
  const idx = selectedCustomerIds.value.indexOf(id)
  if (idx !== -1) {
    selectedCustomerIds.value.splice(idx, 1)
  } else {
    selectedCustomerIds.value.push(id)
  }
}

const isAllCustomersOnPageSelected = computed(() => {
  if (customersList.value.length === 0) return false
  return customersList.value.every(c => selectedCustomerIds.value.includes(c.id))
})

function toggleSelectAllCustomersOnPage() {
  if (isAllCustomersOnPageSelected.value) {
    customersList.value.forEach(c => {
      const idx = selectedCustomerIds.value.indexOf(c.id)
      if (idx !== -1) selectedCustomerIds.value.splice(idx, 1)
    })
  } else {
    customersList.value.forEach(c => {
      if (!selectedCustomerIds.value.includes(c.id)) {
        selectedCustomerIds.value.push(c.id)
      }
    })
  }
}

async function saveCustomerAssignments() {
  if (!currentVoucherForCustomer.value) return
  savingCustomers.value = true
  try {
    await discountService.assignCustomers(currentVoucherForCustomer.value.id, selectedCustomerIds.value)
    toast.success(`Đã cập nhật gán ${selectedCustomerIds.value.length} khách hàng cho voucher!`)
    customerModalInstance.hide()
  } catch (err) {
    toast.error(err.message || 'Lỗi lưu gán khách hàng')
  } finally {
    savingCustomers.value = false
  }
}

// ---------------------- SIMULATOR ----------------------
function openSimulatorModal() {
  sim.code = ''
  sim.orderAmount = 500000
  sim.result = null
  simulatorModalInstance.show()
}

async function runSimulation() {
  if (!sim.code.trim()) {
    toast.warning('Vui lòng nhập mã voucher cần kiểm tra!')
    return
  }

  sim.loading = true
  sim.result = null
  try {
    const res = await discountService.validateVoucher({
      maVoucher: sim.code.trim(),
      tongTienDonHang: sim.orderAmount
    })
    if (res.success && res.data) {
      sim.result = res.data
      if (res.data.valid) {
        toast.success('Voucher hợp lệ!')
      } else {
        toast.warning(res.data.message || 'Không thể áp dụng voucher!')
      }
    }
  } catch (err) {
    toast.error(err.message || 'Không thể kiểm tra voucher!')
  } finally {
    sim.loading = false
  }
}

// ---------------------- UTILS ----------------------
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
  formModalInstance = new Modal(voucherFormModalRef.value)
  simulatorModalInstance = new Modal(simulatorModalRef.value)
  customerModalInstance = new Modal(customerAssignModalRef.value)
  loadVouchers()
})
</script>

<style scoped>
.voucher-management-page {
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

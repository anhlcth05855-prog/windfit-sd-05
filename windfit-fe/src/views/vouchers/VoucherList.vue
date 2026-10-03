<template>
  <div class="voucher-management-page">
    <!-- Header -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
      <div>
        <h4 class="fw-bold mb-1 d-flex align-items-center gap-2">
          <span>Phiếu Giảm Giá (Vouchers)</span>
          <span class="badge bg-primary-subtle text-primary rounded-pill fs-6 px-3 py-1">{{ totalElements }} phiếu</span>
        </h4>
        <p class="text-muted mb-0 small">
          Tạo và quản lý các mã coupon ưu đãi, chiết khấu theo đơn hàng tại quầy và online WindFit
        </p>
      </div>
      <div class="d-flex align-items-center gap-2">
        <button class="btn btn-outline-dark d-flex align-items-center gap-2 px-3 shadow-sm" @click="openSimulatorModal">
          <i class="bi bi-calculator"></i>
          <span>Thử Nghiệm Áp Mã</span>
        </button>
        <button class="btn btn-primary d-flex align-items-center gap-2 px-3 shadow-sm fw-medium" @click="openCreateModal">
          <i class="bi bi-plus-circle-fill"></i>
          <span>Tạo Voucher Mới</span>
        </button>
      </div>
    </div>

    <!-- Stats -->
    <div class="row g-3 mb-4">
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Tổng số voucher</div>
              <div class="fs-4 fw-bold text-dark mt-1">{{ totalElements }}</div>
            </div>
            <div class="stat-icon bg-primary-subtle text-primary rounded-3">
              <i class="bi bi-ticket-perforated-fill fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Đang hoạt động</div>
              <div class="fs-4 fw-bold text-success mt-1">{{ activeVouchersCount }}</div>
            </div>
            <div class="stat-icon bg-success-subtle text-success rounded-3">
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
            <div class="stat-icon bg-info-subtle text-info rounded-3">
              <i class="bi bi-check2-all fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Đã kết thúc / Hết hạn</div>
              <div class="fs-4 fw-bold text-secondary mt-1">{{ expiredCount }}</div>
            </div>
            <div class="stat-icon bg-secondary-subtle text-secondary rounded-3">
              <i class="bi bi-clock-history fs-4"></i>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Card -->
    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
      <!-- Search & Filters -->
      <div class="row g-2 mb-4">
        <div class="col-md-4">
          <div class="input-group search-input-group">
            <span class="input-group-text bg-light border-end-0"><i class="bi bi-search text-muted"></i></span>
            <input 
              type="text" 
              v-model="filters.keyword" 
              @keyup.enter="loadVouchers"
              class="form-control bg-light border-start-0" 
              placeholder="Tìm theo mã voucher, tên chương trình..." 
            />
            <button class="btn btn-light border" v-if="filters.keyword" @click="filters.keyword = ''; loadVouchers()">
              <i class="bi bi-x"></i>
            </button>
          </div>
        </div>

        <div class="col-6 col-md-3">
          <select class="form-select" v-model="filters.loaiGiamGia" @change="loadVouchers">
            <option value="">Tất cả hình thức</option>
            <option value="PHAN_TRAM">Giảm theo phần trăm (%)</option>
            <option value="TIEN_MAT">Giảm thẳng tiền mặt (VNĐ)</option>
          </select>
        </div>

        <div class="col-6 col-md-3">
          <select class="form-select" v-model="filters.trangThai" @change="loadVouchers">
            <option value="">Tất cả trạng thái</option>
            <option value="1">Đang kích hoạt</option>
            <option value="0">Đang khóa</option>
          </select>
        </div>

        <div class="col-12 col-md-2 d-flex gap-1">
          <button class="btn btn-light border w-100" @click="resetFilters" title="Đặt lại bộ lọc">
            <i class="bi bi-arrow-counterclockwise"></i> Đặt lại
          </button>
          <button class="btn btn-primary w-100" @click="loadVouchers" title="Tìm kiếm">
            <i class="bi bi-search"></i> Lọc
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Đang tải...</span>
        </div>
        <p class="text-muted mt-2 small">Đang tải danh sách voucher...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="vouchers.length === 0" class="text-center py-5">
        <i class="bi bi-ticket-detailed text-muted display-4"></i>
        <h6 class="fw-bold mt-3 text-secondary">Không tìm thấy voucher nào</h6>
        <p class="text-muted small">Hãy bấm nút "Tạo Voucher Mới" để bắt đầu chương trình khuyến mại</p>
        <button class="btn btn-sm btn-outline-primary" @click="openCreateModal">
          <i class="bi bi-plus"></i> Tạo Voucher Mới
        </button>
      </div>

      <!-- Table -->
      <div v-else class="table-responsive">
        <table class="table table-hover align-middle custom-table">
          <thead>
            <tr>
              <th style="width: 50px;">#</th>
              <th style="width: 150px;">Mã Voucher</th>
              <th>Tên Chương Trình</th>
              <th>Loại Giảm Giá</th>
              <th>Mức Ưu Đãi</th>
              <th>Đơn Tối Thiểu</th>
              <th style="width: 170px;">Tiến Độ Sử Dụng</th>
              <th>Thời Gian Hiệu Lực</th>
              <th class="text-center">Tình Trạng</th>
              <th class="text-center" style="width: 120px;">Thao Tác</th>
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
                <span 
                  class="badge px-2 py-1"
                  :class="v.loaiGiamGia === 'PHAN_TRAM' ? 'bg-info-subtle text-info border' : 'bg-warning-subtle text-warning border'"
                >
                  <i :class="v.loaiGiamGia === 'PHAN_TRAM' ? 'bi bi-percent me-1' : 'bi bi-cash me-1'"></i>
                  {{ v.loaiGiamGia === 'PHAN_TRAM' ? 'Phần trăm' : 'Tiền mặt' }}
                </span>
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
                <span class="badge rounded-pill px-2 py-1" :class="getStatusBadgeClass(v.tinhTrang)">
                  {{ getStatusText(v.tinhTrang) }}
                </span>
              </td>
              <td class="text-center">
                <div class="btn-group btn-group-sm">
                  <button class="btn btn-outline-secondary" @click="openEditModal(v)" title="Chỉnh sửa">
                    <i class="bi bi-pencil"></i>
                  </button>
                  <button 
                    class="btn" 
                    :class="v.trangThai === 1 ? 'btn-outline-danger' : 'btn-outline-success'"
                    @click="toggleStatus(v)" 
                    :title="v.trangThai === 1 ? 'Tạm khóa voucher' : 'Kích hoạt lại'"
                  >
                    <i :class="v.trangThai === 1 ? 'bi bi-slash-circle' : 'bi bi-arrow-repeat'"></i>
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
          Trang {{ page + 1 }} / {{ totalPages }} (Tổng {{ totalElements }} voucher)
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

    <!-- MODAL 1: CREATE / EDIT VOUCHER -->
    <div class="modal fade" id="voucherFormModal" tabindex="-1" aria-hidden="true" ref="voucherFormModalRef">
      <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
          <div class="modal-header border-bottom bg-light px-4 py-3">
            <h5 class="modal-title fw-bold text-dark d-flex align-items-center gap-2">
              <i :class="isEditMode ? 'bi bi-pencil-square text-primary' : 'bi bi-plus-circle-fill text-primary'"></i>
              <span>{{ isEditMode ? 'Chỉnh Sửa Phiếu Giảm Giá' : 'Tạo Phiếu Giảm Giá Mới' }}</span>
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>

          <div class="modal-body p-4">
            <form @submit.prevent="submitVoucher">
              <div class="row g-3">
                <div class="col-md-5">
                  <label class="form-label small fw-semibold">Mã Voucher (Tự sinh nếu trống)</label>
                  <input 
                    type="text" 
                    v-model="form.maPhieuGiamGia" 
                    class="form-control font-monospace text-uppercase" 
                    placeholder="VD: WINDFIT2026"
                    :disabled="isEditMode"
                  />
                </div>

                <div class="col-md-7">
                  <label class="form-label small fw-semibold">Tên Chương Trình Ưu Đãi <span class="text-danger">*</span></label>
                  <input 
                    type="text" 
                    v-model="form.tenPhieuGiamGia" 
                    required 
                    class="form-control" 
                    placeholder="VD: Ưu đãi chào thu đông giảm 15%..."
                  />
                </div>

                <div class="col-md-6">
                  <label class="form-label small fw-semibold">Hình Thức Giảm Giá <span class="text-danger">*</span></label>
                  <select v-model="form.loaiGiamGia" required class="form-select">
                    <option value="PHAN_TRAM">Giảm theo Phần Trăm (%)</option>
                    <option value="TIEN_MAT">Giảm thẳng Tiền Mặt (VNĐ)</option>
                  </select>
                </div>

                <div class="col-md-6">
                  <label class="form-label small fw-semibold">
                    Giá Trị Giảm <span class="text-danger">*</span>
                    <span class="text-muted small" v-if="form.loaiGiamGia === 'PHAN_TRAM'">(%)</span>
                    <span class="text-muted small" v-else>(VNĐ)</span>
                  </label>
                  <div class="input-group">
                    <input 
                      type="number" 
                      v-model.number="form.giaTriGiam" 
                      required 
                      class="form-control fw-bold" 
                      :min="form.loaiGiamGia === 'PHAN_TRAM' ? 1 : 1000"
                      :max="form.loaiGiamGia === 'PHAN_TRAM' ? 100 : undefined"
                      :step="form.loaiGiamGia === 'PHAN_TRAM' ? 1 : 5000"
                    />
                    <span class="input-group-text">{{ form.loaiGiamGia === 'PHAN_TRAM' ? '%' : 'VNĐ' }}</span>
                  </div>
                </div>

                <div class="col-md-6" v-if="form.loaiGiamGia === 'PHAN_TRAM'">
                  <label class="form-label small fw-semibold">Giảm Tối Đa (VNĐ) <span class="text-muted small">(Để trống nếu không giới hạn)</span></label>
                  <div class="input-group">
                    <input 
                      type="number" 
                      v-model.number="form.giamToiDa" 
                      class="form-control" 
                      min="1000" 
                      step="5000"
                      placeholder="VD: 100.000"
                    />
                    <span class="input-group-text">VNĐ</span>
                  </div>
                </div>

                <div class="col-md-6">
                  <label class="form-label small fw-semibold">Đơn Tối Thiểu Để Áp Dụng <span class="text-danger">*</span></label>
                  <div class="input-group">
                    <input 
                      type="number" 
                      v-model.number="form.donToiThieu" 
                      required 
                      class="form-control" 
                      min="0" 
                      step="10000"
                      placeholder="VD: 500.000"
                    />
                    <span class="input-group-text">VNĐ</span>
                  </div>
                </div>

                <div class="col-md-6">
                  <label class="form-label small fw-semibold">Số Lượng Phát Hành <span class="text-danger">*</span></label>
                  <input 
                    type="number" 
                    v-model.number="form.soLuong" 
                    required 
                    class="form-control" 
                    min="1"
                    placeholder="VD: 100"
                  />
                </div>

                <div class="col-md-6">
                  <label class="form-label small fw-semibold">Ngày & Giờ Bắt Đầu <span class="text-danger">*</span></label>
                  <input 
                    type="datetime-local" 
                    v-model="form.ngayBatDau" 
                    required 
                    class="form-control" 
                  />
                </div>

                <div class="col-md-6">
                  <label class="form-label small fw-semibold">Ngày & Giờ Kết Thúc <span class="text-danger">*</span></label>
                  <input 
                    type="datetime-local" 
                    v-model="form.ngayKetThuc" 
                    required 
                    class="form-control" 
                  />
                </div>
              </div>

              <!-- Submit Buttons -->
              <div class="d-flex justify-content-end gap-2 mt-4 pt-3 border-top">
                <button type="button" class="btn btn-light px-4 border" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" class="btn btn-primary px-4 fw-medium shadow-sm" :disabled="submitting">
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

            <button class="btn btn-primary w-100 py-2 mb-3 fw-medium" @click="runSimulation" :disabled="sim.loading">
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

    <!-- Toast Notification -->
    <div class="position-fixed bottom-0 end-0 p-3" style="z-index: 1080;">
      <div 
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
import { discountService } from '@/services/api'

// Data States
const vouchers = ref([])
const loading = ref(false)
const submitting = ref(false)
const totalElements = ref(0)
const totalPages = ref(0)
const page = ref(0)
const size = ref(10)

// Filters
const filters = reactive({
  keyword: '',
  loaiGiamGia: '',
  trangThai: ''
})

// Modals & Toasts
const voucherFormModalRef = ref(null)
const simulatorModalRef = ref(null)
const toastRef = ref(null)

let formModalInstance = null
let simModalInstance = null
let toastInstance = null

const toastMessage = ref('')
const toastType = ref('success')

// Form State
const isEditMode = ref(false)
const editVoucherId = ref(null)

const form = reactive({
  maPhieuGiamGia: '',
  tenPhieuGiamGia: '',
  loaiGiamGia: 'PHAN_TRAM',
  giaTriGiam: 10,
  giamToiDa: 100000,
  donToiThieu: 500000,
  soLuong: 100,
  ngayBatDau: '',
  ngayKetThuc: '',
  trangThai: 1
})

// Simulator State
const sim = reactive({
  code: 'WINDFIT10',
  orderAmount: 690000,
  loading: false,
  result: null
})

// Computed Stats
const activeVouchersCount = computed(() => vouchers.value.filter(v => v.tinhTrang === 'DANG_DIEN_RA').length)
const totalUsedCount = computed(() => vouchers.value.reduce((acc, v) => acc + (v.soLuongDaDung || 0), 0))
const expiredCount = computed(() => vouchers.value.filter(v => v.tinhTrang === 'DA_KET_THUC' || v.tinhTrang === 'HET_LUOT').length)

const showToast = (message, type = 'success') => {
  toastMessage.value = message
  toastType.value = type
  if (toastInstance) toastInstance.show()
}

const formatCurrency = (val) => {
  if (val === null || val === undefined) return '0 đ'
  return Number(val).toLocaleString('vi-VN') + ' đ'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const getStatusBadgeClass = (status) => {
  switch (status) {
    case 'DANG_DIEN_RA': return 'bg-success-subtle text-success'
    case 'SAP_DIEN_RA': return 'bg-info-subtle text-info'
    case 'HET_LUOT': return 'bg-warning-subtle text-warning'
    case 'DA_KET_THUC': return 'bg-secondary-subtle text-secondary'
    case 'DA_KHOA': return 'bg-danger-subtle text-danger'
    default: return 'bg-light text-dark'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'DANG_DIEN_RA': return 'Đang diễn ra'
    case 'SAP_DIEN_RA': return 'Sắp diễn ra'
    case 'HET_LUOT': return 'Hết lượt dùng'
    case 'DA_KET_THUC': return 'Đã kết thúc'
    case 'DA_KHOA': return 'Đang tạm khóa'
    default: return status
  }
}

const copyCode = (code) => {
  navigator.clipboard.writeText(code)
  showToast(`Đã copy mã ${code} vào bộ nhớ tạm!`, 'success')
}

// Load Vouchers
const loadVouchers = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value,
      keyword: filters.keyword || undefined,
      loaiGiamGia: filters.loaiGiamGia || undefined,
      trangThai: filters.trangThai !== '' ? filters.trangThai : undefined
    }

    const res = await discountService.getVouchers(params)
    if (res.data && res.data.data) {
      const data = res.data.data
      vouchers.value = data.content || []
      totalElements.value = data.totalElements || 0
      totalPages.value = data.totalPages || 0
    }
  } catch (err) {
    showToast('Lỗi khi tải danh sách phiếu giảm giá', 'danger')
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.keyword = ''
  filters.loaiGiamGia = ''
  filters.trangThai = ''
  page.value = 0
  loadVouchers()
}

const changePage = (newPage) => {
  if (newPage >= 0 && newPage < totalPages.value) {
    page.value = newPage
    loadVouchers()
  }
}

// Modals
const openCreateModal = () => {
  isEditMode.value = false
  editVoucherId.value = null

  const now = new Date()
  const nextMonth = new Date()
  nextMonth.setDate(now.getDate() + 30)

  Object.assign(form, {
    maPhieuGiamGia: '',
    tenPhieuGiamGia: '',
    loaiGiamGia: 'PHAN_TRAM',
    giaTriGiam: 10,
    giamToiDa: 100000,
    donToiThieu: 500000,
    soLuong: 100,
    ngayBatDau: now.toISOString().slice(0, 16),
    ngayKetThuc: nextMonth.toISOString().slice(0, 16),
    trangThai: 1
  })

  if (formModalInstance) formModalInstance.show()
}

const openEditModal = (v) => {
  isEditMode.value = true
  editVoucherId.value = v.id

  Object.assign(form, {
    maPhieuGiamGia: v.maPhieuGiamGia,
    tenPhieuGiamGia: v.tenPhieuGiamGia,
    loaiGiamGia: v.loaiGiamGia,
    giaTriGiam: v.giaTriGiam,
    giamToiDa: v.giamToiDa,
    donToiThieu: v.donToiThieu,
    soLuong: v.soLuong,
    ngayBatDau: v.ngayBatDau ? v.ngayBatDau.slice(0, 16) : '',
    ngayKetThuc: v.ngayKetThuc ? v.ngayKetThuc.slice(0, 16) : '',
    trangThai: v.trangThai
  })

  if (formModalInstance) formModalInstance.show()
}

const submitVoucher = async () => {
  submitting.value = true
  try {
    if (isEditMode.value) {
      await discountService.updateVoucher(editVoucherId.value, form)
      showToast('Cập nhật voucher thành công!', 'success')
    } else {
      await discountService.createVoucher(form)
      showToast('Tạo mới voucher thành công!', 'success')
    }
    if (formModalInstance) formModalInstance.hide()
    loadVouchers()
  } catch (err) {
    const msg = err.response?.data?.message || 'Có lỗi xảy ra khi lưu phiếu giảm giá'
    showToast(msg, 'danger')
  } finally {
    submitting.value = false
  }
}

const toggleStatus = async (v) => {
  try {
    await discountService.toggleVoucherStatus(v.id)
    v.trangThai = v.trangThai === 1 ? 0 : 1
    showToast(`Đã đổi trạng thái voucher ${v.maPhieuGiamGia}`, 'success')
    loadVouchers()
  } catch (err) {
    showToast('Lỗi khi đổi trạng thái voucher', 'danger')
  }
}

// Simulator
const openSimulatorModal = () => {
  sim.result = null
  if (simModalInstance) simModalInstance.show()
}

const runSimulation = async () => {
  if (!sim.code.trim()) return
  sim.loading = true
  sim.result = null
  try {
    const res = await discountService.validateVoucher({
      maVoucher: sim.code.trim(),
      tongTienDonHang: sim.orderAmount
    })
    if (res.data && res.data.data) {
      sim.result = res.data.data
    }
  } catch (err) {
    sim.result = {
      valid: false,
      message: err.response?.data?.message || 'Lỗi kết nối khi kiểm tra voucher'
    }
  } finally {
    sim.loading = false
  }
}

onMounted(() => {
  if (voucherFormModalRef.value) formModalInstance = new Modal(voucherFormModalRef.value)
  if (simulatorModalRef.value) simModalInstance = new Modal(simulatorModalRef.value)
  if (toastRef.value) toastInstance = new Toast(toastRef.value, { delay: 3000 })

  loadVouchers()
})
</script>

<style scoped>
.voucher-management-page {
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

.custom-table th {
  background-color: #f8fafc;
  color: #475569;
  font-size: 0.78rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-weight: 700;
  border-top: none;
}
</style>

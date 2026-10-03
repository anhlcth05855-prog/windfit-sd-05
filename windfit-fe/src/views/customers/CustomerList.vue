<template>
  <div class="customer-management-page">
    <!-- Header -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
      <div>
        <h4 class="fw-bold mb-1 d-flex align-items-center gap-2">
          <span>Quản Lý Khách Hàng</span>
          <span class="badge bg-primary-subtle text-primary rounded-pill fs-6 px-3 py-1">{{ totalElements }} khách hàng</span>
        </h4>
        <p class="text-muted mb-0 small">
          Hồ sơ hội viên WindFit, hạng tích điểm, lịch sử mua hàng và thông tin liên hệ
        </p>
      </div>
      <div class="d-flex align-items-center gap-2">
        <button class="btn btn-primary d-flex align-items-center gap-2 px-3 shadow-sm fw-medium" @click="openAddModal">
          <i class="bi bi-person-plus-fill"></i>
          <span>Thêm Khách Hàng</span>
        </button>
      </div>
    </div>

    <!-- Quick Stats -->
    <div class="row g-3 mb-4">
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Tổng khách hàng</div>
              <div class="fs-4 fw-bold text-dark mt-1">{{ totalElements }}</div>
            </div>
            <div class="stat-icon bg-primary-subtle text-primary rounded-3">
              <i class="bi bi-people-fill fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Thành viên VIP & Vàng</div>
              <div class="fs-4 fw-bold text-warning mt-1">{{ vipCount }}</div>
            </div>
            <div class="stat-icon bg-warning-subtle text-warning rounded-3">
              <i class="bi bi-gem fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Tổng chi tiêu toàn hệ thống</div>
              <div class="fs-4 fw-bold text-success mt-1">{{ formatVND(totalSpending) }}</div>
            </div>
            <div class="stat-icon bg-success-subtle text-success rounded-3">
              <i class="bi bi-cash-stack fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Đang hoạt động</div>
              <div class="fs-4 fw-bold text-info mt-1">{{ activeCount }}</div>
            </div>
            <div class="stat-icon bg-info-subtle text-info rounded-3">
              <i class="bi bi-person-check-fill fs-4"></i>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Advanced Filter Toolbar -->
    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white mb-4">
      <div class="d-flex align-items-center justify-content-between mb-3">
        <h6 class="fw-bold text-dark mb-0 d-flex align-items-center gap-2">
          <i class="bi bi-funnel-fill text-primary"></i>
          <span>Bộ Lọc Khách Hàng Chi Tiết</span>
        </h6>
        <button class="btn btn-sm btn-link text-decoration-none text-muted" @click="resetFilters">
          <i class="bi bi-arrow-counterclockwise me-1"></i> Đặt lại tất cả
        </button>
      </div>

      <div class="row g-3">
        <!-- Search Keyword -->
        <div class="col-md-4">
          <label class="form-label small fw-semibold text-secondary">Tìm kiếm thông tin</label>
          <div class="input-group">
            <span class="input-group-text bg-light border-end-0"><i class="bi bi-search text-muted"></i></span>
            <input 
              v-model="filters.keyword" 
              type="text" 
              class="form-control bg-light border-start-0" 
              placeholder="Tên khách hàng, SĐT, Email, Mã KH..." 
              @keyup.enter="page = 0"
            />
            <button class="btn btn-light border" v-if="filters.keyword" @click="filters.keyword = ''; page = 0">
              <i class="bi bi-x"></i>
            </button>
          </div>
        </div>

        <!-- Gender Filter -->
        <div class="col-sm-6 col-md-2">
          <label class="form-label small fw-semibold text-secondary">Giới tính</label>
          <select v-model="filters.gioiTinh" class="form-select bg-light" @change="page = 0">
            <option value="all">Tất cả</option>
            <option value="Nam">Nam</option>
            <option value="Nữ">Nữ</option>
          </select>
        </div>

        <!-- Membership Rank -->
        <div class="col-sm-6 col-md-2">
          <label class="form-label small fw-semibold text-secondary">Hạng thành viên</label>
          <select v-model="filters.hang" class="form-select bg-light" @change="page = 0">
            <option value="all">Tất cả hạng</option>
            <option value="Kim Cương">Kim Cương</option>
            <option value="VIP">VIP</option>
            <option value="Vàng">Vàng</option>
            <option value="Bạc">Bạc</option>
            <option value="Thân Thiết">Thân Thiết</option>
            <option value="Mới">Mới</option>
          </select>
        </div>

        <!-- Spending Range -->
        <div class="col-sm-6 col-md-2">
          <label class="form-label small fw-semibold text-secondary">Mức chi tiêu</label>
          <select v-model="filters.spendingTier" class="form-select bg-light" @change="page = 0">
            <option value="all">Tất cả mức</option>
            <option value="under1m">Dưới 1 triệu</option>
            <option value="1mTo5m">1 triệu - 5 triệu</option>
            <option value="over5m">Trên 5 triệu</option>
          </select>
        </div>

        <!-- Status Filter -->
        <div class="col-sm-6 col-md-2">
          <label class="form-label small fw-semibold text-secondary">Trạng thái</label>
          <select v-model="filters.trangThai" class="form-select bg-light" @change="page = 0">
            <option value="all">Tất cả trạng thái</option>
            <option value="1">Đang hoạt động</option>
            <option value="0">Tạm khóa</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Table Card -->
    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
      <!-- Loading State -->
      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Đang tải...</span>
        </div>
        <p class="text-muted mt-2 small">Đang tải danh sách khách hàng...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="filteredCustomers.length === 0" class="text-center py-5">
        <i class="bi bi-people text-muted display-4"></i>
        <h6 class="fw-bold mt-3 text-secondary">Không tìm thấy khách hàng nào</h6>
        <p class="text-muted small">Hãy bấm nút "Thêm Khách Hàng" để tạo hồ sơ mới</p>
        <button class="btn btn-sm btn-outline-primary" @click="openAddModal">
          <i class="bi bi-person-plus"></i> Thêm Khách Hàng
        </button>
      </div>

      <!-- Table -->
      <div v-else class="table-responsive">
        <table class="table table-hover align-middle custom-table">
          <thead>
            <tr>
              <th style="width: 50px;">STT</th>
              <th style="width: 120px;">Mã KH</th>
              <th>Khách Hàng</th>
              <th>Liên Hệ (SĐT / Email)</th>
              <th>Giới Tính</th>
              <th>Hạng Hội Viên</th>
              <th>Tổng Đơn</th>
              <th>Tổng Chi Tiêu</th>
              <th class="text-center">Trạng Thái</th>
              <th class="text-center" style="width: 120px;">Thao Tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(kh, idx) in pagedCustomers" :key="kh.id">
              <td class="text-muted small">{{ page * size + idx + 1 }}</td>
              <td>
                <span class="badge bg-light text-dark font-monospace border px-2 py-1">
                  {{ kh.maKhachHang }}
                </span>
              </td>
              <td>
                <div class="d-flex align-items-center gap-2">
                  <div class="avatar-circle bg-primary-subtle text-primary fw-bold">
                    {{ getInitials(kh.hoTen) }}
                  </div>
                  <div>
                    <div class="fw-bold text-dark">{{ kh.hoTen }}</div>
                    <small class="text-muted" v-if="kh.diaChi">{{ kh.diaChi }}</small>
                  </div>
                </div>
              </td>
              <td>
                <div class="small">
                  <div><i class="bi bi-telephone me-1 text-primary"></i>{{ kh.soDienThoai }}</div>
                  <div class="text-muted"><i class="bi bi-envelope me-1"></i>{{ kh.email || 'Chưa cập nhật' }}</div>
                </div>
              </td>
              <td>
                <span class="badge" :class="kh.gioiTinh === 'Nam' ? 'bg-primary-subtle text-primary' : 'bg-danger-subtle text-danger'">
                  {{ kh.gioiTinh || 'Khác' }}
                </span>
              </td>
              <td>
                <span class="badge rounded-pill px-3 py-1" :class="getRankBadgeClass(kh.hang)">
                  <i class="bi bi-gem me-1" v-if="kh.hang === 'Kim Cương' || kh.hang === 'VIP'"></i>
                  {{ kh.hang || 'Thành viên' }}
                </span>
              </td>
              <td>
                <span class="badge bg-light text-secondary border px-2 py-1 fw-bold">
                  {{ kh.tongDon || 0 }} đơn
                </span>
              </td>
              <td>
                <span class="fw-bold text-success">{{ formatVND(kh.tongChiTieu || 0) }}</span>
              </td>
              <td class="text-center">
                <span class="badge rounded-pill px-2 py-1" :class="kh.trangThai === 1 ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'">
                  {{ kh.trangThai === 1 ? 'Hoạt động' : 'Tạm khóa' }}
                </span>
              </td>
              <td class="text-center">
                <div class="btn-group btn-group-sm">
                  <button class="btn btn-outline-secondary" @click="viewCustomer(kh)" title="Xem chi tiết">
                    <i class="bi bi-eye"></i>
                  </button>
                  <button class="btn btn-outline-primary" @click="openEditModal(kh)" title="Chỉnh sửa">
                    <i class="bi bi-pencil"></i>
                  </button>
                  <button 
                    class="btn" 
                    :class="kh.trangThai === 1 ? 'btn-outline-danger' : 'btn-outline-success'"
                    @click="toggleStatus(kh)" 
                    :title="kh.trangThai === 1 ? 'Tạm khóa' : 'Mở khóa'"
                  >
                    <i :class="kh.trangThai === 1 ? 'bi bi-slash-circle' : 'bi bi-check-circle'"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Full Pagination System -->
      <div class="d-flex flex-wrap justify-content-between align-items-center mt-4 pt-3 border-top gap-3" v-if="filteredCustomers.length > 0">
        <div class="d-flex align-items-center gap-2 text-muted small">
          <span>Hiển thị</span>
          <select class="form-select form-select-sm w-auto bg-light" v-model.number="size" @change="page = 0">
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="20">20</option>
            <option :value="50">50</option>
          </select>
          <span>dòng / trang</span>
          <span class="ms-2 border-start ps-3 text-secondary">
            Hiển thị <strong>{{ paginationFrom }} - {{ paginationTo }}</strong> trên tổng số <strong>{{ totalElements }}</strong> khách hàng
          </span>
        </div>

        <nav aria-label="Customer navigation" v-if="totalPages > 0">
          <ul class="pagination pagination-sm mb-0">
            <li class="page-item" :class="{ disabled: page === 0 }">
              <button class="page-link" @click="page = 0" title="Trang đầu">
                <i class="bi bi-chevron-double-left"></i>
              </button>
            </li>
            <li class="page-item" :class="{ disabled: page === 0 }">
              <button class="page-link" @click="page = Math.max(0, page - 1)" title="Trang trước">
                <i class="bi bi-chevron-left"></i>
              </button>
            </li>
            <li 
              v-for="pNum in visiblePageNumbers" 
              :key="pNum" 
              class="page-item"
              :class="{ active: page === pNum - 1 }"
            >
              <button class="page-link" @click="page = pNum - 1">{{ pNum }}</button>
            </li>
            <li class="page-item" :class="{ disabled: page >= totalPages - 1 }">
              <button class="page-link" @click="page = Math.min(totalPages - 1, page + 1)" title="Trang sau">
                <i class="bi bi-chevron-right"></i>
              </button>
            </li>
            <li class="page-item" :class="{ disabled: page >= totalPages - 1 }">
              <button class="page-link" @click="page = totalPages - 1" title="Trang cuối">
                <i class="bi bi-chevron-double-right"></i>
              </button>
            </li>
          </ul>
        </nav>
      </div>
    </div>

    <!-- Modal Thêm / Chỉnh Sửa Khách Hàng -->
    <div v-if="isModalOpen" class="modal-backdrop-custom" @click.self="isModalOpen = false">
      <div class="modal-card-custom animate-scale-up">
        <div class="modal-header-custom">
          <h5 class="modal-title-custom fw-bold d-flex align-items-center gap-2">
            <i :class="isEditMode ? 'bi bi-person-gear text-primary' : 'bi bi-person-plus-fill text-primary'"></i>
            <span>{{ isEditMode ? 'Chỉnh Sửa Thông Tin Khách Hàng' : 'Thêm Mới Khách Hàng' }}</span>
          </h5>
          <button class="btn-close-custom" @click="isModalOpen = false">&times;</button>
        </div>
        <form @submit.prevent="saveCustomer" class="modal-body-custom">
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Mã Khách Hàng <span class="text-danger">*</span></label>
              <input v-model="formKh.maKhachHang" type="text" class="form-control font-monospace" required :disabled="isEditMode" />
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Họ Và Tên <span class="text-danger">*</span></label>
              <input v-model="formKh.hoTen" type="text" class="form-control" required placeholder="Nguyễn Văn A" />
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Số Điện Thoại <span class="text-danger">*</span></label>
              <input v-model="formKh.soDienThoai" type="tel" class="form-control" required placeholder="0912345678" />
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Email</label>
              <input v-model="formKh.email" type="email" class="form-control" placeholder="user@gmail.com" />
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Giới Tính</label>
              <select v-model="formKh.gioiTinh" class="form-select">
                <option value="Nam">Nam</option>
                <option value="Nữ">Nữ</option>
                <option value="Khác">Khác</option>
              </select>
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Hạng Thành Viên</label>
              <select v-model="formKh.hang" class="form-select">
                <option value="Mới">Mới</option>
                <option value="Thân Thiết">Thân Thiết</option>
                <option value="Bạc">Bạc</option>
                <option value="Vàng">Vàng</option>
                <option value="Kim Cương">Kim Cương</option>
                <option value="VIP">VIP</option>
              </select>
            </div>
            <div class="col-12">
              <label class="form-label small fw-semibold">Địa Chỉ Giao Hàng</label>
              <textarea v-model="formKh.diaChi" class="form-control" rows="2" placeholder="Số nhà, tên đường, phường/xã, quận/huyện..."></textarea>
            </div>
          </div>
          <div class="modal-footer-custom mt-4 d-flex justify-content-end gap-2 border-top pt-3">
            <button type="button" class="btn btn-light px-4 border" @click="isModalOpen = false">Hủy</button>
            <button type="submit" class="btn btn-primary px-4 fw-medium shadow-sm">
              {{ isEditMode ? 'Lưu Thay Đổi' : 'Tạo Khách Hàng' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { danhSachKhachHang } from '@/data/mockData.js'
import { customerService } from '@/services/api.js'

const customers = ref([])
const loading = ref(false)
const isModalOpen = ref(false)
const isEditMode = ref(false)
const editCustomerId = ref(null)

// Pagination
const page = ref(0)
const size = ref(10)

const filters = reactive({
  keyword: '',
  gioiTinh: 'all',
  hang: 'all',
  spendingTier: 'all',
  trangThai: 'all'
})

const formKh = reactive({
  maKhachHang: '',
  hoTen: '',
  soDienThoai: '',
  email: '',
  gioiTinh: 'Nam',
  hang: 'Thân Thiết',
  diaChi: '',
  trangThai: 1
})

const loadCustomers = async () => {
  loading.value = true
  try {
    const res = await customerService.getAll()
    const raw = (res && res.data) ? res.data : res
    if (raw && (raw.content || Array.isArray(raw))) {
      customers.value = raw.content || raw
    } else {
      customers.value = [...danhSachKhachHang]
    }
  } catch (err) {
    console.warn('API getCustomers failed, fallback to mock data:', err)
    customers.value = [...danhSachKhachHang]
  } finally {
    loading.value = false
  }
}

// Stats
const vipCount = computed(() => customers.value.filter(c => c.hang === 'VIP' || c.hang === 'Kim Cương' || c.hang === 'Vàng').length)
const totalSpending = computed(() => customers.value.reduce((acc, c) => acc + (c.tongChiTieu || 0), 0))
const activeCount = computed(() => customers.value.filter(c => c.trangThai === 1).length)

// Filtered List
const filteredCustomers = computed(() => {
  return customers.value.filter(kh => {
    if (filters.keyword.trim()) {
      const kw = filters.keyword.toLowerCase().trim()
      const match = (kh.hoTen && kh.hoTen.toLowerCase().includes(kw)) ||
                    (kh.soDienThoai && kh.soDienThoai.includes(kw)) ||
                    (kh.email && kh.email.toLowerCase().includes(kw)) ||
                    (kh.maKhachHang && kh.maKhachHang.toLowerCase().includes(kw))
      if (!match) return false
    }

    if (filters.gioiTinh !== 'all' && kh.gioiTinh !== filters.gioiTinh) return false
    if (filters.hang !== 'all' && kh.hang !== filters.hang) return false
    if (filters.trangThai !== 'all' && String(kh.trangThai) !== String(filters.trangThai)) return false

    if (filters.spendingTier !== 'all') {
      const spent = kh.tongChiTieu || 0
      if (filters.spendingTier === 'under1m' && spent >= 1000000) return false
      if (filters.spendingTier === '1mTo5m' && (spent < 1000000 || spent > 5000000)) return false
      if (filters.spendingTier === 'over5m' && spent <= 5000000) return false
    }

    return true
  })
})

// Pagination Computed
const totalElements = computed(() => filteredCustomers.value.length)
const totalPages = computed(() => Math.ceil(totalElements.value / size.value) || 1)

const paginationFrom = computed(() => {
  if (totalElements.value === 0) return 0
  return page.value * size.value + 1
})

const paginationTo = computed(() => {
  return Math.min((page.value + 1) * size.value, totalElements.value)
})

const visiblePageNumbers = computed(() => {
  const pages = []
  const maxVisible = 5
  let start = Math.max(1, page.value + 1 - Math.floor(maxVisible / 2))
  let end = Math.min(totalPages.value, start + maxVisible - 1)
  if (end - start + 1 < maxVisible) {
    start = Math.max(1, end - maxVisible + 1)
  }
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

const pagedCustomers = computed(() => {
  const start = page.value * size.value
  return filteredCustomers.value.slice(start, start + size.value)
})

function resetFilters() {
  filters.keyword = ''
  filters.gioiTinh = 'all'
  filters.hang = 'all'
  filters.spendingTier = 'all'
  filters.trangThai = 'all'
  page.value = 0
}

function getInitials(name) {
  if (!name) return 'KH'
  const parts = name.trim().split(' ')
  if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase()
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
}

function getRankBadgeClass(rank) {
  switch (rank) {
    case 'Kim Cương': return 'bg-info text-white'
    case 'VIP': return 'bg-danger text-white'
    case 'Vàng': return 'bg-warning text-dark'
    case 'Bạc': return 'bg-secondary text-white'
    default: return 'bg-light text-dark border'
  }
}

function formatVND(val) {
  if (!val) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}

function openAddModal() {
  isEditMode.value = false
  editCustomerId.value = null
  Object.assign(formKh, {
    maKhachHang: 'KH0000' + (customers.value.length + 1),
    hoTen: '',
    soDienThoai: '',
    email: '',
    gioiTinh: 'Nam',
    hang: 'Thân Thiết',
    diaChi: '',
    trangThai: 1
  })
  isModalOpen.value = true
}

function openEditModal(kh) {
  isEditMode.value = true
  editCustomerId.value = kh.id
  Object.assign(formKh, {
    maKhachHang: kh.maKhachHang,
    hoTen: kh.hoTen,
    soDienThoai: kh.soDienThoai,
    email: kh.email,
    gioiTinh: kh.gioiTinh || 'Nam',
    hang: kh.hang || 'Thân Thiết',
    diaChi: kh.diaChi || '',
    trangThai: kh.trangThai
  })
  isModalOpen.value = true
}

function saveCustomer() {
  if (isEditMode.value) {
    const idx = customers.value.findIndex(c => c.id === editCustomerId.value)
    if (idx !== -1) {
      customers.value[idx] = {
        ...customers.value[idx],
        ...formKh
      }
    }
  } else {
    customers.value.unshift({
      id: Date.now(),
      ...formKh,
      tongDon: 0,
      tongChiTieu: 0
    })
  }
  isModalOpen.value = false
}

function toggleStatus(kh) {
  kh.trangThai = kh.trangThai === 1 ? 0 : 1
}

function viewCustomer(kh) {
  alert(`Thông tin khách hàng:\n- Mã: ${kh.maKhachHang}\n- Tên: ${kh.hoTen}\n- SĐT: ${kh.soDienThoai}\n- Hạng: ${kh.hang}\n- Tổng đơn: ${kh.tongDon}\n- Tổng chi tiêu: ${formatVND(kh.tongChiTieu)}\n- Địa chỉ: ${kh.diaChi || 'Chưa cập nhật'}`)
}

onMounted(() => {
  loadCustomers()
})
</script>

<style scoped>
.customer-management-page {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
}

.stat-card {
  transition: transform 0.2s ease;
}
.stat-card:hover {
  transform: translateY(-2px);
}
.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-circle {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
}

.custom-table thead th {
  background-color: #f8fafc;
  color: #475569;
  font-weight: 600;
  font-size: 0.85rem;
  padding: 12px 14px;
  border-bottom: 1px solid #e2e8f0;
}
.custom-table tbody td {
  padding: 14px;
  font-size: 0.875rem;
  border-bottom: 1px solid #f1f5f9;
}

.modal-backdrop-custom {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.45);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal-card-custom {
  background: #ffffff;
  border-radius: 16px;
  width: 620px;
  max-width: 92vw;
  box-shadow: 0 20px 30px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.modal-header-custom {
  padding: 18px 24px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.modal-title-custom {
  font-size: 1.1rem;
  color: #0f172a;
  margin: 0;
}

.btn-close-custom {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #94a3b8;
  cursor: pointer;
}

.modal-body-custom {
  padding: 24px;
}
</style>

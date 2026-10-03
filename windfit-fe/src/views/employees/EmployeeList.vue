<template>
  <div class="employee-management-page">
    <!-- Header -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
      <div>
        <h4 class="fw-bold mb-1 d-flex align-items-center gap-2">
          <span>Quản Lý Nhân Viên & Phân Quyền</span>
          <span class="badge bg-primary-subtle text-primary rounded-pill fs-6 px-3 py-1">{{ totalElements }} nhân sự</span>
        </h4>
        <p class="text-muted mb-0 small">
          Danh sách nhân viên WindFit, phân quyền tài khoản (ADMIN, MANAGER, STAFF) và tình trạng làm việc
        </p>
      </div>
      <div class="d-flex align-items-center gap-2">
        <button class="btn btn-primary d-flex align-items-center gap-2 px-3 shadow-sm fw-medium" @click="openAddModal">
          <i class="bi bi-person-plus-fill"></i>
          <span>Thêm Nhân Viên</span>
        </button>
      </div>
    </div>

    <!-- Quick Stats -->
    <div class="row g-3 mb-4">
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Tổng số nhân sự</div>
              <div class="fs-4 fw-bold text-dark mt-1">{{ totalElements }}</div>
            </div>
            <div class="stat-icon bg-primary-subtle text-primary rounded-3">
              <i class="bi bi-person-badge-fill fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Quản trị viên (ADMIN)</div>
              <div class="fs-4 fw-bold text-danger mt-1">{{ adminCount }}</div>
            </div>
            <div class="stat-icon bg-danger-subtle text-danger rounded-3">
              <i class="bi bi-shield-lock-fill fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Quản lý (MANAGER)</div>
              <div class="fs-4 fw-bold text-warning mt-1">{{ managerCount }}</div>
            </div>
            <div class="stat-icon bg-warning-subtle text-warning rounded-3">
              <i class="bi bi-person-lines-fill fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Nhân viên (STAFF)</div>
              <div class="fs-4 fw-bold text-success mt-1">{{ staffCount }}</div>
            </div>
            <div class="stat-icon bg-success-subtle text-success rounded-3">
              <i class="bi bi-person-workspace fs-4"></i>
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
          <span>Bộ Lọc Nhân Viên Chi Tiết</span>
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
              placeholder="Tên nhân viên, mã NV, SĐT, Email..." 
              @keyup.enter="page = 0"
            />
            <button class="btn btn-light border" v-if="filters.keyword" @click="filters.keyword = ''; page = 0">
              <i class="bi bi-x"></i>
            </button>
          </div>
        </div>

        <!-- Role Filter -->
        <div class="col-sm-6 col-md-3">
          <label class="form-label small fw-semibold text-secondary">Vai trò / Phân quyền</label>
          <select v-model="filters.vaiTro" class="form-select bg-light" @change="page = 0">
            <option value="all">Tất cả vai trò</option>
            <option value="ADMIN">ADMIN (Quản trị viên)</option>
            <option value="MANAGER">MANAGER (Quản lý cửa hàng)</option>
            <option value="STAFF">STAFF (Nhân viên bán hàng)</option>
          </select>
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

        <!-- Status Filter -->
        <div class="col-sm-6 col-md-3">
          <label class="form-label small fw-semibold text-secondary">Trạng thái công việc</label>
          <select v-model="filters.trangThai" class="form-select bg-light" @change="page = 0">
            <option value="all">Tất cả trạng thái</option>
            <option value="1">Đang làm việc</option>
            <option value="0">Tạm nghỉ / Đã khóa</option>
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
        <p class="text-muted mt-2 small">Đang tải danh sách nhân sự...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="filteredEmployees.length === 0" class="text-center py-5">
        <i class="bi bi-person-badge text-muted display-4"></i>
        <h6 class="fw-bold mt-3 text-secondary">Không tìm thấy nhân viên nào</h6>
        <p class="text-muted small">Hãy bấm nút "Thêm Nhân Viên" để tạo tài khoản mới</p>
        <button class="btn btn-sm btn-outline-primary" @click="openAddModal">
          <i class="bi bi-person-plus"></i> Thêm Nhân Viên
        </button>
      </div>

      <!-- Table -->
      <div v-else class="table-responsive">
        <table class="table table-hover align-middle custom-table">
          <thead>
            <tr>
              <th style="width: 50px;">STT</th>
              <th style="width: 120px;">Mã NV</th>
              <th>Nhân Viên</th>
              <th>Vai Trò & Chức Vụ</th>
              <th>Liên Hệ (SĐT / Email)</th>
              <th>Giới Tính</th>
              <th>Địa Chỉ</th>
              <th class="text-center">Trạng Thái</th>
              <th class="text-center" style="width: 120px;">Thao Tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(nv, idx) in pagedEmployees" :key="nv.id">
              <td class="text-muted small">{{ page * size + idx + 1 }}</td>
              <td>
                <span class="badge bg-light text-dark font-monospace border px-2 py-1">
                  {{ nv.maNv }}
                </span>
              </td>
              <td>
                <div class="d-flex align-items-center gap-2">
                  <div class="avatar-circle bg-primary-subtle text-primary fw-bold">
                    {{ getInitials(nv.hoTen) }}
                  </div>
                  <div>
                    <div class="fw-bold text-dark">{{ nv.hoTen }}</div>
                  </div>
                </div>
              </td>
              <td>
                <span class="badge rounded-pill px-3 py-1" :class="getRoleBadgeClass(nv.vaiTro)">
                  <i class="bi bi-shield-lock me-1" v-if="nv.vaiTro === 'ADMIN'"></i>
                  {{ nv.tenVaiTro || nv.vaiTro }}
                </span>
              </td>
              <td>
                <div class="small">
                  <div><i class="bi bi-telephone me-1 text-primary"></i>{{ nv.soDienThoai }}</div>
                  <div class="text-muted"><i class="bi bi-envelope me-1"></i>{{ nv.email }}</div>
                </div>
              </td>
              <td>
                <span class="badge" :class="nv.gioiTinh === 'Nam' ? 'bg-primary-subtle text-primary' : 'bg-danger-subtle text-danger'">
                  {{ nv.gioiTinh || 'Nam' }}
                </span>
              </td>
              <td>
                <span class="small text-muted">{{ nv.diaChi || 'Cửa hàng WindFit' }}</span>
              </td>
              <td class="text-center">
                <span class="badge rounded-pill px-2 py-1" :class="nv.trangThai === 1 ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'">
                  {{ nv.trangThai === 1 ? 'Đang làm việc' : 'Tạm khóa' }}
                </span>
              </td>
              <td class="text-center">
                <div class="btn-group btn-group-sm">
                  <button class="btn btn-outline-secondary" @click="viewEmployee(nv)" title="Xem chi tiết">
                    <i class="bi bi-eye"></i>
                  </button>
                  <button class="btn btn-outline-primary" @click="openEditModal(nv)" title="Chỉnh sửa">
                    <i class="bi bi-pencil"></i>
                  </button>
                  <button 
                    class="btn" 
                    :class="nv.trangThai === 1 ? 'btn-outline-danger' : 'btn-outline-success'"
                    @click="toggleStatus(nv)" 
                    :title="nv.trangThai === 1 ? 'Tạm khóa' : 'Mở khóa'"
                  >
                    <i :class="nv.trangThai === 1 ? 'bi bi-slash-circle' : 'bi bi-check-circle'"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Full Pagination System -->
      <div class="d-flex flex-wrap justify-content-between align-items-center mt-4 pt-3 border-top gap-3" v-if="filteredEmployees.length > 0">
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
            Hiển thị <strong>{{ paginationFrom }} - {{ paginationTo }}</strong> trên tổng số <strong>{{ totalElements }}</strong> nhân viên
          </span>
        </div>

        <nav aria-label="Employee navigation" v-if="totalPages > 0">
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

    <!-- Modal Thêm / Chỉnh Sửa Nhân Viên -->
    <div v-if="isModalOpen" class="modal-backdrop-custom" @click.self="isModalOpen = false">
      <div class="modal-card-custom animate-scale-up">
        <div class="modal-header-custom">
          <h5 class="modal-title-custom fw-bold d-flex align-items-center gap-2">
            <i :class="isEditMode ? 'bi bi-person-gear text-primary' : 'bi bi-person-plus-fill text-primary'"></i>
            <span>{{ isEditMode ? 'Chỉnh Sửa Nhân Viên' : 'Thêm Nhân Viên Mới' }}</span>
          </h5>
          <button class="btn-close-custom" @click="isModalOpen = false">&times;</button>
        </div>
        <form @submit.prevent="saveEmployee" class="modal-body-custom">
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Mã Nhân Viên <span class="text-danger">*</span></label>
              <input v-model="formNv.maNv" type="text" class="form-control font-monospace" required :disabled="isEditMode" />
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Họ Và Tên <span class="text-danger">*</span></label>
              <input v-model="formNv.hoTen" type="text" class="form-control" required placeholder="Nguyễn Văn B" />
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Vai Trò / Quyền <span class="text-danger">*</span></label>
              <select v-model="formNv.vaiTro" class="form-select">
                <option value="ADMIN">ADMIN (Quản trị viên)</option>
                <option value="MANAGER">MANAGER (Quản lý)</option>
                <option value="STAFF">STAFF (Nhân viên)</option>
              </select>
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Số Điện Thoại <span class="text-danger">*</span></label>
              <input v-model="formNv.soDienThoai" type="tel" class="form-control" required placeholder="0988111222" />
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Email Công Việc</label>
              <input v-model="formNv.email" type="email" class="form-control" placeholder="user@windfit.vn" />
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Giới Tính</label>
              <select v-model="formNv.gioiTinh" class="form-select">
                <option value="Nam">Nam</option>
                <option value="Nữ">Nữ</option>
              </select>
            </div>
            <div class="col-12">
              <label class="form-label small fw-semibold">Địa Chỉ</label>
              <input v-model="formNv.diaChi" type="text" class="form-control" placeholder="Quận/Huyện, Tỉnh/Thành phố..." />
            </div>
          </div>
          <div class="modal-footer-custom mt-4 d-flex justify-content-end gap-2 border-top pt-3">
            <button type="button" class="btn btn-light px-4 border" @click="isModalOpen = false">Hủy</button>
            <button type="submit" class="btn btn-primary px-4 fw-medium shadow-sm">
              {{ isEditMode ? 'Lưu Thay Đổi' : 'Tạo Nhân Viên' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { danhSachNhanVien } from '@/data/mockData.js'
import { employeeService } from '@/services/api.js'

const employees = ref([])
const loading = ref(false)
const isModalOpen = ref(false)
const isEditMode = ref(false)
const editEmployeeId = ref(null)

// Pagination
const page = ref(0)
const size = ref(10)

const filters = reactive({
  keyword: '',
  vaiTro: 'all',
  gioiTinh: 'all',
  trangThai: 'all'
})

const formNv = reactive({
  maNv: '',
  hoTen: '',
  vaiTro: 'STAFF',
  tenVaiTro: 'Nhân viên bán hàng',
  soDienThoai: '',
  email: '',
  gioiTinh: 'Nam',
  diaChi: '',
  trangThai: 1
})

const loadEmployees = async () => {
  loading.value = true
  try {
    const res = await employeeService.getAll()
    const raw = (res && res.data) ? res.data : res
    if (raw && (raw.content || Array.isArray(raw))) {
      employees.value = raw.content || raw
    } else {
      employees.value = [...danhSachNhanVien]
    }
  } catch (err) {
    console.warn('API getEmployees failed, fallback to mock data:', err)
    employees.value = [...danhSachNhanVien]
  } finally {
    loading.value = false
  }
}

// Stats
const adminCount = computed(() => employees.value.filter(e => e.vaiTro === 'ADMIN').length)
const managerCount = computed(() => employees.value.filter(e => e.vaiTro === 'MANAGER').length)
const staffCount = computed(() => employees.value.filter(e => e.vaiTro === 'STAFF').length)

// Filtered List
const filteredEmployees = computed(() => {
  return employees.value.filter(nv => {
    if (filters.keyword.trim()) {
      const kw = filters.keyword.toLowerCase().trim()
      const match = (nv.hoTen && nv.hoTen.toLowerCase().includes(kw)) ||
                    (nv.maNv && nv.maNv.toLowerCase().includes(kw)) ||
                    (nv.soDienThoai && nv.soDienThoai.includes(kw)) ||
                    (nv.email && nv.email.toLowerCase().includes(kw))
      if (!match) return false
    }

    if (filters.vaiTro !== 'all' && nv.vaiTro !== filters.vaiTro) return false
    if (filters.gioiTinh !== 'all' && nv.gioiTinh !== filters.gioiTinh) return false
    if (filters.trangThai !== 'all' && String(nv.trangThai) !== String(filters.trangThai)) return false

    return true
  })
})

// Pagination Computed
const totalElements = computed(() => filteredEmployees.value.length)
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

const pagedEmployees = computed(() => {
  const start = page.value * size.value
  return filteredEmployees.value.slice(start, start + size.value)
})

function resetFilters() {
  filters.keyword = ''
  filters.vaiTro = 'all'
  filters.gioiTinh = 'all'
  filters.trangThai = 'all'
  page.value = 0
}

function getInitials(name) {
  if (!name) return 'NV'
  const parts = name.trim().split(' ')
  if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase()
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
}

function getRoleBadgeClass(role) {
  switch (role) {
    case 'ADMIN': return 'bg-danger text-white'
    case 'MANAGER': return 'bg-warning text-dark'
    case 'STAFF': return 'bg-primary text-white'
    default: return 'bg-secondary text-white'
  }
}

function openAddModal() {
  isEditMode.value = false
  editEmployeeId.value = null
  Object.assign(formNv, {
    maNv: 'NV00' + (employees.value.length + 1),
    hoTen: '',
    vaiTro: 'STAFF',
    tenVaiTro: 'Nhân viên bán hàng',
    soDienThoai: '',
    email: '',
    gioiTinh: 'Nam',
    diaChi: '',
    trangThai: 1
  })
  isModalOpen.value = true
}

function openEditModal(nv) {
  isEditMode.value = true
  editEmployeeId.value = nv.id
  Object.assign(formNv, {
    maNv: nv.maNv,
    hoTen: nv.hoTen,
    vaiTro: nv.vaiTro,
    tenVaiTro: nv.tenVaiTro || nv.vaiTro,
    soDienThoai: nv.soDienThoai,
    email: nv.email,
    gioiTinh: nv.gioiTinh || 'Nam',
    diaChi: nv.diaChi || '',
    trangThai: nv.trangThai
  })
  isModalOpen.value = true
}

function saveEmployee() {
  const roleNames = { ADMIN: 'Quản trị viên', MANAGER: 'Quản lý cửa hàng', STAFF: 'Nhân viên bán hàng' }
  formNv.tenVaiTro = roleNames[formNv.vaiTro] || formNv.vaiTro

  if (isEditMode.value) {
    const idx = employees.value.findIndex(e => e.id === editEmployeeId.value)
    if (idx !== -1) {
      employees.value[idx] = {
        ...employees.value[idx],
        ...formNv
      }
    }
  } else {
    employees.value.unshift({
      id: Date.now(),
      ...formNv
    })
  }
  isModalOpen.value = false
}

function toggleStatus(nv) {
  nv.trangThai = nv.trangThai === 1 ? 0 : 1
}

function viewEmployee(nv) {
  alert(`Thông tin nhân viên:\n- Mã NV: ${nv.maNv}\n- Họ tên: ${nv.hoTen}\n- Chức vụ: ${nv.tenVaiTro || nv.vaiTro}\n- SĐT: ${nv.soDienThoai}\n- Email: ${nv.email}\n- Địa chỉ: ${nv.diaChi || 'Cửa hàng WindFit'}`)
}

onMounted(() => {
  loadEmployees()
})
</script>

<style scoped>
.employee-management-page {
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

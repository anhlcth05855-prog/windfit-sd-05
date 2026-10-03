<template>
  <div class="windfit-campaign-page">
    <!-- Header -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
      <div>
        <h4 class="fw-bold mb-1 d-flex align-items-center gap-2">
          <span>Đợt Giảm Giá (Campaigns)</span>
          <span class="badge bg-primary-subtle text-primary rounded-pill fs-6 px-3 py-1">{{ totalElements }} chiến dịch</span>
        </h4>
        <p class="text-muted mb-0 small">
          Tổ chức các sự kiện khuyến mãi, lễ hội áo gió thể thao & Flash Sale toàn hệ thống WindFit
        </p>
      </div>
      <div class="d-flex align-items-center gap-2">
        <button class="btn btn-primary d-flex align-items-center gap-2 px-3 shadow-sm fw-medium" @click="openAddModal">
          <i class="bi bi-plus-circle-fill"></i>
          <span>Tạo Đợt Giảm Giá</span>
        </button>
      </div>
    </div>

    <!-- Quick Stats -->
    <div class="row g-3 mb-4">
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Tổng số chiến dịch</div>
              <div class="fs-4 fw-bold text-dark mt-1">{{ totalElements }}</div>
            </div>
            <div class="stat-icon bg-primary-subtle text-primary rounded-3">
              <i class="bi bi-megaphone-fill fs-4"></i>
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
              <div class="text-muted small fw-semibold">Sắp diễn ra</div>
              <div class="fs-4 fw-bold text-info mt-1">{{ upcomingCount }}</div>
            </div>
            <div class="stat-icon bg-info-subtle text-info rounded-3">
              <i class="bi bi-hourglass-split fs-4"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="col-sm-6 col-xl-3">
        <div class="card stat-card border-0 shadow-sm rounded-4 p-3 bg-white">
          <div class="d-flex align-items-center justify-content-between">
            <div>
              <div class="text-muted small fw-semibold">Đã kết thúc</div>
              <div class="fs-4 fw-bold text-secondary mt-1">{{ endedCount }}</div>
            </div>
            <div class="stat-icon bg-secondary-subtle text-secondary rounded-3">
              <i class="bi bi-check2-circle fs-4"></i>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Filter Card -->
    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white mb-4">
      <div class="d-flex align-items-center justify-content-between mb-3">
        <h6 class="fw-bold text-dark mb-0 d-flex align-items-center gap-2">
          <i class="bi bi-funnel-fill text-primary"></i>
          <span>Bộ Lọc Chi Tiết</span>
        </h6>
        <button class="btn btn-sm btn-link text-decoration-none text-muted" @click="resetFilters">
          <i class="bi bi-arrow-counterclockwise me-1"></i> Đặt lại tất cả
        </button>
      </div>

      <div class="row g-3">
        <!-- Keyword -->
        <div class="col-md-4">
          <label class="form-label small fw-semibold text-secondary">Từ khóa tìm kiếm</label>
          <div class="input-group">
            <span class="input-group-text bg-light border-end-0"><i class="bi bi-search text-muted"></i></span>
            <input 
              v-model="filters.keyword" 
              type="text" 
              class="form-control bg-light border-start-0" 
              placeholder="Tìm theo mã hoặc tên đợt..." 
              @keyup.enter="applyFilters"
            />
            <button class="btn btn-light border" v-if="filters.keyword" @click="filters.keyword = ''; applyFilters()">
              <i class="bi bi-x"></i>
            </button>
          </div>
        </div>

        <!-- Start Date -->
        <div class="col-sm-6 col-md-4">
          <label class="form-label small fw-semibold text-secondary">Từ ngày bắt đầu</label>
          <input 
            v-model="filters.startDate" 
            type="date" 
            class="form-control bg-light" 
            @change="applyFilters"
          />
        </div>

        <!-- End Date -->
        <div class="col-sm-6 col-md-4">
          <label class="form-label small fw-semibold text-secondary">Đến ngày kết thúc</label>
          <input 
            v-model="filters.endDate" 
            type="date" 
            class="form-control bg-light" 
            @change="applyFilters"
          />
        </div>

        <!-- Hình thức -->
        <div class="col-sm-6 col-md-4">
          <label class="form-label small fw-semibold text-secondary">Hình thức chiến dịch</label>
          <select v-model="filters.hinhThuc" class="form-select bg-light" @change="applyFilters">
            <option value="all">Tất cả hình thức</option>
            <option value="Công khai">Công khai / Theo mùa</option>
            <option value="Đặc biệt">Đặc biệt / Flash Sale</option>
          </select>
        </div>

        <!-- Trạng thái -->
        <div class="col-sm-6 col-md-4">
          <label class="form-label small fw-semibold text-secondary">Trạng thái diễn ra</label>
          <select v-model="filters.trangThai" class="form-select bg-light" @change="applyFilters">
            <option value="all">Tất cả trạng thái</option>
            <option value="Đang diễn ra">Đang diễn ra</option>
            <option value="Sắp diễn ra">Sắp diễn ra</option>
            <option value="Đã kết thúc">Đã kết thúc</option>
          </select>
        </div>

        <!-- Mức giảm giá -->
        <div class="col-md-4">
          <label class="form-label small fw-semibold text-secondary">Mức chiết khấu</label>
          <select v-model="filters.mucGiam" class="form-select bg-light" @change="applyFilters">
            <option value="all">Tất cả mức giảm</option>
            <option value="under15">Dưới 15%</option>
            <option value="15to25">Từ 15% - 25%</option>
            <option value="over25">Trên 25%</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Table Card -->
    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h6 class="fw-bold text-dark mb-0">
          Danh Sách Đợt Giảm Giá ({{ displayedCampaigns.length }})
        </h6>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Đang tải...</span>
        </div>
        <p class="text-muted mt-2 small">Đang tải dữ liệu đợt giảm giá...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="displayedCampaigns.length === 0" class="text-center py-5">
        <i class="bi bi-megaphone text-muted display-4"></i>
        <h6 class="fw-bold mt-3 text-secondary">Không tìm thấy đợt giảm giá nào</h6>
        <p class="text-muted small">Hãy bấm nút "Tạo Đợt Giảm Giá" để bắt đầu chương trình mới</p>
        <button class="btn btn-sm btn-outline-primary" @click="openAddModal">
          <i class="bi bi-plus"></i> Tạo Đợt Mới
        </button>
      </div>

      <!-- Table -->
      <div v-else class="table-responsive">
        <table class="table table-hover align-middle custom-table">
          <thead>
            <tr>
              <th style="width: 50px;">STT</th>
              <th style="width: 120px;">Mã Đợt</th>
              <th>Tên Đợt Giảm Giá</th>
              <th>Hình Thức</th>
              <th>Mức Ưu Đãi</th>
              <th style="width: 100px;">Số Lượng</th>
              <th>Thời Gian Diễn Ra</th>
              <th class="text-center">Trạng Thái</th>
              <th class="text-center" style="width: 120px;">Thao Tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(km, idx) in pagedCampaigns" :key="km.id">
              <td class="text-muted small">{{ page * size + idx + 1 }}</td>
              <td>
                <span class="badge bg-primary-subtle text-primary font-monospace fs-6 px-2 py-1 border border-primary-subtle">
                  {{ km.ma || km.maDotGiamGia }}
                </span>
              </td>
              <td>
                <div class="fw-bold text-dark">{{ km.ten || km.tenDotGiamGia }}</div>
              </td>
              <td>
                <span class="badge bg-light text-dark border px-2 py-1">
                  {{ km.loai || (km.hinhThuc === 'FLASH_SALE' ? 'Đặc biệt' : 'Công khai') }}
                </span>
              </td>
              <td>
                <div class="fw-bold text-success fs-6">
                  {{ km.giaTriGiam || (km.phanTramGiam + ' %') }}
                </div>
              </td>
              <td>
                <span class="badge bg-secondary-subtle text-secondary px-2 py-1">
                  {{ km.soLuong || 1 }}
                </span>
              </td>
              <td>
                <div class="small text-muted">
                  <div><i class="bi bi-clock me-1 text-primary"></i>{{ km.ngayBatDau }}</div>
                  <div><i class="bi bi-calendar-check me-1 text-danger"></i>{{ km.ngayKetThuc }}</div>
                </div>
              </td>
              <td class="text-center">
                <span 
                  class="badge rounded-pill px-3 py-2"
                  :class="getCampaignBadgeClass(km.trangThaiText || km.trangThai)"
                >
                  {{ km.trangThaiText || (km.trangThai === 1 ? 'Đang diễn ra' : 'Đã kết thúc') }}
                </span>
              </td>
              <td class="text-center">
                <div class="btn-group btn-group-sm">
                  <button class="btn btn-outline-secondary" @click="viewCampaign(km)" title="Xem chi tiết">
                    <i class="bi bi-eye"></i>
                  </button>
                  <button class="btn btn-outline-primary" @click="openEditModal(km)" title="Chỉnh sửa">
                    <i class="bi bi-pencil"></i>
                  </button>
                  <button 
                    class="btn btn-outline-danger" 
                    @click="toggleStatus(km)" 
                    :title="km.trangThai === 1 ? 'Khóa đợt' : 'Kích hoạt lại'"
                  >
                    <i :class="km.trangThai === 1 ? 'bi bi-slash-circle' : 'bi bi-check-circle'"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Full Pagination System -->
      <div class="d-flex flex-wrap justify-content-between align-items-center mt-4 pt-3 border-top gap-3" v-if="displayedCampaigns.length > 0">
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
            Hiển thị <strong>{{ paginationFrom }} - {{ paginationTo }}</strong> trên tổng số <strong>{{ totalElements }}</strong> đợt giảm giá
          </span>
        </div>

        <nav aria-label="Campaign navigation" v-if="totalPages > 0">
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

    <!-- Modal Thêm / Chỉnh Sửa Đợt Giảm Giá -->
    <div v-if="isModalOpen" class="modal-backdrop-custom" @click.self="isModalOpen = false">
      <div class="modal-card-custom animate-scale-up">
        <div class="modal-header-custom">
          <h5 class="modal-title-custom fw-bold d-flex align-items-center gap-2">
            <i class="bi bi-percent text-primary"></i>
            <span>{{ isEditMode ? 'Chỉnh Sửa Đợt Giảm Giá' : 'Tạo Đợt Giảm Giá Mới' }}</span>
          </h5>
          <button class="btn-close-custom" @click="isModalOpen = false">&times;</button>
        </div>
        <form @submit.prevent="saveCampaign" class="modal-body-custom">
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Mã Đợt Giảm Giá <span class="text-danger">*</span></label>
              <input v-model="formKm.ma" type="text" class="form-control font-monospace text-uppercase" required placeholder="DGG04" />
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Tên Đợt Giảm Giá <span class="text-danger">*</span></label>
              <input v-model="formKm.ten" type="text" class="form-control" required placeholder="Lễ hội áo gió..." />
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Hình Thức Chiến Dịch</label>
              <select v-model="formKm.loai" class="form-select">
                <option value="Công khai">Công khai / Theo mùa</option>
                <option value="Đặc biệt">Đặc biệt / Flash Sale</option>
              </select>
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Giá Trị Giảm (%) <span class="text-danger">*</span></label>
              <input v-model="formKm.giaTriGiam" type="text" class="form-control" required placeholder="20 %" />
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Số Lượng Áp Dụng</label>
              <input v-model.number="formKm.soLuong" type="number" class="form-control" min="1" required placeholder="100" />
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Trạng Thái Ban Đầu</label>
              <select v-model="formKm.trangThai" class="form-select">
                <option value="Đang diễn ra">Đang diễn ra</option>
                <option value="Sắp diễn ra">Sắp diễn ra</option>
                <option value="Đã kết thúc">Đã kết thúc</option>
              </select>
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Ngày & Giờ Bắt Đầu</label>
              <input v-model="formKm.ngayBatDau" type="datetime-local" class="form-control" />
            </div>
            <div class="col-md-6">
              <label class="form-label small fw-semibold">Ngày & Giờ Kết Thúc</label>
              <input v-model="formKm.ngayKetThuc" type="datetime-local" class="form-control" />
            </div>
          </div>
          <div class="modal-footer-custom mt-4 d-flex justify-content-end gap-2 border-top pt-3">
            <button type="button" class="btn btn-light px-4 border" @click="isModalOpen = false">Hủy</button>
            <button type="submit" class="btn btn-primary px-4 fw-medium shadow-sm">
              {{ isEditMode ? 'Lưu Thay Đổi' : 'Tạo Chiến Dịch' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { danhSachDotGiamGia } from '@/data/mockData.js'
import { discountService } from '@/services/api.js'

const campaigns = ref([])
const loading = ref(false)
const isModalOpen = ref(false)
const isEditMode = ref(false)
const editCampaignId = ref(null)

// Pagination states
const page = ref(0)
const size = ref(10)

const filters = reactive({
  keyword: '',
  startDate: '',
  endDate: '',
  hinhThuc: 'all',
  trangThai: 'all',
  mucGiam: 'all'
})

const formKm = reactive({
  ma: '',
  ten: '',
  loai: 'Công khai',
  giaTriGiam: '20 %',
  soLuong: 100,
  trangThai: 'Đang diễn ra',
  ngayBatDau: '',
  ngayKetThuc: ''
})

// Load data
const loadCampaigns = async () => {
  loading.value = true
  try {
    const res = await discountService.getCampaigns()
    const raw = (res && res.data) ? res.data : res
    if (raw && (raw.content || Array.isArray(raw))) {
      campaigns.value = raw.content || raw
    } else {
      campaigns.value = [...danhSachDotGiamGia]
    }
  } catch (err) {
    console.warn('API getCampaigns failed, fallback to mock data:', err)
    campaigns.value = [...danhSachDotGiamGia]
  } finally {
    loading.value = false
  }
}

// Stats
const activeCount = computed(() => campaigns.value.filter(c => (c.trangThaiText === 'Đang diễn ra' || c.trangThai === 1)).length)
const upcomingCount = computed(() => campaigns.value.filter(c => (c.trangThaiText === 'Sắp diễn ra')).length)
const endedCount = computed(() => campaigns.value.filter(c => (c.trangThaiText === 'Đã kết thúc' || c.trangThai === 0)).length)

// Filtered List
const displayedCampaigns = computed(() => {
  return campaigns.value.filter(km => {
    // Keyword
    if (filters.keyword.trim()) {
      const kw = filters.keyword.toLowerCase().trim()
      const maStr = (km.ma || km.maDotGiamGia || '').toLowerCase()
      const tenStr = (km.ten || km.tenDotGiamGia || '').toLowerCase()
      if (!maStr.includes(kw) && !tenStr.includes(kw)) return false
    }

    // Type
    if (filters.hinhThuc !== 'all') {
      const loaiStr = km.loai || (km.hinhThuc === 'FLASH_SALE' ? 'Đặc biệt' : 'Công khai')
      if (loaiStr !== filters.hinhThuc) return false
    }

    // Status
    if (filters.trangThai !== 'all') {
      const statusStr = km.trangThaiText || (km.trangThai === 1 ? 'Đang diễn ra' : 'Đã kết thúc')
      if (statusStr !== filters.trangThai) return false
    }

    // Discount tier
    if (filters.mucGiam !== 'all') {
      const valNum = parseInt(km.giaTriGiam || km.phanTramGiam || 0)
      if (filters.mucGiam === 'under15' && valNum >= 15) return false
      if (filters.mucGiam === '15to25' && (valNum < 15 || valNum > 25)) return false
      if (filters.mucGiam === 'over25' && valNum <= 25) return false
    }

    return true
  })
})

// Pagination Computed
const totalElements = computed(() => displayedCampaigns.value.length)
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

const pagedCampaigns = computed(() => {
  const start = page.value * size.value
  return displayedCampaigns.value.slice(start, start + size.value)
})

function applyFilters() {
  page.value = 0
}

function resetFilters() {
  filters.keyword = ''
  filters.startDate = ''
  filters.endDate = ''
  filters.hinhThuc = 'all'
  filters.trangThai = 'all'
  filters.mucGiam = 'all'
  page.value = 0
}

function getCampaignBadgeClass(status) {
  if (status === 'Đang diễn ra' || status === 1) return 'bg-success-subtle text-success border border-success-subtle'
  if (status === 'Sắp diễn ra') return 'bg-info-subtle text-info border border-info-subtle'
  return 'bg-secondary-subtle text-secondary border border-secondary-subtle'
}

function openAddModal() {
  isEditMode.value = false
  editCampaignId.value = null
  Object.assign(formKm, {
    ma: 'DGG0' + (campaigns.value.length + 1),
    ten: '',
    loai: 'Công khai',
    giaTriGiam: '20 %',
    soLuong: 100,
    trangThai: 'Đang diễn ra',
    ngayBatDau: '',
    ngayKetThuc: ''
  })
  isModalOpen.value = true
}

function openEditModal(km) {
  isEditMode.value = true
  editCampaignId.value = km.id
  Object.assign(formKm, {
    ma: km.ma || km.maDotGiamGia,
    ten: km.ten || km.tenDotGiamGia,
    loai: km.loai || (km.hinhThuc === 'FLASH_SALE' ? 'Đặc biệt' : 'Công khai'),
    giaTriGiam: km.giaTriGiam || (km.phanTramGiam + ' %'),
    soLuong: km.soLuong || 100,
    trangThai: km.trangThaiText || (km.trangThai === 1 ? 'Đang diễn ra' : 'Đã kết thúc'),
    ngayBatDau: '',
    ngayKetThuc: ''
  })
  isModalOpen.value = true
}

function saveCampaign() {
  if (isEditMode.value) {
    const idx = campaigns.value.findIndex(c => c.id === editCampaignId.value)
    if (idx !== -1) {
      campaigns.value[idx] = {
        ...campaigns.value[idx],
        ma: formKm.ma,
        maDotGiamGia: formKm.ma,
        ten: formKm.ten,
        tenDotGiamGia: formKm.ten,
        loai: formKm.loai,
        giaTriGiam: formKm.giaTriGiam,
        soLuong: formKm.soLuong,
        trangThaiText: formKm.trangThai,
        trangThai: formKm.trangThai === 'Đang diễn ra' ? 1 : 0
      }
    }
  } else {
    campaigns.value.unshift({
      id: Date.now(),
      ma: formKm.ma,
      maDotGiamGia: formKm.ma,
      ten: formKm.ten,
      tenDotGiamGia: formKm.ten,
      loai: formKm.loai,
      giaTriGiam: formKm.giaTriGiam,
      soLuong: formKm.soLuong,
      ngayBatDau: '00:00:00 01/05/2026',
      ngayKetThuc: '23:59:59 31/12/2026',
      trangThai: formKm.trangThai === 'Đang diễn ra' ? 1 : 0,
      trangThaiText: formKm.trangThai
    })
  }
  isModalOpen.value = false
}

function toggleStatus(km) {
  if (km.trangThai === 1) {
    km.trangThai = 0
    km.trangThaiText = 'Đã kết thúc'
  } else {
    km.trangThai = 1
    km.trangThaiText = 'Đang diễn ra'
  }
}

function viewCampaign(km) {
  alert(`Chi tiết chiến dịch:\n\n- Tên: ${km.ten || km.tenDotGiamGia}\n- Mã: ${km.ma || km.maDotGiamGia}\n- Mức giảm: ${km.giaTriGiam || km.phanTramGiam + '%'}\n- Thời gian: ${km.ngayBatDau} ~ ${km.ngayKetThuc}\n- Trạng thái: ${km.trangThaiText || (km.trangThai === 1 ? 'Đang diễn ra' : 'Đã kết thúc')}`)
}

onMounted(() => {
  loadCampaigns()
})
</script>

<style scoped>
.windfit-campaign-page {
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

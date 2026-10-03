<template>
  <div class="attribute-management-page">
    <!-- Header -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-2">
      <div>
        <h4 class="fw-bold mb-1">Thuộc Tính Sản Phẩm Áo Khoác Gió</h4>
        <p class="text-muted mb-0 small">Quản lý các đặc tính kỹ thuật thể thao WindFit (Chất liệu, Môn thể thao, Kiểu dáng, Màu sắc, Kích cỡ...)</p>
      </div>
      <button class="btn btn-primary d-flex align-items-center gap-2 shadow-sm px-3" @click="openCreateModal">
        <i class="bi bi-plus-circle-fill"></i>
        <span>Thêm {{ currentTabMeta.label }} Mới</span>
      </button>
    </div>

    <!-- Attribute Tabs -->
    <div class="card border-0 shadow-sm rounded-4 mb-4 p-2 bg-white">
      <ul class="nav nav-pills flex-nowrap overflow-auto gap-2 p-1 custom-pills">
        <li class="nav-item" v-for="tab in tabs" :key="tab.type">
          <button 
            class="nav-link tab-btn d-flex align-items-center gap-2"
            :class="{ active: currentTab === tab.type }"
            @click="switchTab(tab.type)"
          >
            <i :class="tab.icon"></i>
            <span>{{ tab.label }}</span>
            <span class="badge rounded-pill bg-light text-dark ms-1" v-if="itemsMap[tab.type]">
              {{ itemsMap[tab.type].length }}
            </span>
          </button>
        </li>
      </ul>
    </div>

    <!-- Main Content Table Card -->
    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
      <!-- Search & Filter Bar -->
      <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
        <div class="input-group search-input-group" style="max-width: 380px;">
          <span class="input-group-text bg-light border-end-0"><i class="bi bi-search text-muted"></i></span>
          <input 
            type="text" 
            v-model="searchQuery" 
            class="form-control bg-light border-start-0" 
            :placeholder="`Tìm kiếm ${currentTabMeta.label.toLowerCase()} theo mã, tên...`" 
          />
          <button class="btn btn-light border" v-if="searchQuery" @click="searchQuery = ''">
            <i class="bi bi-x"></i>
          </button>
        </div>

        <div class="d-flex align-items-center gap-2">
          <span class="text-muted small">Lọc trạng thái:</span>
          <select class="form-select form-select-sm" v-model="filterStatus" style="width: 140px;">
            <option value="ALL">Tất cả</option>
            <option value="1">Đang hoạt động</option>
            <option value="0">Đã khóa</option>
          </select>
          <button class="btn btn-outline-secondary btn-sm" @click="fetchData" title="Tải lại">
            <i class="bi bi-arrow-clockwise"></i>
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="text-muted small mt-2">Đang tải dữ liệu thuộc tính...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="filteredItems.length === 0" class="text-center py-5">
        <i class="bi bi-inbox fs-1 text-muted"></i>
        <p class="text-muted mt-2">Không tìm thấy thuộc tính nào phù hợp.</p>
      </div>

      <!-- Table View -->
      <div v-else class="table-responsive">
        <table class="table table-hover align-middle">
          <thead class="table-light">
            <tr>
              <th style="width: 60px;">#</th>
              <th style="width: 180px;">Mã Thuộc Tính</th>
              <th>Tên {{ currentTabMeta.label }}</th>
              <th style="width: 160px;" class="text-center">Trạng Thái</th>
              <th style="width: 140px;" class="text-center">Thao Tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in filteredItems" :key="item.id">
              <td class="text-muted small">{{ index + 1 }}</td>
              <td>
                <span class="badge bg-light text-dark border font-monospace px-2 py-1">
                  {{ item.ma }}
                </span>
              </td>
              <td>
                <span class="fw-semibold text-dark">{{ item.ten }}</span>
              </td>
              <td class="text-center">
                <span 
                  class="badge rounded-pill px-3 py-2 cursor-pointer"
                  :class="item.trangThai === 1 ? 'bg-success-subtle text-success border border-success-subtle' : 'bg-secondary-subtle text-secondary border border-secondary-subtle'"
                  @click="handleToggleStatus(item)"
                  title="Nhấn để đổi trạng thái"
                >
                  <i :class="item.trangThai === 1 ? 'bi bi-check-circle-fill me-1' : 'bi bi-slash-circle me-1'"></i>
                  {{ item.trangThai === 1 ? 'Hoạt động' : 'Đã khóa' }}
                </span>
              </td>
              <td class="text-center">
                <div class="btn-group btn-group-sm">
                  <button class="btn btn-outline-primary" @click="openEditModal(item)" title="Chỉnh sửa">
                    <i class="bi bi-pencil-square"></i>
                  </button>
                  <button 
                    class="btn" 
                    :class="item.trangThai === 1 ? 'btn-outline-danger' : 'btn-outline-success'"
                    @click="handleToggleStatus(item)" 
                    :title="item.trangThai === 1 ? 'Khóa thuộc tính' : 'Kích hoạt lại'"
                  >
                    <i :class="item.trangThai === 1 ? 'bi bi-lock' : 'bi bi-unlock'"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal Thêm / Sửa Thuộc Tính -->
    <div class="modal fade" id="attributeModal" tabindex="-1" ref="modalRef">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
          <div class="modal-header border-bottom-0 pb-0">
            <h5 class="modal-title fw-bold text-dark">
              {{ isEditing ? 'Chỉnh Sửa' : 'Thêm Mới' }} {{ currentTabMeta.label }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>

          <form @submit.prevent="saveAttribute">
            <div class="modal-body py-3">
              <!-- Mã thuộc tính -->
              <div class="mb-3">
                <label class="form-label small fw-bold">Mã {{ currentTabMeta.label }} <span class="text-danger">*</span></label>
                <input 
                  type="text" 
                  v-model="formData.ma" 
                  class="form-control font-monospace" 
                  :placeholder="`VD: ${currentTabMeta.prefix}01`" 
                  required 
                  :disabled="isEditing" 
                />
                <small class="text-muted">Mã là duy nhất, không thể thay đổi sau khi tạo.</small>
              </div>

              <!-- Tên thuộc tính -->
              <div class="mb-3">
                <label class="form-label small fw-bold">Tên {{ currentTabMeta.label }} <span class="text-danger">*</span></label>
                <input 
                  type="text" 
                  v-model="formData.ten" 
                  class="form-control" 
                  :placeholder="`Nhập tên ${currentTabMeta.label.toLowerCase()}...`" 
                  required 
                />
              </div>

              <!-- Trạng thái -->
              <div class="mb-3">
                <label class="form-label small fw-bold">Trạng thái</label>
                <select class="form-select" v-model="formData.trangThai">
                  <option :value="1">Hoạt động</option>
                  <option :value="0">Khóa</option>
                </select>
              </div>
            </div>

            <div class="modal-footer border-top-0 pt-0">
              <button type="button" class="btn btn-light" data-bs-dismiss="modal">Hủy</button>
              <button type="submit" class="btn btn-primary fw-bold px-4" :disabled="submitting">
                <span v-if="submitting" class="spinner-border spinner-border-sm me-1"></span>
                <span>{{ isEditing ? 'Lưu Thay Đổi' : 'Thêm Mới' }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import { attributeService } from '../../services/api'

const tabs = [
  { type: 'danh-muc', label: 'Danh Mục', icon: 'bi bi-folder-fill', prefix: 'DM_' },
  { type: 'thuong-hieu', label: 'Thương Hiệu', icon: 'bi bi-award-fill', prefix: 'TH_' },
  { type: 'chat-lieu', label: 'Chất Liệu', icon: 'bi bi-moisture', prefix: 'CL_' },
  { type: 'xuat-xu', label: 'Xuất Xứ', icon: 'bi bi-globe-americas', prefix: 'XX_' },
  { type: 'kieu-dang', label: 'Kiểu Dáng', icon: 'bi bi-person-standing', prefix: 'KD_' },
  { type: 'mon-the-thao', label: 'Môn Thể Thao', icon: 'bi bi-bicycle', prefix: 'MT_' },
  { type: 'mau-sac', label: 'Màu Sắc', icon: 'bi bi-palette-fill', prefix: 'MS_' },
  { type: 'kich-co', label: 'Kích Cỡ', icon: 'bi bi-arrows-angle-expand', prefix: 'SZ_' }
]

const currentTab = ref('danh-muc')
const currentTabMeta = computed(() => tabs.find(t => t.type === currentTab.value) || tabs[0])

const itemsMap = ref({})
const loading = ref(false)
const searchQuery = ref('')
const filterStatus = ref('ALL')

// Modal state
const modalRef = ref(null)
let bsModal = null
const isEditing = ref(false)
const submitting = ref(false)
const formData = ref({
  id: null,
  ma: '',
  ten: '',
  trangThai: 1
})

const currentItems = computed(() => itemsMap.value[currentTab.value] || [])

const filteredItems = computed(() => {
  return currentItems.value.filter(item => {
    const q = searchQuery.value.toLowerCase().trim()
    const matchesSearch = !q || item.ma.toLowerCase().includes(q) || item.ten.toLowerCase().includes(q)
    const matchesStatus = filterStatus.value === 'ALL' || String(item.trangThai) === filterStatus.value
    return matchesSearch && matchesStatus
  })
})

async function fetchTabItems(type) {
  try {
    const res = await attributeService.getByType(type)
    if (res.success && res.data) {
      itemsMap.value[type] = res.data
    }
  } catch (err) {
    console.error(`Lỗi tải dữ liệu ${type}:`, err)
  }
}

async function fetchData() {
  loading.value = true
  try {
    await fetchTabItems(currentTab.value)
  } finally {
    loading.value = false
  }
}

function switchTab(type) {
  currentTab.value = type
  searchQuery.value = ''
  filterStatus.value = 'ALL'
  if (!itemsMap.value[type]) {
    fetchData()
  }
}

function openCreateModal() {
  isEditing.value = false
  formData.value = {
    id: null,
    ma: currentTabMeta.value.prefix + (currentItems.value.length + 1).toString().padStart(2, '0'),
    ten: '',
    trangThai: 1
  }
  bsModal?.show()
}

function openEditModal(item) {
  isEditing.value = true
  formData.value = {
    id: item.id,
    ma: item.ma,
    ten: item.ten,
    trangThai: item.trangThai
  }
  bsModal?.show()
}

async function saveAttribute() {
  submitting.value = true
  try {
    if (isEditing.value) {
      const res = await attributeService.update(currentTab.value, formData.value.id, formData.value)
      if (res.success) {
        bsModal?.hide()
        await fetchData()
      } else {
        alert(res.message || 'Lỗi cập nhật!')
      }
    } else {
      const res = await attributeService.create(currentTab.value, formData.value)
      if (res.success) {
        bsModal?.hide()
        await fetchData()
      } else {
        alert(res.message || 'Lỗi tạo mới!')
      }
    }
  } catch (err) {
    alert(err.message || 'Lỗi xử lý yêu cầu!')
  } finally {
    submitting.value = false
  }
}

async function handleToggleStatus(item) {
  const actionText = item.trangThai === 1 ? 'khóa' : 'kích hoạt lại'
  if (!confirm(`Bạn có chắc chắn muốn ${actionText} thuộc tính "${item.ten}"?`)) return

  try {
    const res = await attributeService.toggleStatus(currentTab.value, item.id)
    if (res.success) {
      await fetchData()
    } else {
      alert(res.message || 'Lỗi đổi trạng thái!')
    }
  } catch (err) {
    alert(err.message || 'Lỗi thao tác!')
  }
}

onMounted(async () => {
  if (modalRef.value) {
    bsModal = new Modal(modalRef.value)
  }
  // Load initial tab
  await fetchData()
  // Prefetch other tabs in background
  for (const t of tabs) {
    if (t.type !== currentTab.value) {
      fetchTabItems(t.type)
    }
  }
})
</script>

<style scoped>
.custom-pills .tab-btn {
  background: #f1f5f9;
  color: #475569;
  border-radius: 12px;
  padding: 0.6rem 1.15rem;
  font-size: 0.88rem;
  font-weight: 600;
  white-space: nowrap;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.custom-pills .tab-btn:hover {
  background: #e2e8f0;
  color: #0f172a;
}

.custom-pills .tab-btn.active {
  background: linear-gradient(135deg, #0284c7 0%, #0284c7 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(2, 132, 199, 0.3);
}

.custom-pills .tab-btn.active .badge {
  background: rgba(255, 255, 255, 0.25) !important;
  color: #ffffff !important;
}

.search-input-group .form-control:focus {
  background-color: #ffffff;
}

.cursor-pointer {
  cursor: pointer;
  user-select: none;
}
</style>

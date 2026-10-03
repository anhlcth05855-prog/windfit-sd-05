<template>
  <div class="app-pagination d-flex flex-wrap justify-content-between align-items-center gap-3 py-3 border-top">
    <!-- Info & Page size selector -->
    <div class="d-flex align-items-center gap-3 text-muted small">
      <div>
        <span>Hiển thị </span>
        <span class="fw-bold text-dark">{{ startItem }}</span>
        <span> - </span>
        <span class="fw-bold text-dark">{{ endItem }}</span>
        <span> trong tổng số </span>
        <span class="fw-bold text-primary">{{ totalElements }}</span>
        <span> bản ghi</span>
      </div>

      <div class="d-flex align-items-center gap-2">
        <span class="d-none d-sm-inline">Số dòng:</span>
        <select
          class="form-select form-select-sm page-size-select py-1 px-2 border rounded-2"
          :value="pageSize"
          @change="onSizeChange"
        >
          <option v-for="opt in pageSizeOptions" :key="opt" :value="opt">
            {{ opt }} / trang
          </option>
        </select>
      </div>
    </div>

    <!-- Paging buttons -->
    <div class="d-flex align-items-center" v-if="totalPages > 0">
      <ul class="pagination pagination-sm mb-0 gap-1">
        <!-- First Page -->
        <li class="page-item" :class="{ disabled: currentPage === 0 }">
          <button
            class="page-link rounded-2"
            type="button"
            title="Trang đầu"
            :disabled="currentPage === 0"
            @click="changePage(0)"
          >
            <i class="bi bi-chevron-double-left"></i>
          </button>
        </li>

        <!-- Previous Page -->
        <li class="page-item" :class="{ disabled: currentPage === 0 }">
          <button
            class="page-link rounded-2"
            type="button"
            title="Trang trước"
            :disabled="currentPage === 0"
            @click="changePage(currentPage - 1)"
          >
            <i class="bi bi-chevron-left"></i>
          </button>
        </li>

        <!-- Page Numbers with Ellipsis -->
        <li
          v-for="(p, index) in visiblePages"
          :key="index"
          class="page-item"
          :class="{ active: p === currentPage, disabled: p === '...' }"
        >
          <button
            v-if="p !== '...'"
            class="page-link rounded-2"
            type="button"
            @click="changePage(p)"
          >
            {{ p + 1 }}
          </button>
          <span v-else class="page-link border-0 text-muted px-2">...</span>
        </li>

        <!-- Next Page -->
        <li class="page-item" :class="{ disabled: currentPage >= totalPages - 1 }">
          <button
            class="page-link rounded-2"
            type="button"
            title="Trang sau"
            :disabled="currentPage >= totalPages - 1"
            @click="changePage(currentPage + 1)"
          >
            <i class="bi bi-chevron-right"></i>
          </button>
        </li>

        <!-- Last Page -->
        <li class="page-item" :class="{ disabled: currentPage >= totalPages - 1 }">
          <button
            class="page-link rounded-2"
            type="button"
            title="Trang cuối"
            :disabled="currentPage >= totalPages - 1"
            @click="changePage(totalPages - 1)"
          >
            <i class="bi bi-chevron-double-right"></i>
          </button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  currentPage: {
    type: Number,
    default: 0
  },
  pageSize: {
    type: Number,
    default: 10
  },
  totalElements: {
    type: Number,
    default: 0
  },
  totalPages: {
    type: Number,
    default: 0
  },
  pageSizeOptions: {
    type: Array,
    default: () => [5, 10, 20, 50]
  }
})

const emit = defineEmits(['page-change', 'size-change'])

const startItem = computed(() => {
  if (props.totalElements === 0) return 0
  return props.currentPage * props.pageSize + 1
})

const endItem = computed(() => {
  if (props.totalElements === 0) return 0
  return Math.min((props.currentPage + 1) * props.pageSize, props.totalElements)
})

const visiblePages = computed(() => {
  const total = props.totalPages
  const current = props.currentPage

  if (total <= 7) {
    return Array.from({ length: total }, (_, i) => i)
  }

  const pages = []
  pages.push(0)

  if (current > 3) {
    pages.push('...')
  }

  const start = Math.max(1, current - 1)
  const end = Math.min(total - 2, current + 1)

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  if (current < total - 4) {
    pages.push('...')
  }

  pages.push(total - 1)
  return pages
})

function changePage(page) {
  if (page < 0 || page >= props.totalPages || page === props.currentPage) return
  emit('page-change', page)
}

function onSizeChange(e) {
  const newSize = parseInt(e.target.value, 10)
  emit('size-change', newSize)
}
</script>

<style scoped>
.page-size-select {
  width: auto;
  font-size: 0.85rem;
  background-color: #f8fafc;
}

.page-link {
  color: #475569;
  border-color: #e2e8f0;
  font-weight: 500;
  padding: 0.35rem 0.65rem;
  min-width: 32px;
  text-align: center;
  transition: all 0.15s ease-in-out;
}

.page-link:hover {
  background-color: #f1f5f9;
  color: #0f172a;
}

.page-item.active .page-link {
  background-color: #0284c7;
  border-color: #0284c7;
  color: #ffffff;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(2, 132, 199, 0.25);
}

.page-item.disabled .page-link {
  color: #94a3b8;
  background-color: #f8fafc;
  border-color: #e2e8f0;
  opacity: 0.65;
}
</style>

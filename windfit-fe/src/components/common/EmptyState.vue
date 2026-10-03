<template>
  <div class="empty-state-card text-center p-5 rounded-4 bg-light bg-opacity-50 border border-dashed my-3">
    <div class="empty-icon-wrap rounded-circle bg-white shadow-sm d-inline-flex align-items-center justify-content-center mb-3">
      <i :class="icon" class="fs-1 text-muted"></i>
    </div>
    <h6 class="fw-bold text-dark mb-1">{{ title }}</h6>
    <p class="text-muted small mb-3 mx-auto" style="max-width: 360px;">{{ description }}</p>
    <div v-if="$slots.action" class="mt-2">
      <slot name="action"></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  icon: {
    type: String,
    default: 'bi bi-inbox'
  },
  title: {
    type: String,
    default: 'Không có dữ liệu'
  },
  description: {
    type: String,
    default: 'Hiện tại chưa có bản ghi nào phù hợp với bộ lọc tìm kiếm.'
  }
})
</script>

<style scoped>
.empty-state-card {
  border-color: #cbd5e1 !important;
}

.empty-icon-wrap {
  width: 64px;
  height: 64px;
}
</style>

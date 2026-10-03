<template>
  <div class="loading-state-card text-center p-5 rounded-4 bg-light bg-opacity-25 my-3">
    <div class="spinner-grow text-primary mb-3" style="width: 2.5rem; height: 2.5rem;" role="status">
      <span class="visually-hidden">Đang tải...</span>
    </div>
    <div class="fw-semibold text-dark mb-1 small">{{ text }}</div>
    <div class="text-muted" style="font-size: 0.78rem;">Vui lòng đợi trong giây lát...</div>
  </div>
</template>

<script setup>
defineProps({
  text: {
    type: String,
    default: 'Đang tải dữ liệu từ máy chủ...'
  }
})
</script>

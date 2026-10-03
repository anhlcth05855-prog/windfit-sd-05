<template>
  <div class="filter-bar-card card border-0 shadow-sm rounded-4 p-3 bg-white mb-4">
    <div class="row g-2 align-items-center">
      <!-- Search Keyword Input with 300ms debounce -->
      <div class="col-12 col-md-4 col-lg-3">
        <div class="input-group search-input-group">
          <span class="input-group-text bg-light border-end-0 text-muted">
            <i class="bi bi-search"></i>
          </span>
          <input
            type="text"
            class="form-control bg-light border-start-0 ps-0"
            :placeholder="searchPlaceholder"
            :value="keyword"
            @input="onInputKeyword"
          />
          <button
            v-if="keyword"
            class="btn btn-light border-start-0 text-muted"
            type="button"
            @click="clearKeyword"
          >
            <i class="bi bi-x-circle-fill"></i>
          </button>
        </div>
      </div>

      <!-- Additional Filters Slot -->
      <div class="col-12 col-md-auto flex-grow-1">
        <div class="row g-2 align-items-center">
          <slot></slot>
        </div>
      </div>

      <!-- Reset button -->
      <div class="col-auto ms-auto d-flex align-items-center gap-2">
        <button
          type="button"
          class="btn btn-light border text-muted d-flex align-items-center gap-1.5 px-3 py-1.5 rounded-3 btn-sm"
          title="Đặt lại bộ lọc"
          @click="$emit('reset')"
        >
          <i class="bi bi-arrow-counterclockwise"></i>
          <span>Đặt lại</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  keyword: {
    type: String,
    default: ''
  },
  searchPlaceholder: {
    type: String,
    default: 'Tìm kiếm theo mã, tên...'
  }
})

const emit = defineEmits(['update:keyword', 'search', 'reset'])

let debounceTimer = null

function onInputKeyword(e) {
  const val = e.target.value
  emit('update:keyword', val)

  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    emit('search', val)
  }, 300)
}

function clearKeyword() {
  emit('update:keyword', '')
  emit('search', '')
}
</script>

<style scoped>
.search-input-group .form-control:focus {
  background-color: #ffffff !important;
  box-shadow: none;
}
</style>

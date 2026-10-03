<template>
  <div class="toast-stack-container" aria-live="polite" aria-atomic="true">
    <transition-group name="toast-slide">
      <div
        v-for="t in toasts"
        :key="t.id"
        class="custom-toast-item shadow-lg rounded-3 d-flex align-items-start p-3"
        :class="`toast-${t.type}`"
        role="alert"
      >
        <div class="toast-icon me-3">
          <i v-if="t.type === 'success'" class="bi bi-check-circle-fill text-success fs-5"></i>
          <i v-else-if="t.type === 'danger'" class="bi bi-x-circle-fill text-danger fs-5"></i>
          <i v-else-if="t.type === 'warning'" class="bi bi-exclamation-triangle-fill text-warning fs-5"></i>
          <i v-else class="bi bi-info-circle-fill text-primary fs-5"></i>
        </div>
        <div class="toast-content flex-grow-1 me-2">
          <div v-if="t.title" class="toast-title fw-bold text-dark mb-1 small">{{ t.title }}</div>
          <div class="toast-message text-secondary small">{{ t.message }}</div>
        </div>
        <button
          type="button"
          class="btn-close btn-close-sm ms-auto"
          aria-label="Close"
          @click="removeToast(t.id)"
        ></button>
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { useToast } from '../../composables/useToast'

const { toasts, removeToast } = useToast()
</script>

<style scoped>
.toast-stack-container {
  position: fixed;
  top: 24px;
  right: 24px;
  z-index: 1090;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 380px;
  width: calc(100vw - 48px);
  pointer-events: none;
}

.custom-toast-item {
  pointer-events: auto;
  background: #ffffff;
  border-left: 4px solid #cbd5e1;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.custom-toast-item.toast-success {
  border-left: 5px solid #10b981;
}

.custom-toast-item.toast-danger {
  border-left: 5px solid #ef4444;
}

.custom-toast-item.toast-warning {
  border-left: 5px solid #f59e0b;
}

.custom-toast-item.toast-info {
  border-left: 5px solid #3b82f6;
}

.toast-slide-enter-active,
.toast-slide-leave-active {
  transition: all 0.35s ease;
}

.toast-slide-enter-from {
  opacity: 0;
  transform: translateX(40px) scale(0.95);
}

.toast-slide-leave-to {
  opacity: 0;
  transform: translateX(60px);
}
</style>

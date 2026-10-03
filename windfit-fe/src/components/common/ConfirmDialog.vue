<template>
  <teleport to="body">
    <transition name="confirm-fade">
      <div
        v-if="modelValue"
        class="confirm-overlay"
        tabindex="-1"
        @keydown.esc="onEsc"
        @click.self="onBackdropClick"
      >
        <div class="confirm-card shadow-2xl rounded-4" role="dialog" aria-modal="true">
          <!-- Icon & Header -->
          <div class="confirm-header d-flex flex-column align-items-center text-center p-4 pb-2">
            <div class="confirm-icon-box rounded-circle d-flex align-items-center justify-content-center mb-3" :class="`icon-${type}`">
              <i v-if="type === 'danger'" class="bi bi-trash3-fill fs-3"></i>
              <i v-else-if="type === 'warning'" class="bi bi-exclamation-triangle-fill fs-3"></i>
              <i v-else-if="type === 'success'" class="bi bi-check-lg fs-3"></i>
              <i v-else class="bi bi-question-circle-fill fs-3"></i>
            </div>
            <h5 class="fw-bold text-dark mb-1">{{ title }}</h5>
            <div class="text-muted small px-2 mt-1" v-if="message">
              <span v-html="message"></span>
            </div>
            <div v-if="targetName" class="badge bg-light text-primary border px-3 py-2 mt-2 font-monospace fs-6">
              {{ targetName }}
            </div>
          </div>

          <!-- Actions -->
          <div class="confirm-footer p-4 pt-3 d-flex gap-2 justify-content-center">
            <button
              type="button"
              class="btn btn-light px-4 py-2 border fw-medium rounded-3"
              :disabled="loading"
              @click="cancel"
            >
              {{ cancelText }}
            </button>
            <button
              type="button"
              class="btn px-4 py-2 fw-medium rounded-3 d-flex align-items-center gap-2"
              :class="confirmBtnClass"
              :disabled="loading"
              @click="confirm"
            >
              <span v-if="loading" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
              <span>{{ confirmText }}</span>
            </button>
          </div>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: 'Xác nhận thao tác'
  },
  message: {
    type: String,
    default: 'Bạn có chắc chắn muốn thực hiện hành động này?'
  },
  targetName: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'warning'
  },
  confirmText: {
    type: String,
    default: 'Xác nhận'
  },
  cancelText: {
    type: String,
    default: 'Hủy'
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])

const confirmBtnClass = computed(() => {
  switch (props.type) {
    case 'danger':
      return 'btn-danger shadow-sm'
    case 'warning':
      return 'btn-warning text-dark shadow-sm'
    case 'success':
      return 'btn-success shadow-sm'
    case 'info':
    default:
      return 'btn-primary shadow-sm'
  }
})

function confirm() {
  if (props.loading) return
  emit('confirm')
}

function cancel() {
  if (props.loading) return
  emit('update:modelValue', false)
  emit('cancel')
}

function onBackdropClick() {
  if (!props.loading) {
    cancel()
  }
}

function onEsc(e) {
  if (e.key === 'Escape' && !props.loading) {
    cancel()
  }
}

function handleKeydown(e) {
  if (e.key === 'Escape' && props.modelValue && !props.loading) {
    cancel()
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
.confirm-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(4px);
  z-index: 1080;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.25rem;
}

.confirm-card {
  background: #ffffff;
  width: 100%;
  max-width: 440px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.2), 0 10px 10px -5px rgba(0, 0, 0, 0.1);
  transform: translateY(0);
  transition: transform 0.2s cubic-bezier(0.16, 1, 0.3, 1);
}

.confirm-icon-box {
  width: 56px;
  height: 56px;
}

.confirm-icon-box.icon-danger {
  background: #fee2e2;
  color: #dc2626;
}

.confirm-icon-box.icon-warning {
  background: #fef3c7;
  color: #d97706;
}

.confirm-icon-box.icon-info {
  background: #e0f2fe;
  color: #0284c7;
}

.confirm-icon-box.icon-success {
  background: #dcfce7;
  color: #16a34a;
}

.confirm-fade-enter-active,
.confirm-fade-leave-active {
  transition: opacity 0.25s ease;
}

.confirm-fade-enter-from,
.confirm-fade-leave-to {
  opacity: 0;
}

.confirm-fade-enter-active .confirm-card {
  animation: modal-pop 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes modal-pop {
  0% {
    transform: scale(0.92) translateY(10px);
  }
  100% {
    transform: scale(1) translateY(0);
  }
}
</style>

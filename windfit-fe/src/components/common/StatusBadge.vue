<template>
  <span class="status-pill-badge d-inline-flex align-items-center gap-1 px-2.5 py-1 rounded-pill" :class="badgeClass">
    <span class="status-dot rounded-circle" :class="dotClass"></span>
    <span class="status-text fw-medium">{{ statusText }}</span>
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  status: {
    type: [String, Number],
    required: true
  },
  // Optional override for custom text
  customText: {
    type: String,
    default: ''
  }
})

const badgeConfig = computed(() => {
  const s = props.status

  // If status is string
  if (typeof s === 'string') {
    switch (s.toUpperCase()) {
      case 'DANG_DIEN_RA':
        return { text: 'Đang diễn ra', badge: 'bg-emerald-subtle text-emerald border-emerald', dot: 'bg-emerald' }
      case 'SAP_DIEN_RA':
        return { text: 'Sắp diễn ra', badge: 'bg-sky-subtle text-sky border-sky', dot: 'bg-sky' }
      case 'DA_KET_THUC':
        return { text: 'Đã kết thúc', badge: 'bg-slate-subtle text-slate border-slate', dot: 'bg-slate' }
      case 'DA_KHOA':
        return { text: 'Đã khóa', badge: 'bg-rose-subtle text-rose border-rose', dot: 'bg-rose' }
      case 'HET_LUOT':
        return { text: 'Hết lượt', badge: 'bg-amber-subtle text-amber border-amber', dot: 'bg-amber' }
      case 'PHAN_TRAM':
        return { text: 'Phần trăm', badge: 'bg-indigo-subtle text-indigo border-indigo', dot: 'bg-indigo' }
      case 'TIEN_MAT':
        return { text: 'Tiền mặt', badge: 'bg-teal-subtle text-teal border-teal', dot: 'bg-teal' }
      case 'HOAT_DONG':
        return { text: 'Hoạt động', badge: 'bg-emerald-subtle text-emerald border-emerald', dot: 'bg-emerald' }
      case 'NGUNG_HOAT_DONG':
        return { text: 'Ngưng hoạt động', badge: 'bg-rose-subtle text-rose border-rose', dot: 'bg-rose' }
    }
  }

  // If status is number (1 / 0)
  if (s === 1 || s === '1') {
    return { text: 'Hoạt động', badge: 'bg-emerald-subtle text-emerald border-emerald', dot: 'bg-emerald' }
  } else if (s === 0 || s === '0') {
    return { text: 'Tạm khóa', badge: 'bg-rose-subtle text-rose border-rose', dot: 'bg-rose' }
  }

  return { text: String(s), badge: 'bg-secondary-subtle text-secondary border', dot: 'bg-secondary' }
})

const statusText = computed(() => props.customText || badgeConfig.value.text)
const badgeClass = computed(() => badgeConfig.value.badge)
const dotClass = computed(() => badgeConfig.value.dot)
</script>

<style scoped>
.status-pill-badge {
  font-size: 0.76rem;
  line-height: 1.2;
  letter-spacing: 0.2px;
  border-width: 1px;
  border-style: solid;
}

.status-dot {
  width: 6px;
  height: 6px;
  display: inline-block;
}

/* Custom Emerald colors */
.bg-emerald-subtle { background-color: #ecfdf5 !important; }
.text-emerald { color: #047857 !important; }
.border-emerald { border-color: #a7f3d0 !important; }
.bg-emerald { background-color: #10b981 !important; }

/* Custom Sky colors */
.bg-sky-subtle { background-color: #f0f9ff !important; }
.text-sky { color: #0369a1 !important; }
.border-sky { border-color: #bae6fd !important; }
.bg-sky { background-color: #0ea5e9 !important; }

/* Custom Rose colors */
.bg-rose-subtle { background-color: #fff1f2 !important; }
.text-rose { color: #be123c !important; }
.border-rose { border-color: #fecdd3 !important; }
.bg-rose { background-color: #f43f5e !important; }

/* Custom Amber colors */
.bg-amber-subtle { background-color: #fffbeb !important; }
.text-amber { color: #b45309 !important; }
.border-amber { border-color: #fde68a !important; }
.bg-amber { background-color: #f59e0b !important; }

/* Custom Slate colors */
.bg-slate-subtle { background-color: #f8fafc !important; }
.text-slate { color: #64748b !important; }
.border-slate { border-color: #e2e8f0 !important; }
.bg-slate { background-color: #94a3b8 !important; }

/* Custom Indigo colors */
.bg-indigo-subtle { background-color: #eef2ff !important; }
.text-indigo { color: #4338ca !important; }
.border-indigo { border-color: #c7d2fe !important; }
.bg-indigo { background-color: #6366f1 !important; }

/* Custom Teal colors */
.bg-teal-subtle { background-color: #f0fdfa !important; }
.text-teal { color: #0f766e !important; }
.border-teal { border-color: #99f6e4 !important; }
.bg-teal { background-color: #14b8a6 !important; }
</style>

<script setup lang="ts">
import { useNotifications } from '@/composables/useNotifications';

const { confirmState, resolveConfirm } = useNotifications();
</script>

<template>
  <teleport to="body">
    <transition name="confirm-slide">
      <div v-if="confirmState.visible" class="confirm-wrap" role="alertdialog" aria-modal="true">
        <div class="confirm-box">
          <p class="confirm-message">{{ confirmState.message }}</p>
          <div class="confirm-actions">
            <button type="button" class="btn btn-cancel" @click="resolveConfirm(false)">Cancel</button>
            <button type="button" class="btn btn-proceed" @click="resolveConfirm(true)">Proceed</button>
          </div>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<style scoped>
.confirm-wrap {
  position: fixed;
  top: 14px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10000;
  width: min(92vw, 680px);
  display: flex;
  justify-content: center;
  pointer-events: none;
}

.confirm-box {
  pointer-events: auto;
  width: fit-content;
  max-width: 100%;
  border-radius: 12px;
  padding: 12px 14px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  background: rgba(255, 255, 255, 0.97);
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.16);
}

.confirm-message {
  margin: 0 0 10px;
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

.confirm-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.btn {
  border: none;
  border-radius: 8px;
  padding: 7px 14px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}

.btn-cancel {
  background: #e5e7eb;
  color: #111827;
}

.btn-proceed {
  background: #2563eb;
  color: #ffffff;
}

:global([data-theme='dark']) .confirm-box {
  border: 1px solid rgba(255, 255, 255, 0.16);
  background: rgba(17, 24, 39, 0.95);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.45);
}

:global([data-theme='dark']) .confirm-message {
  color: #f9fafb;
}

:global([data-theme='dark']) .btn-cancel {
  background: #374151;
  color: #f9fafb;
}

:global([data-theme='dark']) .btn-proceed {
  background: #3b82f6;
  color: #ffffff;
}

.confirm-slide-enter-active,
.confirm-slide-leave-active {
  transition: all 0.2s ease;
}

.confirm-slide-enter-from,
.confirm-slide-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>

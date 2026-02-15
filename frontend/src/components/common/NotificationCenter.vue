<script setup lang="ts">
// This component is currently not in use but kept for future use
</script>

<template>
  <div class="notification-wrap" aria-live="polite" aria-atomic="true">
    <transition-group name="notify" tag="div" class="notification-list">
    </transition-group>
  </div>

  <teleport to="body">
    <transition name="popup-fade">
    </transition>
  </teleport>
</template>

<style scoped>
.notification-wrap {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9998;
  pointer-events: none;
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.notification-item {
  max-width: 360px;
  color: #fff;
  padding: 12px 14px;
  border-radius: 10px;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.18);
  font-size: 14px;
  pointer-events: auto;
}

.notification-info {
  background: #2563eb;
}

.notification-success {
  background: #16a34a;
}

.notification-error {
  background: #dc2626;
}

.notify-enter-active,
.notify-leave-active {
  transition: all 0.25s ease;
}

.notify-enter-from,
.notify-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

.popup-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: grid;
  place-items: center;
  z-index: 9999;
  padding: 16px;
}

.popup-card {
  width: 100%;
  max-width: 460px;
  background: #fff;
  border-radius: 14px;
  padding: 20px;
  box-shadow: 0 14px 35px rgba(0, 0, 0, 0.25);
}

.popup-message {
  margin: 0 0 16px;
  color: #111827;
  font-size: 15px;
}

.popup-btn {
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  background: #2563eb;
  color: #fff;
  cursor: pointer;
}

.popup-btn:hover {
  background: #1d4ed8;
}

.popup-fade-enter-active,
.popup-fade-leave-active {
  transition: opacity 0.2s ease;
}

.popup-fade-enter-from,
.popup-fade-leave-to {
  opacity: 0;
}
</style>

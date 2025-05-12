<template>
  <div class="teacher-layout">
    <div class="teacher-header">
      <h1>Teacher Portal</h1>
      <div class="tab-container">
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'classes' }"
          @click="activeTab = 'classes'"
        >
          Class Management
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'students' }"
          @click="activeTab = 'students'"
        >
          Student Management
        </button>
      </div>
    </div>
    
    <div class="teacher-content">
      <component :is="currentComponent" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import ClassManagement from './ClassManagement.vue'
import StudentManagement from './StudentManagement.vue'

const activeTab = ref('classes')

const currentComponent = computed(() => {
  return activeTab.value === 'classes' ? ClassManagement : StudentManagement
})
</script>

<style scoped>
.teacher-layout {
  padding: 20px;
  max-width: 1200px;
  margin: 80px auto 0;
}

.teacher-header {
  margin-bottom: 30px;
}

.teacher-header h1 {
  color: #2c3e50;
  margin-bottom: 20px;
}

.tab-container {
  display: flex;
  gap: 10px;
  border-bottom: 2px solid #e5e7eb;
  padding-bottom: 10px;
}

.tab-btn {
  padding: 10px 20px;
  border: none;
  background: none;
  font-size: 16px;
  font-weight: bold;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8px 8px 0 0;
}

.tab-btn:hover {
  color: #4CAF50;
  background: #f3f4f6;
}

.tab-btn.active {
  color: #4CAF50;
  border-bottom: 3px solid #4CAF50;
  background: #f3f4f6;
}

.teacher-content {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style> 
<template>
  <div class="courses">
    <h1>课程列表</h1>
    <div class="course-list">
      <div class="course-item" v-for="course in courses" :key="course.id">
        <h3>{{ course.title }}</h3>
        <p>{{ course.description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const courses = ref([
  {
    id: 1,
    title: 'Java 编程基础',
    description: '学习 Java 编程的基础知识'
  },
  {
    id: 2,
    title: 'Spring Boot 入门',
    description: '快速掌握 Spring Boot 开发'
  }
])
</script>

<style scoped>
.courses {
  padding: 20px;
}

.course-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.course-item {
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style> 
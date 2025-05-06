<template>
  <div class="resources">
    <h1>学习资源</h1>
    <div class="resource-list">
      <div class="resource-item" v-for="resource in resources" :key="resource.id">
        <h3>{{ resource.title }}</h3>
        <p>{{ resource.description }}</p>
        <a :href="resource.link" target="_blank">查看资源</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const resources = ref([
  {
    id: 1,
    title: 'Java 官方文档',
    description: 'Oracle 官方 Java 文档',
    link: 'https://docs.oracle.com/en/java/'
  },
  {
    id: 2,
    title: 'Spring 官方文档',
    description: 'Spring Framework 官方文档',
    link: 'https://spring.io/projects/spring-framework'
  }
])
</script>

<style scoped>
.resources {
  padding: 20px;
}

.resource-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.resource-item {
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.resource-item a {
  display: inline-block;
  margin-top: 10px;
  color: #4CAF50;
  text-decoration: none;
}

.resource-item a:hover {
  text-decoration: underline;
}
</style> 
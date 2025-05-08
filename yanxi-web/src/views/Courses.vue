<template>
  <div class="courses-page">
    <h1 class="courses-title">Our Courses</h1>
    <div class="courses-subtitle">
      Explore our comprehensive range of Chinese language and mathematics courses<br />
      designed for all ages and skill levels
    </div>
    <div class="courses-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        :class="['tab-btn', { active: currentTab === tab.key }]"
        @click="currentTab = tab.key"
      >
        {{ tab.label }}
      </button>
    </div>
    <div class="course-list">
      <div
        class="course-item"
        v-for="course in filteredCourses"
        :key="course.id"
      >
        <div class="course-header">
          <h2 class="course-name">{{ course.title }}</h2>
          <span v-if="course.tag" :class="['course-tag', course.tagClass]">{{ course.tag }}</span>
        </div>
        <div class="course-desc">{{ course.description }}</div>
        <div class="course-meta">
          <div class="meta-row">
            <span class="meta-icon">📖</span>
            <span>Level: {{ course.level }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-icon">🕒</span>
            <span>Duration: {{ course.duration }}</span>
          </div>
        </div>
        <a class="course-link" href="#">View Course Details →</a>
      </div>
    </div>
    <div class="consult-section">
      <div class="consult-title">Not sure which course is right for you?</div>
      <div class="consult-desc">
        Our educational consultants can help you find the perfect course based on your current<br />
        level, goals, and learning style.
      </div>
      <button class="consult-btn">Schedule a Consultation</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const tabs = [
  { key: 'all', label: 'All Courses' },
  { key: 'chinese', label: 'Chinese Language' },
  { key: 'math', label: 'Mathematics' },
  { key: 'combined', label: 'Combined Programs' },
]
const currentTab = ref('all')

const allCourses = [
  {
    id: 1,
    title: 'Chinese Language Fundamentals',
    description: 'Master Chinese characters, pronunciation, and basic conversation skills while learning numerical vocabulary and simple math concepts.',
    level: 'Beginner',
    duration: '12 weeks',
    tag: '语文',
    tagClass: 'tag-chinese',
    tabs: ['all', 'chinese']
  },
  {
    id: 2,
    title: 'Mathematical Chinese',
    description: 'Learn mathematical terminology in Chinese while solving problems. Perfect for students who want to strengthen both subjects simultaneously.',
    level: 'Intermediate',
    duration: '10 weeks',
    tag: '联合',
    tagClass: 'tag-combined',
    tabs: ['all', 'combined']
  },
  {
    id: 3,
    title: 'Advanced Chinese & Algebra',
    description: 'Develop advanced Chinese language skills while tackling algebraic concepts and problem-solving in both languages.',
    level: 'Advanced',
    duration: '14 weeks',
    tag: '联合',
    tagClass: 'tag-combined',
    tabs: ['all', 'combined']
  },
  {
    id: 4,
    title: 'Math Competition Prep (Chinese)',
    description: 'Prepare for mathematics competitions with problem-solving strategies taught in both Chinese and English.',
    level: 'Advanced',
    duration: '8 weeks',
    tag: '数学',
    tagClass: 'tag-math',
    tabs: ['all', 'math']
  },
  {
    id: 5,
    title: 'Elementary Chinese',
    description: 'A fun, interactive introduction to Chinese language for young learners, incorporating numbers and basic counting.',
    level: 'Beginner',
    duration: '8 weeks',
    tag: '语文',
    tagClass: 'tag-chinese',
    tabs: ['chinese']
  },
  {
    id: 6,
    title: 'Intermediate Conversation',
    description: 'Build fluency through practical conversations, including discussions about quantities, measurements, and everyday math.',
    level: 'Intermediate',
    duration: '10 weeks',
    tag: '语文',
    tagClass: 'tag-chinese',
    tabs: ['chinese']
  },
  {
    id: 7,
    title: 'Elementary Mathematics',
    description: 'Build a strong foundation in mathematics with instruction in both Chinese and English.',
    level: 'Beginner',
    duration: '10 weeks',
    tag: '数学',
    tagClass: 'tag-math',
    tabs: ['math']
  },
  {
    id: 8,
    title: 'Pre-Algebra',
    description: 'Prepare for algebra with a bilingual approach to mathematical concepts and problem-solving.',
    level: 'Intermediate',
    duration: '12 weeks',
    tag: '数学',
    tagClass: 'tag-math',
    tabs: ['math']
  },
]

const filteredCourses = computed(() => {
  if (currentTab.value === 'all') {
    // All Courses: 只显示图片1的4个课程
    return allCourses.filter(c => [1,2,3,4].includes(c.id))
  }
  if (currentTab.value === 'chinese') {
    return allCourses.filter(c => c.tabs.includes('chinese'))
  }
  if (currentTab.value === 'math') {
    return allCourses.filter(c => c.tabs.includes('math'))
  }
  if (currentTab.value === 'combined') {
    return allCourses.filter(c => c.tabs.includes('combined'))
  }
  return []
})
</script>

<style scoped>
.courses-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 48px 0 0 0;
  min-height: 100vh;
  font-size: 0.97rem;
  margin-top: 140px;
}
.courses-title {
  text-align: center;
  font-size: 2.2rem;
  font-weight: bold;
  margin-bottom: 10px;
}
.courses-subtitle {
  text-align: center;
  color: #555;
  font-size: 1.15rem;
  margin-bottom: 32px;
}
.courses-tabs {
  display: flex;
  justify-content: center;
  gap: 4px;
  margin-bottom: 36px;
}
.tab-btn {
  background: #f6f6f6;
  border: none;
  outline: none;
  font-size: 1rem;
  font-weight: 500;
  color: #444;
  padding: 8px 22px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}
.tab-btn.active {
  background: #fff;
  color: #111;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.course-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28px;
  margin: 0 auto 48px auto;
  max-width: 1200px;
}
.course-item {
  background: #fff;
  border: 1.5px solid #eee;
  border-radius: 14px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  padding: 20px 14px 16px 14px;
  display: flex;
  flex-direction: column;
  min-height: 160px;
  font-size: 1em;
  text-align: left;
}
.course-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  flex-wrap: nowrap;
  overflow: hidden;
}
.course-name {
  font-size: 1.08rem;
  font-weight: bold;
  margin: 0;
  white-space: nowrap;
}
.course-tag {
  font-size: 0.78rem;
  font-weight: 500;
  border-radius: 16px;
  padding: 2px 12px;
  margin-left: 10px;
  display: inline-block;
  margin-top: 0;
  white-space: nowrap;
}
.tag-chinese {
  background: #fbeaea;
  color: #e53935;
}
.tag-math {
  background: #eaf2fb;
  color: #2563eb;
}
.tag-combined {
  background: #f3eafd;
  color: #a259e4;
}
.course-desc {
  color: #444;
  font-size: 1.05rem;
  margin-bottom: 18px;
  min-height: 48px;
}
.course-meta {
  margin-bottom: 12px;
}
.meta-row {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #444;
  font-size: 1rem;
  margin-bottom: 2px;
}
.meta-icon {
  font-size: 1.1rem;
}
.course-link {
  color: #2563eb;
  font-size: 1rem;
  font-weight: 500;
  text-decoration: none;
  margin-top: 8px;
  transition: color 0.2s;
}
.course-link:hover {
  color: #1746a2;
  text-decoration: underline;
}
.consult-section {
  text-align: center;
  margin-top: 40px;
  margin-bottom: 60px;
}
.consult-title {
  font-size: 1.25rem;
  font-weight: bold;
  margin-bottom: 10px;
}
.consult-desc {
  color: #444;
  font-size: 1.05rem;
  margin-bottom: 22px;
}
.consult-btn {
  background: #e53935;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 14px 38px;
  font-size: 1.08rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s;
}
.consult-btn:hover {
  background: #b71c1c;
}
@media (max-width: 900px) {
  .course-list {
    grid-template-columns: 1fr;
    gap: 18px;
  }
  .courses-page {
    padding: 24px 0 0 0;
  }
}
</style> 
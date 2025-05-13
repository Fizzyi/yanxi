<template>
    <div class="student-home">
      <div class="welcome-section">
        <h1>Welcome back, {{ userInfo.realName }}</h1>
      </div>
  
      <div class="class-section">
        <div class="section-header">
          <h2>My Classes</h2>
          <div class="join-class">
            <input 
              type="text" 
              v-model="classCode" 
              placeholder="Enter class code"
              class="code-input"
            />
            <button class="join-btn" @click="joinClass">
              <i class="fas fa-plus"></i> Join Class
            </button>
          </div>
        </div>
  
        <div class="class-grid">
          <div v-if="loading" class="loading">Loading...</div>
          <div v-else-if="classes.length === 0" class="empty-state">
            <i class="fas fa-book-open empty-icon"></i>
            <p>You haven't joined any classes yet</p>
            <p class="empty-tip">Enter a class code to join a new class</p>
          </div>
          <div v-else v-for="classItem in classes" :key="classItem.id" class="class-card">
            <div class="class-info">
              <h3>{{ classItem.name }}</h3>
              <p class="code">Class Code: {{ classItem.code }}</p>
              <p class="teacher">Teacher: {{ classItem.teacherName }}</p>
              <p class="students">Students: {{ classItem.studentCount }}</p>
              <p class="created">Created: {{ classItem.createdAt }}</p>
            </div>
            <div class="class-actions">
              <button class="action-btn view" @click="viewClassDetails(classItem)">
                <i class="fas fa-eye"></i> View Details
              </button>
            </div>
          </div>
        </div>
      </div>
  
      <!-- Class Details Modal -->
      <div v-if="showDetailsModal" class="modal">
        <div class="modal-content">
          <div class="modal-header">
            <h3>Class Details</h3>
            <button class="close-btn" @click="closeDetailsModal">×</button>
          </div>
          <div v-if="selectedClass" class="class-details">
            <div class="detail-group">
              <label>Class Name</label>
              <p>{{ selectedClass.name }}</p>
            </div>
            <div class="detail-group">
              <label>Class Code</label>
              <p>{{ selectedClass.code }}</p>
            </div>
            <div class="detail-group">
              <label>Teacher</label>
              <p>{{ selectedClass.teacherName }}</p>
            </div>
            <div class="detail-group">
              <label>Students</label>
              <p>{{ selectedClass.studentCount }}</p>
            </div>
            <div class="detail-group">
              <label>Created</label>
              <p>{{ selectedClass.createdAt }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import axios from 'axios'
  import { ElMessageBox } from 'element-plus'
  import 'element-plus/dist/index.css'
  
  const userInfo = ref({
    realName: localStorage.getItem('realName') || 'Student'
  })
  const classes = ref([])
  const loading = ref(true)
  const classCode = ref('')
  const showDetailsModal = ref(false)
  const selectedClass = ref(null)
  
  const fetchClasses = async () => {
    try {
      loading.value = true
      const response = await axios.get('http://localhost:8080/api/classes/student', {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      })
      classes.value = response.data
    } catch (error) {
      console.error('Failed to fetch classes:', error)
      await ElMessageBox.alert('Failed to fetch classes', 'Error', {
        confirmButtonText: 'OK',
        type: 'error'
      })
    } finally {
      loading.value = false
    }
  }
  
  const joinClass = async () => {
    if (!classCode.value) {
      await ElMessageBox.alert('Please enter a class code', 'Warning', {
        confirmButtonText: 'OK',
        type: 'warning'
      })
      return
    }
  
    try {
      const response = await axios.post('http://localhost:8080/api/classes/join', 
        { code: classCode.value },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        }
      )
      console.log("返回的数据为", response.data)
      if (!response.data) {
        await ElMessageBox.alert('Invalid class code', 'Error', {
          confirmButtonText: 'OK',
          type: 'error'
        })
        return
      }

      await ElMessageBox.alert('Successfully joined the class', 'Success', {
        confirmButtonText: 'OK',
        type: 'success'
      })
      classCode.value = ''
      await fetchClasses()
    } catch (error) {
      console.error('Failed to join class:', error)
      await ElMessageBox.alert('Invalid class code', 'Error', {
        confirmButtonText: 'OK',
        type: 'error'
      })
    }
  }
  
  const viewClassDetails = (classItem) => {
    selectedClass.value = classItem
    showDetailsModal.value = true
  }
  
  const closeDetailsModal = () => {
    showDetailsModal.value = false
    selectedClass.value = null
  }
  
  onMounted(fetchClasses)
  </script>
  
  <style scoped>
  .student-home {
    padding: 20px;
    max-width: 1200px;
    margin: 0 auto;
    margin-top: 100px;
  }
  
  .welcome-section {
    text-align: center;
    margin-bottom: 40px;
    padding: 30px;
    background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
    border-radius: 12px;
    color: white;
  }
  
  .welcome-section h1 {
    margin: 0;
    font-size: 2rem;
  }
  
  .welcome-section .subtitle {
    margin: 10px 0 0;
    opacity: 0.9;
  }
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
  }
  
  .join-class {
    display: flex;
    gap: 10px;
  }
  
  .code-input {
    padding: 10px 15px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 1rem;
    width: 200px;
  }
  
  .join-btn {
    padding: 10px 20px;
    background: #4CAF50;
    color: white;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 5px;
    font-weight: bold;
    transition: all 0.3s;
  }
  
  .join-btn:hover {
    background: #45a049;
  }
  
  .class-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
  }
  
  .class-card {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    gap: 15px;
    transition: transform 0.3s;
  }
  
  .class-card:hover {
    transform: translateY(-5px);
  }
  
  .class-info h3 {
    margin: 0 0 5px 0;
    color: #2c3e50;
  }
  
  .class-info p {
    margin: 5px 0;
    color: #666;
  }
  
  .class-actions {
    display: flex;
    gap: 10px;
  }
  
  .action-btn {
    flex: 1;
    padding: 8px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 5px;
    font-weight: bold;
    transition: all 0.3s;
  }
  
  .action-btn.view {
    background: #E3F2FD;
    color: #1976D2;
  }
  
  .action-btn.view:hover {
    background: #BBDEFB;
  }
  
  .modal {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .modal-content {
    background: white;
    padding: 30px;
    border-radius: 12px;
    width: 100%;
    max-width: 600px;
  }
  
  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .close-btn {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #666;
  }
  
  .class-details {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .detail-group {
    margin-bottom: 15px;
  }
  
  .detail-group label {
    display: block;
    font-weight: bold;
    color: #2c3e50;
    margin-bottom: 5px;
  }
  
  .detail-group p {
    margin: 0;
    color: #666;
  }
  
  .loading {
    text-align: center;
    padding: 20px;
    color: #666;
  }
  
  .empty-state {
    text-align: center;
    padding: 40px;
    color: #666;
    background: #f8f9fa;
    border-radius: 12px;
    grid-column: 1 / -1;
  }
  
  .empty-icon {
    font-size: 48px;
    color: #ddd;
    margin-bottom: 20px;
  }
  
  .empty-tip {
    color: #999;
    font-size: 0.9rem;
    margin-top: 10px;
  }
  </style>
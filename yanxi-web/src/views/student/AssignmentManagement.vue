<template>
  <div class="assignment-management">
    <div class="welcome-section">
      <h1>我的作业</h1>
    </div>

    <div class="assignment-section">
      <div class="section-header">
        <div class="header-tabs">
          <h2 class="tab" @click="goToHome">My Classes</h2>
          <h2 class="tab active">My Work</h2>
        </div>
        <div class="filters">
          <el-select v-model="filterSubmitted" placeholder="筛选提交状态" @change="fetchAssignments" style="width: 180px;">
            <el-option label="全部" :value="null" />
            <el-option label="已提交" :value="true" />
            <el-option label="未提交" :value="false" />
          </el-select>
        </div>
      </div>

      <div class="assignment-list">
        <div v-if="loading" class="loading">加载作业中...</div>
        <div v-else-if="assignmentList.length === 0" class="empty-state">
          <i class="fas fa-book empty-icon"></i>
          <p>暂无作业数据</p>
        </div>
        <div v-else class="assignment-table">
          <table>
            <thead>
              <tr>
                <th>所属班级</th>
                <th>作业标题</th>
                <th>作业描述</th>
                <th>发布日期</th>
                <th>提交状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="assignment in assignmentList" :key="assignment.id">
                <td>{{ assignment.classId }}</td>
                <td>{{ assignment.title }}</td>
                <td>{{ assignment.description }}</td>
                <td>{{ formatDate(assignment.createdAt) }}</td>
                <td>
                  <el-tag :type="assignment.submitted ? 'success' : 'warning'">
                    {{ assignment.submitted ? '已提交' : '未提交' }}
                  </el-tag>
                </td>
                <td>
                    <button class="action-btn download" 
                    @click="handleUpload(assignment)">
                    <i class="fas fa-upload"></i> 下载作业
                    </button>

                  <button 
                    v-if="!assignment.submitted" 
                    class="action-btn submit" 
                    @click="handleUpload(assignment)"
                  >
                    <i class="fas fa-upload"></i> 提交作业
                  </button>
                  <button 
                    v-else 
                    class="action-btn view" 
                    @click="handleViewSubmission(assignment)"
                  >
                    <i class="fas fa-eye"></i> 查看提交
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const loading = ref(false)
const assignmentList = ref([])
const filterSubmitted = ref(null)

const fetchAssignments = async () => {
  try {
    loading.value = true
    const params = {}
    if (filterSubmitted.value !== null) {
      params.submitted = filterSubmitted.value
    }
    const response = await axios.get('http://localhost:8080/api/assignments/student', {
      params,
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    assignmentList.value = response.data || []
  } catch (error) {
    console.error('获取作业列表失败:', error)
    ElMessage.error('获取作业列表失败')
    assignmentList.value = []
  } finally {
    loading.value = false
  }
}

const formatDate = (date) => {
  if (!date) return ''
  try {
    return new Date(date).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (error) {
    console.error('日期格式化错误:', error)
    return date
  }
}

const handleUpload = (assignment) => {
    
    var down_url = 'http://localhost:8080/api/assignments/download?fileUrl=' + assignment.fileUrl
    axios({
          url:down_url,
          method: 'GET',
          responseType: 'blob'
        }).then((response) => {
          const href = URL.createObjectURL(response.data);
          const link = document.createElement('a');
          link.href = href;
          link.setAttribute('download',  assignment.fileUrl);
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          URL.revokeObjectURL(href);
        });
}

const handleViewSubmission = (assignment) => {
  ElMessage.info('查看提交功能开发中...')
}

const goToHome = () => {
  router.push('/student/home')
}

onMounted(fetchAssignments)
</script>

<style scoped>
.assignment-management {
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

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-tabs {
  display: flex;
  gap: 20px;
  align-items: center;
}

.tab {
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s;
  color: #666;
}

.tab:hover {
  background: #f5f5f5;
}

.tab.active {
  color: #4CAF50;
  font-weight: bold;
}

.assignment-list {
  margin-bottom: 30px;
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

.assignment-table {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 15px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #2c3e50;
}

tr:hover {
  background-color: #f8f9fa;
}

.action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-weight: bold;
  transition: all 0.3s;
}

.action-btn.submit {
  background: #E8F5E9;
  color: #388E3C;
}

.action-btn.submit:hover {
  background: #C8E6C9;
}

.action-btn.view {
  background: #E3F2FD;
  color: #1976D2;
}

.action-btn.view:hover {
  background: #BBDEFB;
}
</style>
<template>
    <div class="assignment-management">
      <div class="header-actions">
        <h2></h2>
        <div class="filters">
          <select v-model="filterForm.classId" @change="handleFilter" class="class-select">
            <option value="">所有班级</option>
            <option v-for="item in classList" :key="item.id" :value="item.id">
              {{ item.name }}
            </option>
          </select>
          <div class="search-box">
            <input 
              type="text" 
              v-model="filterForm.studentEmail" 
              placeholder="按学生邮箱搜索..."
              @input="handleFilter"
            />
          </div>
        </div>
      </div>
  
      <div class="assignment-list">
        <div v-if="loading" class="loading">加载作业中...</div>
        <div v-else-if="assignmentList.length === 0" class="empty-state">
          暂无作业数据
        </div>
        <div v-else class="assignment-table">
          <table>
            <thead>
              <tr>
                <th>作业标题</th>
                <th>作业描述</th>
                <th>班级ID</th>
                <th>创建时间</th>
                <!-- <th>附件</th> -->
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="assignment in assignmentList" :key="assignment.id">
                <td>{{ assignment.title }}</td>
                <td>{{ assignment.description }}</td>
                <td>{{ assignment.classId }}</td>
                <td>{{ formatDate(assignment.createdAt) }}</td>
                
                <td class="actions">
                  <button class="action-btn edit" @click="handleEdit(assignment)">
                    <i class="fas fa-edit"></i> 编辑
                  </button>
                  <button class="action-btn view" @click="handleViewStudents(assignment)">
                    <i class="fas fa-download"></i> 上传详情
                  </button>
                  <button class="action-btn delete" @click="handleDelete(assignment)">
                    <i class="fas fa-trash"></i> 删除
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
  
      <!-- 编辑作业对话框 -->
      <el-dialog 
        v-model="dialogVisible" 
        :title="editForm.id ? '编辑作业' : '发布新作业'" 
        width="50%"
      >
        <el-form :model="editForm" label-width="100px" :rules="rules" ref="editFormRef">
          <el-form-item label="作业标题" prop="title">
            <el-input v-model="editForm.title" />
          </el-form-item>
          <el-form-item label="作业描述" prop="description">
            <el-input type="textarea" v-model="editForm.description" :rows="4" />
          </el-form-item>
          <el-form-item label="班级" prop="classId">
            <el-select v-model="editForm.classId" placeholder="选择班级">
              <el-option
                v-for="item in classList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="截止日期" prop="dueDate">
            <el-date-picker
              v-model="editForm.dueDate"
              type="datetime"
              placeholder="选择截止日期"
              :disabled-date="disabledDate"
            />
          </el-form-item>
          <el-form-item label="附件">
            <el-upload
              class="upload-demo"
              :action="uploadUrl"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
              :headers="uploadHeaders"
            >
        
            </el-upload>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSave">保存</el-button>
          </span>
        </template>
      </el-dialog>
  
      <!-- 学生列表对话框 -->
      <el-dialog 
        v-model="studentDialogVisible" 
        title="作业提交详情" 
        width="60%"
        @opened="handleDialogOpened"
        @closed="handleDialogClosed"
      >
        <template v-if="currentAssignment">
          <div v-if="loadingStudents" class="loading">加载学生列表中...</div>
          <div v-else-if="studentList.length === 0" class="empty-state">
            暂无学生数据
          </div>
          <div v-else class="student-table">
            <table>
              <thead>
                <tr>
                  <th>学生姓名</th>
                  <th>邮箱</th>
                  <th>提交状态</th>
                  <th>提交时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="student in studentList" :key="student.id">
                  <td>{{ student.username }}</td>
                  <td>{{ student.email }}</td>
                  <td>
                    <el-tag :type="student.submitted ? 'success' : 'warning'">
                      {{ student.submitted ? '已提交' : '未提交' }}
                    </el-tag>
                  </td>
                  <td>{{ student.submitted ? formatDate(student.submittedAt) : '-' }}</td>
                  <td>
                    <el-button 
                      type="primary" 
                      size="small"
                      v-if="student.submitted"
                      @click="handleDownloadSubmission(student.submissionId)"
                    >
                      下载作业
                    </el-button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, watch } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import axios from 'axios'
  
  const loading = ref(false)
  const dialogVisible = ref(false)
  const assignmentList = ref([])
  const classList = ref([])
  const studentList = ref([])
  const editFormRef = ref(null)
  const uploadUrl = import.meta.env.VITE_API_URL + '/api/assignments/upload'
  const studentDialogVisible = ref(false)
  const loadingStudents = ref(false)
  const currentAssignment = ref(null)
  
  const filterForm = ref({
    classId: '',
    studentEmail: ''
  })
  
  const editForm = ref({
    id: null,
    title: '',
    description: '',
    classId: '',
    dueDate: null,
    fileUrl: ''
  })
  
  const rules = {
    title: [{ required: true, message: '请输入作业标题', trigger: 'blur' }],
    description: [{ required: true, message: '请输入作业描述', trigger: 'blur' }],
    classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
    dueDate: [{ required: true, message: '请选择截止日期', trigger: 'change' }]
  }
  
  const uploadHeaders = {
    Authorization: `Bearer ${localStorage.getItem('token')}`
  }
  
  // 获取作业列表
  const fetchAssignments = async () => {
    try {
      loading.value = true
      const response = await axios.get('http://localhost:8080/api/assignments', {
        params: filterForm.value,
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
  
  // 获取班级列表
  const fetchClasses = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/classes/teacher', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })
      classList.value = response.data || []
    } catch (error) {
      console.error('获取班级列表失败:', error)
      ElMessage.error('获取班级列表失败')
      classList.value = []
    }
  }
  
  // 获取学生列表
  const fetchStudents = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/classes/teacher/students', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })
      studentList.value = response.data || []
    } catch (error) {
      console.error('获取学生列表失败:', error)
      ElMessage.error('获取学生列表失败')
      studentList.value = []
    }
  }
  
  // 处理筛选
  const handleFilter = () => {
    fetchAssignments()
  }
  
  // 处理添加
  const handleAdd = () => {
    editForm.value = {
      id: null,
      title: '',
      description: '',
      classId: '',
      dueDate: null,
      fileUrl: ''
    }
    dialogVisible.value = true
  }
  
  // 处理编辑
  const handleEdit = (row) => {
    editForm.value = { ...row }
    dialogVisible.value = true
  }
  
  // 处理删除
  const handleDelete = async (row) => {
    try {
      await ElMessageBox.confirm('确定要删除这个作业吗？', '提示', {
        type: 'warning'
      })
      await axios.delete(`/api/assignments/${row.id}`)
      ElMessage.success('删除成功')
      fetchAssignments()
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('删除失败')
      }
    }
  }
  
  // 处理保存
  const handleSave = async () => {
    if (!editFormRef.value) return
    
    try {
      await editFormRef.value.validate()
      const url = editForm.value.id 
        ? `/api/assignments/${editForm.value.id}`
        : '/api/assignments'
      const method = editForm.value.id ? 'put' : 'post'
      
      await axios[method](url, editForm.value)
      ElMessage.success(editForm.value.id ? '更新成功' : '发布成功')
      dialogVisible.value = false
      fetchAssignments()
    } catch (error) {
      console.error('保存作业失败:', error)
      ElMessage.error('保存失败')
    }
  }
  
  // 处理文件上传成功
  const handleUploadSuccess = (response) => {
    editForm.value.fileUrl = response.data
    ElMessage.success('文件上传成功')
  }
  
  // 上传前检查
  const beforeUpload = (file) => {
    const isLt10M = file.size / 1024 / 1024 < 10
    if (!isLt10M) {
      ElMessage.error('文件大小不能超过 10MB!')
      return false
    }
    return true
  }
  
  // 格式化日期
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
  
  // 查看学生列表
  const handleViewStudents = async (assignment) => {
    try {
      loadingStudents.value = true
      currentAssignment.value = assignment
      studentList.value = [] // 清空之前的数据
      
      const response = await axios.get(`http://localhost:8080/api/assignments/${assignment.id}/students`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })
      studentList.value = response.data
      studentDialogVisible.value = true
    } catch (error) {
      console.error('获取学生列表失败:', error)
      ElMessage.error('获取学生列表失败')
    } finally {
      loadingStudents.value = false
    }
  }
  
  // 对话框打开后加载数据
  const handleDialogOpened = () => {
    // 对话框打开时不需要重新加载数据，因为数据已经在点击按钮时加载完成
  }
  
  // 对话框关闭时清空数据
  const handleDialogClosed = () => {
    studentList.value = []
    currentAssignment.value = null
    loadingStudents.value = false
  }
  
  // 下载学生提交的作业
  const handleDownloadSubmission = (submissionId) => {
    // TODO: 实现下载学生提交的作业
    ElMessage.info('下载功能开发中...')
  }
  
  // 禁用过去的日期
  const disabledDate = (time) => {
    return time.getTime() < Date.now() - 8.64e7
  }
  
  onMounted(() => {
    fetchAssignments()
    fetchClasses()
  })
  </script>
  
  <style scoped>
  .assignment-management {
    padding: 20px;
  }
  
  .header-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
  }
  
  .filters {
    display: flex;
    gap: 15px;
    align-items: center;
  }
  
  .class-select {
    padding: 10px 15px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 1rem;
    min-width: 200px;
    background-color: white;
  }
  
  .search-box input {
    padding: 10px 15px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 1rem;
    width: 300px;
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
  
  .actions {
    display: flex;
    gap: 10px;
  }
  
  .action-btn {
    padding: 6px 12px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 5px;
    font-size: 0.9rem;
    transition: all 0.3s;
  }
  
  .action-btn.edit {
    background: #E8F5E9;
    color: #388E3C;
  }
  
  .action-btn.edit:hover {
    background: #C8E6C9;
  }
  
  .action-btn.delete {
    background: #FFEBEE;
    color: #D32F2F;
  }
  
  .action-btn.delete:hover {
    background: #FFCDD2;
  }
  
  .action-btn.view {
    background: #E3F2FD;
    color: #1976D2;
  }
  
  .action-btn.view:hover {
    background: #BBDEFB;
  }
  
  .loading,
  .empty-state {
    text-align: center;
    padding: 40px;
    color: #666;
  }
  
  .upload-demo {
    width: 100%;
  }
  
  .el-upload__tip {
    margin-top: 5px;
    color: #909399;
  }
  
  .download-btn {
    display: flex;
    align-items: center;
    gap: 5px;
    background: #409EFF;
    border: none;
    transition: all 0.3s ease;
  }
  
  .download-btn:hover {
    background: #66b1ff;
    transform: translateY(-1px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .download-btn i {
    font-size: 14px;
  }
  
  .student-table {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    margin-top: 20px;
  }
  
  .student-table table {
    width: 100%;
    border-collapse: collapse;
  }
  
  .student-table th,
  .student-table td {
    padding: 12px;
    text-align: left;
    border-bottom: 1px solid #eee;
  }
  
  .student-table th {
    background-color: #f8f9fa;
    font-weight: 600;
    color: #2c3e50;
  }
  
  .student-table tr:hover {
    background-color: #f8f9fa;
  }
  </style>
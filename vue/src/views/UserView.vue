<template>
  <div class="user-view">
    <!-- Search Bar -->
    <div class="search-bar">
      <el-input
        v-model="params.name"
        :placeholder="$t('user.searchName')"
        class="search-input"
      ></el-input>
      <el-input
        v-model="params.phone"
        :placeholder="$t('user.searchPhone')"
        class="search-input"
      ></el-input>
      <el-input
        v-model="params.address"
        :placeholder="$t('user.searchAddress')"
        class="search-input"
      ></el-input>
      <el-button type="warning" @click="searchUser()" class="search-button" style="margin-left: 5px;">
        {{ $t('user.search') }}
      </el-button>
      <el-button type="danger" @click="reset()" class="reset-button">
        {{ $t('user.reset') }}
      </el-button>
      <el-button type="primary" @click="add()" class="add-button">
        {{ $t('user.add') }}
      </el-button>
    </div>

    <div>
      <el-dialog :title="isEdit ? $t('user.editUser') : $t('user.addUser')" :visible.sync="dialogFormVisible" width="40%">
    <el-form :model="form" :rules="rules" ref="form" label-width="120px">
      
      <el-form-item :label="$t('user.username')" prop="username" required class="required-label">
        <el-input 
          v-model="form.username" 
          :placeholder="$t('user.usernamePlaceholder')"
          class="required-input">
        </el-input>
      </el-form-item>
      
      <el-form-item :label="$t('user.gender')" prop="gender" required class="required-label">
        <el-radio-group v-model="form.gender" :placeholder="$t('user.genderPlaceholder')">
          <el-radio label="male">{{ $t('user.male') }}</el-radio>
          <el-radio label="female">{{ $t('user.female') }}</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item :label="$t('user.age')" prop="age">
        <el-input-number v-model="form.age" :min=16 :max=120 :placeholder="$t('common.optional')"></el-input-number>
      </el-form-item>
      
      <el-form-item :label="$t('user.phone')" prop="phone">
        <el-input v-model="form.phone" :placeholder="$t('common.optional')"></el-input>
      </el-form-item>
      
      <el-form-item :label="$t('user.address')" prop="address">
        <el-input v-model="form.address" :placeholder="$t('common.optional')"></el-input>
      </el-form-item>
      
      <el-form-item :label="$t('user.realName')" prop="realName">
        <el-input v-model="form.realName" :placeholder="$t('common.optional')"></el-input>
      </el-form-item>
      
      <el-form-item :label="$t('user.studentId')" prop="studentId" required class="required-label">
        <el-input 
          v-model="form.studentId" 
          :placeholder="$t('user.studentIdPlaceholder')"
          class="required-input">
        </el-input>
      </el-form-item>
      
      <el-form-item :label="$t('user.email')" prop="email">
        <el-input v-model="form.email" type="email" :placeholder="$t('common.optional')"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogFormVisible = false">{{ $t('common.cancel') }}</el-button>
      <el-button type="primary" @click="submit()">{{ $t('common.confirm') }}</el-button>
    </div>
  </el-dialog>
    </div>

    <!-- User Table -->
    <el-table 
      v-loading="loading"
      :data="tableData"
      border
      style="width: 100%">
      <el-table-column
        prop="username"
        :label="$t('user.username')"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="gender"
        :label="$t('user.gender')"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="age"
        :label="$t('user.age')"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="phone"
        :label="$t('user.phone')"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="address"
        :label="$t('user.address')"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="realName"
        :label="$t('user.realName')"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="studentId"
        :label="$t('user.studentId')"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="email"
        :label="$t('user.email')"
        align="center"
      ></el-table-column>
      <el-table-column
        :label="$t('user.actions')"
        fixed="right"
        align="center"
      >
      <template slot-scope="scope">
        <div class="action-buttons">
          <el-button 
            type="text"
            size="mini" 
            @click="edit(scope.row)"
          >
            {{ $t('user.edit') }}
          </el-button>
          
          <el-popconfirm 
            :title="$t('user.deleteConfirm')" 
            confirm-button-text="Yes"
            cancel-button-text="No"
            icon="el-icon-warning"
            icon-color="orange"
            @confirm="handleDelete(scope.row.id)"
          >
            <el-button 
              slot="reference"
              type="text"
              size="mini"
              :aria-label="$t('user.delete')"
            >
              {{ $t('user.delete') }}
            </el-button>
          </el-popconfirm>
        </div>
      </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <div class="pagination">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
        :current-page="params.pageNum"
        :page-sizes="[5, 10, 15, 20]"
        :page-size="params.pageSize"
        :total="total"
      ></el-pagination>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request';

export default {
  data() {
    return {
      params: {
        username: '',
        phone: '',
        address: '',
        pageNum: 1,
        pageSize: 10
      },
      total: 0,
      tableData: [],
      dialogFormVisible: false,
      form:{
        id: undefined,
        username: '',
        password: undefined,
        gender: '',
        age: undefined,
        phone: undefined,
        address: undefined,
        realName: undefined,
        studentId: '',
        email: undefined,
      },
      rules: {
        username: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' },
          { min: 2, max: 20, message: this.$t('validate.length'), trigger: 'blur' }
        ],
        gender: [
          { required: true, message: this.$t('validate.required'), trigger: 'change' }
        ],
        studentId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' },
          { pattern: /^[0-9]{10}$/, message: this.$t('validate.studentId'), trigger: 'blur' }
        ]
      },
      isEdit: false,
    }
  },
  
  methods: {
    searchUser() {
      const lang = this.$route.params.lang || 'zh-cn'
      request.get(`/${lang}/user/search`, {
        params: this.params
      }).then(res => {
        if (res.code === '0') {
          this.tableData = res.data.list
          this.total = res.data.total
        } else {
          this.$message.error(res.msg || this.$t('message.searchError'))
        }
      }).catch(err => {
        console.error('searchUser error:', err)
        this.$message.error(this.$t('message.searchError'))
      })
    },

    handlePageChange(page) {
      this.params.pageNum = page
      this.searchUser()
    },

    handleSizeChange(size) {
      this.params.pageSize = size
      this.searchUser()
    },

    reset() {
      this.params = {
        name: '',
        phone: '',
        address: '',
        pageNum: 1,
        pageSize: 10
      };
      this.searchUser();
    },

    add() {
      this.isEdit = false;
      this.form = {
        id: undefined,
        username: '',
        password: undefined,
        gender: '',
        age: undefined,
        phone: undefined,
        address: undefined,
        realName: undefined,
        studentId: '',
        email: undefined
      };
      this.dialogFormVisible = true;
    },

    resetForm() {
      this.$refs.form.resetFields();
    },

    submit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          const lang = this.$route.params.lang || 'zh-cn';
          if (!this.form.username || !this.form.gender || !this.form.studentId) {
            this.$message.error(this.$t('validate.requiredFields'));
            return;
          }

          const url = this.isEdit ? `/${lang}/user/update` : `/${lang}/user/add`;
          request.post(url, this.form)
            .then(res => {
              if (res.code === '0') {
                this.$message.success(this.isEdit ? 
                  this.$t('message.updateSuccess') : 
                  this.$t('message.addSuccess'));
                this.dialogFormVisible = false;
                this.resetForm();
                this.searchUser();
              } else {
                this.$message.error(res.msg || this.$t('message.error'));
              }
            })
            .catch(error => {
              console.error(this.isEdit ? this.$t('message.updateError') : this.$t('message.addError'), error);
              this.$message.error(this.$t('message.error'));
            });
        }
      });
    },

    edit(row) {
      this.isEdit = true;
      this.form = {...row};
      this.dialogFormVisible = true;
    },

    handleDelete(id) {
      const lang = this.$route.params.lang || 'zh-cn';
      request.delete(`/${lang}/user/delete/${id}`)
        .then(res => {
          if (res.code === '0') {
            this.$message.success(this.$t('message.deleteSuccess'));
            this.searchUser();
          } else {
            this.$message.error(res.msg || this.$t('message.deleteError'));
          }
        })
        .catch(error => {
          console.error('Delete failed:', error);
          this.$message.error(this.$t('message.error'));
        });
    }

  },

  created() {
    this.searchUser()
  },

}
</script>

<style scoped>
.user-view {
  padding: 0.05%;
}

.search-bar {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.search-input {
  width: 200px;
  margin-right: 5px;
}

.search-button,
.reset-button,
.add-button {
  margin-right: -1px;
}

.pagination {
  margin-top: 15px;
  margin-left: 5px;
  text-align: left;
}

/* .required-field :deep(.el-form-item__label) {
  color: #f56c6c;
} */

.required-label :deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
}

.required-input :deep(.el-input__inner::placeholder) {
  color: #8CCB66;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 10px;
}
</style>
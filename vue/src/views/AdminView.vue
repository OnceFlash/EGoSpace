<template>
  <div class="admin-view">
    <!-- Search Bar -->
    <div class="search-bar">
      <el-input 
        v-model="params.realName" 
        :placeholder="$t('admin.searchRealName')"
        class="search-input"
      ></el-input>
      <el-input
        v-model="params.phone"
        :placeholder="$t('admin.searchPhone')"
        class="search-input"
      ></el-input>
      <el-input
        v-model="params.email"
        :placeholder="$t('admin.searchEmail')"
        class="search-input"
      ></el-input>
      <el-button type="warning" @click="searchAdmin()" class="search-button" style="margin-left: 5px;">
        {{ $t('admin.search') }}
      </el-button>
      <el-button type="danger" @click="reset()" class="reset-button">
        {{ $t('admin.reset') }}
      </el-button>
      <el-button type="primary" @click="add()" class="add-button">
        {{ $t('admin.add') }}
      </el-button>
    </div>

    <!-- Admin Form Dialog -->
    <el-dialog :title="isEdit ? $t('admin.editAdmin') : $t('admin.addAdmin')" 
               :visible.sync="dialogFormVisible" 
               width="40%">
      <el-form :model="form" :rules="rules" ref="form" label-width="120px">
        <el-form-item :label="$t('admin.realName')" prop="realName" required class="required-label">
          <el-input 
            v-model="form.realName"
            :placeholder="$t('admin.realNamePlaceholder')"
            class="required-input"
          ></el-input>
        </el-form-item>
        <el-form-item :label="$t('admin.phone')" prop="phone">
          <el-input 
            v-model="form.phone"
            :placeholder="$t('common.optional')"
          ></el-input>
        </el-form-item>
          <el-form-item :label="$t('admin.address')" prop="address">
          <el-input 
            v-model="form.address"
            :placeholder="$t('common.optional')"
          ></el-input>
        </el-form-item>
        <el-form-item :label="$t('admin.email')" prop="email">
          <el-input 
            v-model="form.email" 
            type="email"
            :placeholder="$t('common.optional')"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="submit">{{ $t('common.confirm') }}</el-button>
      </div>
    </el-dialog>

    <!-- Admin Table -->
    <el-table 
      v-loading="loading"
      :data="tableData"
      border
      style="width: 100%">
      <el-table-column
        prop="realName"
        :label="$t('admin.realName')"
        align="center"
        min-width="120"
      ></el-table-column>
      <el-table-column
        prop="phone"
        :label="$t('admin.phone')"
        align="center"
        min-width="120"
      ></el-table-column>
      <el-table-column
        prop="address"
        :label="$t('admin.address')"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="email"
        :label="$t('admin.email')"
        align="center"
        min-width="150"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        :label="$t('admin.actions')"
        fixed="right"
        align="center"
        width="180"
        class="actions-column"
      >
      <template slot-scope="scope">
        <div class="action-buttons">
          <el-button 
            type="text"
            size="mini" 
            @click="edit(scope.row)"
          >
            {{ $t('admin.edit') }}
          </el-button>
          
          <el-popconfirm 
            :title="$t('admin.deleteConfirm')" 
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
              :aria-label="$t('admin.delete')"
            >
              {{ $t('admin.delete') }}
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
import request from '@/utils/request'

export default {
  name: 'AdminView',
  data() {
    return {
      params: {
        realName: '',
        phone: '',
        email: '',
        pageNum: 1,
        pageSize: 10
      },
      total: 0,
      tableData: [],
      dialogFormVisible: false,
      form: {
        ID: undefined,
        realName: '',
        phone: undefined,
        address: undefined,
        email: undefined,
      },
      rules: {
        realName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' },
          { min: 2, max: 20, message: this.$t('validate.length'), trigger: 'blur' }
        ],
      },
      isEdit: false
    }
  },

  methods: {
    searchAdmin() {
      const lang = this.$route.params.lang || 'zh-cn'
      request.get(`/${lang}/admin/search`, {
        params: this.params
      }).then(res => {
        if (res.code === '0') {
          this.tableData = res.data.list
          this.total = res.data.total
        } else {
          this.$message.error(res.msg || this.$t('message.searchError'))
        }
      }).catch(error => {
        console.error('searchAdmin error:', error)
        this.$message.error(this.$t('message.searchError'))
      })
    },

    handlePageChange(page) {
      this.params.pageNum = page
      this.searchAdmin()
    },

    handleSizeChange(size) {
      this.params.pageSize = size
      this.searchAdmin()
    },

    reset() {
      this.params = {
        realName: '',
        phone: '',
        email: '',
        pageNum: 1,
        pageSize: 10
      }
      this.searchAdmin()
    },

    add() {
      this.isEdit = false
      this.form = {
        ID: undefined,
        realName: '',
        phone: undefined,
        address: undefined,
        email: undefined,
      }
      this.dialogFormVisible = true
    },

    edit(row) {
      this.isEdit = true
      this.form = {...row}
      this.dialogFormVisible = true
    },

    submit() {
    this.$refs.form.validate((valid) => {
      if (!valid) return;

      const lang = this.$route.params.lang || 'zh-cn';
      const url = this.isEdit ? `/${lang}/admin/update` : `/${lang}/admin/add`;

      request.post(url, this.form)
        .then(res => {
          if (res.code === '0') {
            this.$message.success(this.isEdit ? 
              this.$t('message.updateSuccess') : 
              this.$t('message.addSuccess'));
            this.dialogFormVisible = false;
            this.$refs.form.resetFields();
            this.searchAdmin();
          } else {
            throw new Error(res.msg || this.$t('message.error'));
          }
        })
        .catch(error => {
          console.error('Operation failed:', error);
          this.$message.error(error.response?.data?.msg || this.$t('message.error'));
        });
    });
  },

    handleDelete(ID) {
      const lang = this.$route.params.lang || 'zh-cn'
      request.delete(`/${lang}/admin/delete/${ID}`)
        .then(res => {
          if (res.code === '0') {
            this.$message.success(this.$t('message.deleteSuccess'))
            this.searchAdmin()
          } else {
            this.$message.error(res.msg || this.$t('message.deleteError'))
          }
        })
        .catch(error => {
          console.error('Delete failed:', error)
          this.$message.error(this.$t('message.error'))
        })
    }
  },

  created() {
    this.searchAdmin()
  }
}
</script>

<style scoped>
.admin-view {
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
在多人协作的 Git 项目中，当你处于 `dev` 分支时，需按照以下流程操作，以确保代码协作高效且避免冲突：

---

### **1. 同步远程 `dev` 分支的最新代码**
```bash
# 拉取远程 dev 分支的最新代码（确保本地与远程一致）
git pull origin dev
```
- **注意**：如果拉取失败（提示冲突），需要先解决冲突（见步骤3）。

---

### **2. 创建新分支（针对具体任务）**
如果团队要求每个功能/修复在独立分支开发：
```bash
# 基于 dev 分支创建新功能分支（例如：feature/login）
git checkout -b feature/your-feature-name dev
```
- **分支命名规范**：
    - 功能分支：`feature/xxx`
    - 修复分支：`fix/xxx`
    - 热修复分支：`hotfix/xxx`

---

### **3. 开发并提交代码**
```bash
# 在本地完成代码修改后，提交到本地仓库
git add .
git commit -m "feat: 实现用户登录功能"
```
- **提交消息规范**：
    - `feat`: 新功能
    - `fix`: 修复问题
    - `docs`: 文档变更
    - `style`: 代码格式调整
    - `refactor`: 代码重构
    - `test`: 测试相关
    - `chore`: 构建或工具变更

---

### **4. 推送分支到远程仓库**
```bash
# 将本地分支推送到远程（首次推送需关联远程分支）
git push -u origin feature/your-feature-name
```

---

### **5. 发起 Pull Request (PR) / Merge Request (MR)**
1. 访问 Git 平台（如 GitHub/GitLab），进入仓库页面。
2. 选择你的分支（如 `feature/your-feature-name`），点击 **New Pull Request**。
3. 设置目标分支为 `dev`，填写 PR 描述，并通知团队审查。

---

### **6. 处理代码审查反馈**
- 如果审查后需要修改代码：
    1. 在本地分支继续提交修改。
    2. 再次推送更新，PR 会自动同步：
       ```bash
       git push origin feature/your-feature-name
       ```

---

### **7. 合并到 `dev` 分支**
- 当 PR 通过审查后：
    1. 在 Git 平台上点击 **Merge**，将代码合并到 `dev`。
    2. 删除已合并的远程分支（可选）：
       ```bash
       git push origin --delete feature/your-feature-name
       ```

---

### **8. 更新本地 `dev` 分支**
合并完成后，确保本地 `dev` 分支与远程同步：
```bash
git checkout dev
git pull origin dev
```

---

### **关键注意事项**
1. **禁止直接强制推送**（`git push -f`）：  
   这会覆盖他人提交，导致协作混乱。
2. **每日工作前先拉取最新代码**：
   ```bash
   git checkout dev
   git pull origin dev
   ```  
3. **保持分支简洁**：
    - 一个分支只做一件事（如修复一个 Bug 或开发一个功能）。
    - 完成后及时删除已合并的分支。

---

### **常见问题处理**
#### **场景 1：拉取代码时发现冲突**
1. 手动解决冲突文件中的 `<<<<<<< HEAD` 和 `>>>>>>>` 标记。
2. 标记冲突已解决：
   ```bash
   git add .
   git commit -m "fix: 解决合并冲突"
   git push origin dev
   ```

#### **场景 2：误提交到 `dev` 分支**
1. 将误提交的内容移动到新分支：
   ```bash
   git checkout -b temp-fix
   git checkout dev
   git reset --hard HEAD~1  # 回退 dev 分支到上一个提交
   git push -f origin dev   # 强制回退远程（需谨慎，提前告知团队）
   ```

---

遵循以上流程，可以高效参与团队协作，同时保持代码库的稳定性。  
刘文俊好帅  
卢威涵好帅  
李鹏卓好帅  
李鹏卓好帅  
李鹏卓好帅  
梁春杰好帅  
刘恩硕好帅
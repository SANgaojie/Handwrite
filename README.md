
# Handwrite 手写模拟器

## 项目概述
Handwrite 是一款 Android 应用，支持用户输入文字、选择背景图片与字体文件，自定义字体颜色、大小、行间距及显示区域，最终生成带文字的图片并导出。


## 功能特性
- **文字输入**：支持多行文本输入
- **背景图片**：从相册选择自定义背景
- **字体管理**：
  - 内置字体库（`assets/fonts`）
  - 支持外部字体文件（TTF/OTF）
- **样式设置**：
  - 颜色选择器（Argb 模式）
  - 字体大小调节（1-50sp）
  - 行间距调节（0-50dp）
  - 文字区域自定义（宽高范围 100-400dp）
- **图片导出**：生成 PNG 格式图片保存至相册


## 安装指南
###
下载安装apk文件
### 本地构建
1. 克隆项目：
   ```bash
   git clone https://github.com/SANgaojie/Handwrite.git
   ```
2. 导入 Android Studio，等待 Gradle 同步完成
3. 连接设备或启动模拟器
4. 点击运行按钮（▶️）

### 依赖库
```gradle
dependencies {
    implementation 'com.skydoves:colorpickerview:3.2.1'
    implementation 'androidx.appcompat:appcompat:1.6.1'
}
```


## 使用说明
### 基础操作
1. **输入文字**：在文本框输入内容
2. **选择背景**：点击「选择背景图片」从相册选取
3. **设置字体**：
   - 内置字体：通过下拉列表选择
   - 自定义字体：点击「选择字体文件」加载
4. **样式调节**：
   - 颜色：通过颜色选择器调整
   - 大小/行间距：滑动条调节
   - 区域：通过宽高滑动条设置
5. **导出图片**：点击「导出图片」保存到本地


## 项目结构
```
app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/handwrite/
│   │   │       └── MainActivity.java  # 核心逻辑
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml  # 主界面布局
│   │   │   └── values/
│   │   │       └── strings.xml        # 字符串资源
│   │   └── assets/
│   │       └── fonts/                 # 内置字体
├── build.gradle.kts                  # 模块构建配置
└── ...
```


## 运行图片示例
![运行界面](image/1.png)
![运行界面](image/2.png)
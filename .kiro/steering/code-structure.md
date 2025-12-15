# Seminar Management System - 代码结构规范

## 项目目录结构
```
src/
├── main/
│   └── java/
│       └── com/fci/seminar/
│           ├── Main.java                    # 程序入口
│           ├── model/                       # 数据模型层
│           │   ├── User.java
│           │   ├── Student.java
│           │   ├── Evaluator.java
│           │   ├── Coordinator.java
│           │   ├── Session.java
│           │   ├── Registration.java
│           │   ├── Evaluation.java
│           │   ├── Award.java
│           │   └── enums/
│           │       ├── Role.java
│           │       ├── SessionStatus.java
│           │       ├── SessionType.java
│           │       ├── RegistrationStatus.java
│           │       └── AwardType.java
│           ├── service/                     # 业务逻辑层
│           │   ├── AuthService.java
│           │   ├── SessionService.java
│           │   ├── RegistrationService.java
│           │   ├── EvaluationService.java
│           │   ├── AwardService.java
│           │   └── ReportService.java
│           ├── repository/                  # 数据访问层
│           │   ├── UserRepository.java
│           │   ├── SessionRepository.java
│           │   ├── RegistrationRepository.java
│           │   └── EvaluationRepository.java
│           ├── ui/                          # 视图层
│           │   ├── MainFrame.java           # 主窗口
│           │   ├── components/              # 通用组件
│           │   │   ├── SidebarPanel.java
│           │   │   ├── HeaderPanel.java
│           │   │   ├── StatusBar.java
│           │   │   ├── StyledButton.java
│           │   │   ├── StyledTextField.java
│           │   │   ├── StyledTable.java
│           │   │   ├── CardPanel.java
│           │   │   └── RoundedPanel.java
│           │   ├── dialogs/                 # 对话框
│           │   │   ├── LoginDialog.java
│           │   │   ├── ConfirmDialog.java
│           │   │   └── MessageDialog.java
│           │   └── panels/                  # 功能面板
│           │       ├── guest/
│           │       │   └── ScheduleViewPanel.java
│           │       ├── student/
│           │       │   ├── RegistrationPanel.java
│           │       │   ├── UploadPanel.java
│           │       │   └── StatusPanel.java
│           │       ├── evaluator/
│           │       │   ├── AssignedListPanel.java
│           │       │   └── EvaluationPanel.java
│           │       └── coordinator/
│           │           ├── DashboardPanel.java
│           │           ├── UserManagementPanel.java
│           │           ├── SessionManagementPanel.java
│           │           ├── AssignmentPanel.java
│           │           ├── AwardPanel.java
│           │           └── ReportPanel.java
│           └── util/                        # 工具类
│               ├── UIConstants.java         # UI常量
│               ├── UIUtils.java             # UI工具方法
│               ├── FileUtils.java
│               ├── ValidationUtils.java
│               └── DataManager.java         # 数据持久化
└── resources/
    ├── data/                                # 数据文件
    │   ├── users.json
    │   ├── sessions.json
    │   └── registrations.json
    └── icons/                               # 图标资源
```

## 核心类设计

### Model 层
```java
// User.java - 用户基类
public abstract class User {
    protected Long id;
    protected String username;
    protected String password;
    protected String name;
    protected String email;
    protected Role role;
    // getters, setters
}

// Student.java
public class Student extends User {
    private String studentId;
    private String program;      // 专业
    private String supervisor;   // 导师
    // 构造函数中设置 role = Role.STUDENT
}

// Session.java
public class Session {
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String venue;
    private SessionType type;
    private int capacity;
    private int registered;
    private SessionStatus status;
    private String description;
}

// Registration.java
public class Registration {
    private Long id;
    private Long studentId;
    private Long sessionId;
    private String researchTitle;
    private String abstractText;
    private String supervisorName;
    private SessionType presentationType;
    private RegistrationStatus status;
    private String filePath;
    private LocalDateTime createdAt;
}

// Evaluation.java
public class Evaluation {
    private Long id;
    private Long evaluatorId;
    private Long registrationId;
    private int problemClarity;
    private int methodology;
    private int results;
    private int presentationQuality;
    private String comments;
    private boolean submitted;
    private LocalDateTime submittedAt;
    
    public int getTotalScore() {
        return problemClarity + methodology + results + presentationQuality;
    }
}
```

### Service 层模式
```java
// 单例模式服务
public class AuthService {
    private static AuthService instance;
    private User currentUser;
    private Role currentRole = Role.GUEST;
    
    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }
    
    public boolean login(String username, String password, Role role);
    public void logout();
    public User getCurrentUser();
    public Role getCurrentRole();
    public boolean hasPermission(String permission);
}
```

### UI 组件基类
```java
// 基础面板 - 所有功能面板继承此类
public abstract class BasePanel extends JPanel {
    protected CardPanel contentCard;
    
    public BasePanel() {
        setBackground(UIConstants.BG_MAIN);
        setLayout(new BorderLayout());
        initComponents();
        loadData();
    }
    
    protected abstract void initComponents();
    protected abstract void loadData();
    public abstract void refresh();
}
```

## 命名规范

### 类命名
- Panel 后缀: UI面板类 (RegistrationPanel)
- Dialog 后缀: 对话框类 (LoginDialog)
- Service 后缀: 服务类 (AuthService)
- Repository 后缀: 数据访问类 (UserRepository)

### 方法命名
- 获取数据: get*, find*, load*
- 保存数据: save*, create*, update*
- 删除数据: delete*, remove*
- 验证: validate*, check*, is*
- UI初始化: init*, setup*, create*Component

### 常量命名
- 全大写下划线分隔: PRIMARY_COLOR, MAX_FILE_SIZE

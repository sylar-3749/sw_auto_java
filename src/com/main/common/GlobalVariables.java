package common;
public class GlobalVariables {
    // 基础URL配置
    public static final String BASE_URL = "https://creative-koala-91iahe-dev-ed.lightning.force.com/lightning";
    public static final String FORCE_TOKEN = "Biz5pREnuGXFyQyMzykOs3XI";
    
    // 账号密码配置（注意：实际生产环境中建议使用环境变量或加密存储）
    public static final String USERNAME = "sylar_wan@creative-koala-91iahe.com";
    public static final String PASSWORD = "Pwcwelcome2";
    
    // 其他常用全局参数
    public static final int TIMEOUT_SHORT = 20; // 较短请求超时时间（秒）
    public static final int TIMEOUT = 30;  // 标准请求超时时间（秒）
    public static final int TIMEOUT_MEDIUM = 40; // 中等请求超时时间（秒）
    public static final int TIMEOUT_LONG = 120; // 较长请求超时时间（秒）
    public static final int TIMEOUT_SUPERLONG = 300; // 超长请求超时时间（秒）
    public static final int IMPLICIT_WAIT = 10; // 实时等待时间（秒）
    public static final int MAX_RETRY_COUNT = 3;  // 最大重试次数
}
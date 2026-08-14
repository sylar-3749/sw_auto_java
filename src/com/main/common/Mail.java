package common;

import javax.mail.*;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import java.util.Arrays;
import java.util.Properties;


public class Mail {

    // ─── IMAP 主机常量 ───────────────────────────────────────────
    public static final String IMAP_GMAIL   = "imap.gmail.com";
    public static final String IMAP_HOTMAIL = "imap-mail.outlook.com";
    public static final int    IMAPS_PORT   = 993;

    /**
     * 通用 IMAP 服务 — 所有邮箱提供商共用同一套逻辑，只差 host 不同。
     */
    public static class ImapService {

        private final String host;

        public ImapService(String host) {
            this.host = host;
        }

        /** 构建 Properties，消除双大括号匿名初始化的写法 */
        private Properties buildProperties() {
            Properties props = new Properties();
            props.put("mail.imap.host", host);
            props.put("mail.imap.port", String.valueOf(IMAPS_PORT));
            props.put("mail.imap.starttls.enable", "true");
            props.put("mail.imap.ssl.enable", "true");
            return props;
        }

        /** 建立 IMAP 连接并返回 Store，供后续操作复用 */
        public Store connect(String username, String password) throws MessagingException {
            Session session = Session.getDefaultInstance(buildProperties());
            Store store = session.getStore("imaps");
            store.connect(host, username, password);
            System.out.println("Connected to " + host + " as " + username);
            return store;
        }

        /**
         * 按邮件主题搜索 INBOX 中的邮件并打印摘要。
         * @param store  已连接的 Store（来自 connect()）
         * @param query  主题搜索关键词
         */
        public void searchAndProcess(Store store, String query) throws MessagingException {
            searchAndProcess(store, new SubjectTerm(query));
        }

        /**
         * 按自定义 SearchTerm 搜索 INBOX 中的邮件并打印摘要。
         * 开放 SearchTerm 参数，方便后续扩展（按发件人、日期、标记等搜索）。
         */
        public void searchAndProcess(Store store, SearchTerm searchTerm) throws MessagingException {
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            Message[] messages = emailFolder.search(searchTerm);
            System.out.println("Found " + messages.length + " email(s) matching criteria");

            for (int i = 0; i < messages.length; i++) {
                Message msg = messages[i];
                System.out.println("---------------------------------");
                System.out.println("Subject: " + msg.getSubject());
                System.out.println("From:    " + Arrays.toString(msg.getFrom()));

                // 发送/接收日期（如果可用）
                try {
                    System.out.println("Date:    " + msg.getSentDate());
                } catch (MessagingException ignored) { }

                // ==============================================
            }

            emailFolder.close(false);   // false = 不持久化删除标记
        }
    }

    // ─── 预置快捷方法 ────────────────────────────────────────────

    /** 快捷：Gmail */
    public static ImapService gmail() {
        return new ImapService(IMAP_GMAIL);
    }

    /** 快捷：Hotmail / Outlook */
    public static ImapService hotmail() {
        return new ImapService(IMAP_HOTMAIL);
    }

    // ─── 示例入口 ────────────────────────────────────────────────

    public static void main(String[] args) {
        // 凭证通过环境变量传入，避免硬编码密码泄漏到代码仓库
        String host     = System.getenv("MAIL_HOST");     // 默认取环境变量，也可硬编码测试
        String username = System.getenv("MAIL_USERNAME");
        String password = System.getenv("MAIL_PASSWORD");
        String query    = System.getenv().getOrDefault("MAIL_QUERY",
                "Role digest from the Talent Exchange");

        if (host == null || username == null || password == null) {
            System.err.println("Usage: set MAIL_HOST, MAIL_USERNAME, MAIL_PASSWORD environment variables");
            return;
        }

        ImapService service = new ImapService(host);

        try {
            Store store = service.connect(username, password);
            service.searchAndProcess(store, query);
            store.close();
        } catch (Exception e) {
            System.err.println("[Error] " + e.getMessage());
            e.printStackTrace();
        }
    }
}

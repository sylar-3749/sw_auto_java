# tests/features/login.feature
Feature: 登录功能（Cucumber Java 版）
  作为用户，验证登录成功/失败场景

  @login-success
  Scenario: 正确账号密码登录成功
    Given 访问登录页面
    When 输入用户名 "sylar_wan@creative-koala-91iahe.com" 和密码 "Pwcwelcome2"
    And 点击登录按钮

  @login-failure
  Scenario Outline: 错误账号密码登录失败
    Given 访问登录页面
    When 输入用户名 "<username>" 和密码 "<password>"
    And 点击登录按钮
    Then 显示错误提示

    Examples:
      | username   | password    |
      |            | 123456      |
      | test_user  |             |
      | test_user  | Hellow0Rld  |

  @login-failure-data-driven
  Scenario: 错误账号密码登录失败（动态数据）
    Given 访问登录页面
    When 从数据文件加载无效凭证并登录
    And 点击登录按钮
    Then 显示错误提示

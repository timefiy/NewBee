package cn.zzuli.shopapp;

public class User {
    private String account;    // 登录名（手机号）
    private String nickname;  // 自动同步账号
    private String password;  // 独立密码
    private String bio;       // 个性签名

    // 构造函数强制昵称同步账号
    public User(String account, String password) {
        this.account = account;
        this.nickname = account; // 关键同步逻辑
        this.password = password;
    }

    // Getter/Setter（省略部分）

    public String getAccount() {
        return account;
    }

    public String getBio() {
        return bio;
    }

    public String getPassword() {
        return password;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    // 禁止单独修改昵称
    public void setNickname(String nickname) {
        throw new UnsupportedOperationException("昵称不可手动修改");
    }
}
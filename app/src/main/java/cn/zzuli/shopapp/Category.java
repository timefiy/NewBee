package cn.zzuli.shopapp;

public class Category {
    private int id;
    private String name;
    private String bigTitle; // 新增用于存储大标题的字段

    public Category(int id, String name, String bigTitle) {
        this.id = id;
        this.name = name;
        this.bigTitle = bigTitle;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBigTitle() {
        return bigTitle;
    }
}

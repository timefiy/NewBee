package cn.zzuli.shopapp.entity;

// created by LWH
//用于home页面的上面的导航栏
public class Icon {
    private int categoryId; // 对应JSON中的 categoryId
    private String name;    // 对应JSON中的 name
    private String imgUrl;  // 对应JSON中的 imgUrl

    // 保持原有的变量，但主要使用新的变量来匹配JSON结构
    private int iId;
    private String iName;

    public Icon() {
    }

    public Icon(int categoryId, String name, String imgUrl) {
        this.categoryId = categoryId;
        this.name = name;
        this.imgUrl = imgUrl;
        // 可以选择性地同步到原有变量，或者废弃原有变量
        this.iId = categoryId;
        this.iName = name;
    }

    // Getters and Setters for new fields
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        this.iId = categoryId; // 同步更新原有变量
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.iName = name; // 同步更新原有变量
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    // Getters and Setters for original fields (kept for compatibility)
    public int getIId() {
        return iId;
    }

    public void setIId(int iId) {
        this.iId = iId;
        this.categoryId = iId; // 同步更新新变量
    }

    public String getIName() {
        return iName;
    }

    public void setIName(String iName) {
        this.iName = iName;
        this.name = iName; // 同步更新新变量
    }

    @Override
    public String toString() {
        return "Icon{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                "}";
    }
} 
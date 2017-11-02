package com.aliletter.onpermission;

/**
 * Author: aliletter
 * Github: http://github.com/aliletter
 * Data: 2017/9/28.
 */

public class Permission {
    private String permision;
    private String content;

    public Permission(String permision, String content) {
        this.permision = permision;
        this.content = content;
    }

    public String getPermision() {
        return permision;
    }

    public void setPermision(String permision) {
        this.permision = permision;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

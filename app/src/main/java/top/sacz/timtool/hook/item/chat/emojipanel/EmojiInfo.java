package top.sacz.timtool.hook.item.chat.emojipanel;

public class EmojiInfo {
    private String path;
    private int type;
    private String md5;
    private String url;
    private String name;

    public int getType() {
        return type;
    }

    public EmojiInfo setType(int type) {
        this.type = type;
        return this;
    }

    public String getPath() {
        return path;
    }

    public EmojiInfo setPath(String path) {
        this.path = path;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public EmojiInfo setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public EmojiInfo setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getName() {
        return name;
    }

    public EmojiInfo setName(String name) {
        this.name = name;
        return this;
    }
}

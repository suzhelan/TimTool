package top.sacz.timtool.hook.item.chat.stickerpanel;

public class StickerInfo {
    private String path;
    private int type;
    private String md5;
    private String url;
    private String name;

    private long time;

    public long getTime() {
        return time;
    }

    public StickerInfo setTime(long time) {
        this.time = time;
        return this;
    }
    public int getType() {
        return type;
    }

    public StickerInfo setType(int type) {
        this.type = type;
        return this;
    }

    public String getPath() {
        return path;
    }

    public StickerInfo setPath(String path) {
        this.path = path;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public StickerInfo setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public StickerInfo setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getName() {
        return name;
    }

    public StickerInfo setName(String name) {
        this.name = name;
        return this;
    }
}

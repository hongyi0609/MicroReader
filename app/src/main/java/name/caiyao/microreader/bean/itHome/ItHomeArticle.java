package name.caiyao.microreader.bean.itHome;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

/**
 * Created by 蔡小木 on 2016/3/24 0024.
 */
@Root(name = "rss",strict=false)
public class ItHomeArticle {
    @Path("channel/item")
    @Element(name = "newssource")
    private String newssource;
    @Path("channel/item")
    @Element(name = "newsauthor")
    private String newsauthor;
    @Path("channel/item")
    @Element(name = "detail")
    private String detail;
    @Path("channel/item")

    public String getNewssource() {
        return newssource;
    }

    public void setNewssource(String newssource) {
        this.newssource = newssource;
    }

    public String getAuthor() {
        return newsauthor;
    }

    public void setAuthor(String author) {
        this.newsauthor = author;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getNewsauthor() {
        return newsauthor;
    }

    public void setNewsauthor(String newsauthor) {
        this.newsauthor = newsauthor;
    }
}

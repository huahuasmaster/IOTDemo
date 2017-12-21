package administrator.entity;

public class PictureDto {

    private long id;

    private String content;

    private String description;

    public PictureDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "PictureDto{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", description=" + description + '\'' +
                '}';
    }
}

package at.technikum.application.model;

public class UserData {
    private String name;
    private String bio;
    private String image;

    public UserData(String name, String bio, String image) {
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getImage() {
        return image;
    }
}

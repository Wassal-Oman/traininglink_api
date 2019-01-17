package om.metamorph.trainingLinks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by OJP on 30/05/2017.
 */
public class Institute extends BaseInstitute {

    private String description;
    @JsonProperty("arabic_description")
    private String arabicDescription;
    private String picture;
    private String address;
    @JsonProperty("arabic_address")
    private String arabicAddress;
    private Location location = new Location();
    private String phone;
    @JsonProperty("is_first_class")
    private boolean isFirstClass;
    private float rating;

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isFirstClass() {
        return isFirstClass;
    }

    public void setFirstClass(boolean firstClass) {
        isFirstClass = firstClass;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Institute() {
        //you need this because POJO
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArabicDescription() {
        return arabicDescription;
    }

    public void setArabicDescription(String arabicDescription) {
        this.arabicDescription = arabicDescription;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArabicAddress() {
        return arabicAddress;
    }

    public void setArabicAddress(String arabicAddress) {
        this.arabicAddress = arabicAddress;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

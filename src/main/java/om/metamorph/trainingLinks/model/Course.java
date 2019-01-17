package om.metamorph.trainingLinks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by OJP on 28/05/2017.
 */
public class Course {
    @JsonProperty("course_id")
    private long courseId;
    private String title;
    private String description;
    private String activities;
    private String milestones;
    private String category;
    @JsonProperty("course_address")
    private String address;

    @JsonProperty("arabic_title")
    private String arabicTitle;
    @JsonProperty("arabic_description")
    private String arabicDescription;
    @JsonProperty("arabic_activities")
    private String arabicActivities;
    @JsonProperty("arabic_milestones")
    private String arabicMilestones;
    @JsonProperty("arabic_category")
    private String arabicCategory;
    @JsonProperty("arabic_course_address")
    private String arabicCourseAddress;

    private float price;

    @JsonProperty("time_post")
    private long timePost;
    @JsonProperty("time_course_start")
    private long timeCourseStart;
    @JsonProperty("time_registration_deadline")
    private long timeRegistrationDeadline;
    private String picture;

    @JsonProperty("is_at_morning")
    private boolean isAtMorning;
    @JsonProperty("is_online")
    private boolean isOnline;

    private Location location = new Location();
    private Institute institute = new Institute();

    public Course() {
        //We need this because of POJO
    }

    public Institute getInstitute() {
        return institute;
    }

    public void setInstitute(Institute institute) {
        this.institute = institute;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public String getMilestones() {
        return milestones;
    }

    public void setMilestones(String milestones) {
        this.milestones = milestones;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getArabicTitle() {
        return arabicTitle;
    }

    public void setArabicTitle(String arabicTitle) {
        this.arabicTitle = arabicTitle;
    }

    public String getArabicDescription() {
        return arabicDescription;
    }

    public void setArabicDescription(String arabicDescription) {
        this.arabicDescription = arabicDescription;
    }

    public String getArabicActivities() {
        return arabicActivities;
    }

    public void setArabicActivities(String arabicActivities) {
        this.arabicActivities = arabicActivities;
    }

    public String getArabicMilestones() {
        return arabicMilestones;
    }

    public void setArabicMilestones(String arabicMilestones) {
        this.arabicMilestones = arabicMilestones;
    }

    public String getArabicCategory() {
        return arabicCategory;
    }

    public void setArabicCategory(String arabicCategory) {
        this.arabicCategory = arabicCategory;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public long getTimePost() {
        return timePost;
    }

    public void setTimePost(long timePost) {
        this.timePost = timePost;
    }

    public long getTimeRegistrationDeadline() {
        return timeRegistrationDeadline;
    }

    public void setTimeRegistrationDeadline(long timeRegistrationDeadline) {
        this.timeRegistrationDeadline = timeRegistrationDeadline;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @JsonProperty("is_at_morning")
    public boolean isAtMorning() {
        return isAtMorning;
    }

    @JsonProperty("is_at_morning")
    public void setAtMorning(boolean atMorning) {
        isAtMorning = atMorning;
    }

    @JsonProperty("is_online")
    public boolean isOnline() {
        return isOnline;
    }

    @JsonProperty("is_online")
    public void setOnline(boolean online) {
        isOnline = online;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getTimeCourseStart() {
        return timeCourseStart;
    }

    public void setTimeCourseStart(long timeCourseStart) {
        this.timeCourseStart = timeCourseStart;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArabicCourseAddress() {
        return arabicCourseAddress;
    }

    public void setArabicCourseAddress(String arabicCourseAddress) {
        this.arabicCourseAddress = arabicCourseAddress;
    }
}

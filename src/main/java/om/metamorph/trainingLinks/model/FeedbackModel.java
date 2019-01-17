package om.metamorph.trainingLinks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by OJP on 12/06/2017.
 */
public class FeedbackModel {

    @JsonProperty("feedback_id")
    private long feedbackId;
    @JsonProperty("institute_id")
    private long instituteId;
    @JsonProperty("user_email")
    private String userEmail;
    private String name;
    @JsonProperty("feedback_text")
    private String feedbackText;
    private short rating;
    @JsonProperty("is_anonymous")
    private boolean isAnonymous;
    private long datetime;

    public FeedbackModel() {
        //You need this because POJO
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public long getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(long instituteId) {
        this.instituteId = instituteId;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public short getRating() {
        return rating;
    }

    public void setRating(short rating) {
        this.rating = rating;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }
}

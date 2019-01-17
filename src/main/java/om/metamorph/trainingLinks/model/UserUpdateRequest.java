package om.metamorph.trainingLinks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by OJP on 06/07/2017.
 */
public class UserUpdateRequest {

    @JsonProperty("old_password")
    private String oldPassword;
    @JsonProperty("new_password")
    private String newPassword;
    private String phone;

    public UserUpdateRequest() {
        //Keep this because pojo
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

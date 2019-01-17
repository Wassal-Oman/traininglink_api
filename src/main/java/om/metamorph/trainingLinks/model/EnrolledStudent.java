package om.metamorph.trainingLinks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by OJP on 12/06/2017.
 */
public class EnrolledStudent {

    @JsonProperty("enrolled_name")
    private String name;
    @JsonProperty("enrolled_phone")
    private String phone;

    public EnrolledStudent() {
        //you need this because POJO
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
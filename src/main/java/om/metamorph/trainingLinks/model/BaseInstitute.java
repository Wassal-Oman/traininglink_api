package om.metamorph.trainingLinks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by OJP on 11/06/2017.
 */
public class BaseInstitute {
    @JsonProperty("institute_id")
    private long instituteId;
    private String name;
    @JsonProperty("arabic_name")
    private String arabicName;

    public BaseInstitute() {
        //you need this because POJO
    }

    public long getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(long instituteId) {
        this.instituteId = instituteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArabicName() {
        return arabicName;
    }

    public void setArabicName(String arabicName) {
        this.arabicName = arabicName;
    }
}

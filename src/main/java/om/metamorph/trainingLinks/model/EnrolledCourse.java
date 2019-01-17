package om.metamorph.trainingLinks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by OJP on 12/06/2017.
 */
public class EnrolledCourse extends Course {
    @JsonProperty("enrolled_student")
    private EnrolledStudent enrolledStudent= new EnrolledStudent();

    public EnrolledCourse() {
        //you need this because POJO
    }

    public EnrolledStudent getEnrolledStudent() {
        return enrolledStudent;
    }

    public void setEnrolledStudent(EnrolledStudent enrolledStudent) {
        this.enrolledStudent = enrolledStudent;
    }
}

package om.metamorph.trainingLinks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by OJP on 19/07/2017.
 */
public class CourseFilter {

    @JsonProperty("institute_id")
    private ArrayList<String> instituteId;
    @JsonProperty("course_categories")
    private ArrayList<String> courseCategories;
    @JsonProperty("course_locations")
    private ArrayList<String> courseLocations;

    public CourseFilter() {
        //need this because POJO
    }

    public ArrayList<String> getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(ArrayList<String> instituteId) {
        this.instituteId = instituteId;
    }

    public ArrayList<String> getCourseCategories() {
        return courseCategories;
    }

    public void setCourseCategories(ArrayList<String> courseCategories) {
        this.courseCategories = courseCategories;
    }

    public ArrayList<String> getCourseLocations() {
        return courseLocations;
    }

    public void setCourseLocations(ArrayList<String> courseLocations) {
        this.courseLocations = courseLocations;
    }
}

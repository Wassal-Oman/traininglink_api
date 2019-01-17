package om.metamorph.trainingLinks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by OJP on 23/07/2017.
 */
public class CourseLocations {
    private ArrayList<String> locations = new ArrayList<String>();
    @JsonProperty("arabic_locations")
    private ArrayList<String> arabicLocations = new ArrayList<String>();

    public CourseLocations() {
        //Blank because POJO
    }

    public ArrayList<String> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<String> locations) {
        this.locations = locations;
    }

    public ArrayList<String> getArabicLocations() {
        return arabicLocations;
    }

    public void setArabicLocations(ArrayList<String> arabicLocations) {
        this.arabicLocations = arabicLocations;
    }
}
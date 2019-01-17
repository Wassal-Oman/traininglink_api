package om.metamorph.trainingLinks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Sabri K on 25/07/2017.
 */
public class CourseCategory {
    @JsonProperty("category_id")
    private long categoryId;
    @JsonProperty("category")
    private String courseCategory;
    @JsonProperty("arabic_category")
    private String courseArabicCategory;

    public CourseCategory() {
        //empty because POJO
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCourseCategory() {
        return courseCategory;
    }

    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory;
    }

    public String getCourseArabicCategory() {
        return courseArabicCategory;
    }

    public void setCourseArabicCategory(String courseArabicCategory) {
        this.courseArabicCategory = courseArabicCategory;
    }
}

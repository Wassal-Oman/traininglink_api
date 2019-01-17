package om.metamorph.trainingLinks.database;

import static om.metamorph.trainingLinks.database.TableColumns.*;

/**
 * Created by Sabri K on 23/07/2017.
 */
public class Queries {
    static String BASE_COURSE_QUERY = "SELECT " +
            TABLE_COURSES + "." + COL_COURSES_COURSE_ID + ", " +
            TABLE_COURSES + "." + COL_COURSES_TITLE + ", " +
            TABLE_COURSES + "." + COL_COURSES_ACTIVITIES + ", " +
            TABLE_COURSES + "." + COL_COURSES_DESCRIPTION + ", " +
            TABLE_COURSES + "." + COL_COURSES_MILESTONES + ", " +
            TABLE_COURSES + "." + COL_COURSES_ADDRESS + ", " +
            TABLE_COURSES + "." + COL_COURSES_CATEGORY_ID + ", " +
            TABLE_COURSES + "." + COL_COURSES_ARABIC_TITLE + ", " +
            TABLE_COURSES + "." + COL_COURSES_ARABIC_DESCRIPTION + ", " +
            TABLE_COURSES + "." + COL_COURSES_ARABIC_ACTIVITIES + ", " +
            TABLE_COURSES + "." + COL_COURSES_ARABIC_MILESTONES + ", " +
            TABLE_COURSES + "." + COL_COURSES_ARABIC_ADDRESS + ", " +
            TABLE_COURSES + "." + COL_COURSES_PRICE + ", " +
            TABLE_COURSES + "." + COL_COURSES_TIME_POST + ", " +
            TABLE_COURSES + "." + COL_COURSES_TIME_START + ", " +
            TABLE_COURSES + "." + COL_COURSES_TIME_DEADLINE + ", " +
            TABLE_COURSES + "." + COL_COURSES_PICTURE + ", " +
            TABLE_COURSES + "." + COL_COURSES_IS_AT_MORNING + ", " +
            TABLE_COURSES + "." + COL_COURSES_IS_ONLINE + ", " +
            TABLE_COURSES + "." + COL_COURSES_LATITUDE + ", " +
            TABLE_COURSES + "." + COL_COURSES_LONGITUDE + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_ID + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_NAME + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_DESCRIPTION + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_ARABIC_NAME + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_ARABIC_DESCRIPTION + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_PICTURE + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_PHONE + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_LATITUDE + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_LONGITUDE + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_ADDRESS + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE__ARABIC_ADDRESS + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_FIRST_CLASS + ", " +
            TABLE_COURSE_CATEGORY + "." + COL_CATEGORY + ", " +
            TABLE_COURSE_CATEGORY + "." + COL_CATEGORY_ARABIC + ", " +
            "(SELECT AVG(" + TABLE_FEEDBACK + "." + COL_FEEDBACK_RATING +
            ") FROM " + TABLE_FEEDBACK + " WHERE " + TABLE_FEEDBACK + "." +
            COL_FEEDBACK_INSTITUTE_ID + " = " + TABLE_INSTITUTE + "." +
            COL_INSTITUTE_ID + ") AS " + COL_AVG_RATING_ALIAS +
            " FROM " + TABLE_COURSES +
            " INNER JOIN " + TABLE_INSTITUTE_COURSE_RELATIONSHIP +
            " ON " + TABLE_INSTITUTE_COURSE_RELATIONSHIP + "." + COL_ICR_COURSE_ID + " = " +
            TABLE_COURSES + "." + COL_COURSES_COURSE_ID +
            " INNER JOIN " + TABLE_INSTITUTE + " ON " + TABLE_INSTITUTE + "." + COL_INSTITUTE_ID + " = " +
            TABLE_INSTITUTE_COURSE_RELATIONSHIP + "." + COL_ICR_INSTITUTE_ID +
            " LEFT JOIN " + TABLE_COURSE_CATEGORY + " ON " + TABLE_COURSES + "." +
            COL_COURSES_CATEGORY_ID + "=" + TABLE_COURSE_CATEGORY + "." + COL_CATEGORY_ID;

    static String BASE_ENROLMENT_COURSE_QUERY = "SELECT " +
            TABLE_COURSES + "." + COL_COURSES_COURSE_ID + ", " +
            TABLE_COURSES + "." + COL_COURSES_TITLE + ", " +
            TABLE_COURSES + "." + COL_COURSES_ACTIVITIES + ", " +
            TABLE_COURSES + "." + COL_COURSES_DESCRIPTION + ", " +
            TABLE_COURSES + "." + COL_COURSES_MILESTONES + ", " +
            TABLE_COURSES + "." + COL_COURSES_ADDRESS + ", " +
            TABLE_COURSES + "." + COL_COURSES_CATEGORY_ID + ", " +
            TABLE_COURSES + "." + COL_COURSES_ARABIC_TITLE + ", " +
            TABLE_COURSES + "." + COL_COURSES_ARABIC_DESCRIPTION + ", " +
            TABLE_COURSES + "." + COL_COURSES_ARABIC_ACTIVITIES + ", " +
            TABLE_COURSES + "." + COL_COURSES_ARABIC_MILESTONES + ", " +
            TABLE_COURSES + "." + COL_COURSES_ARABIC_ADDRESS + ", " +
            TABLE_COURSES + "." + COL_COURSES_PRICE + ", " +
            TABLE_COURSES + "." + COL_COURSES_TIME_POST + ", " +
            TABLE_COURSES + "." + COL_COURSES_TIME_START + ", " +
            TABLE_COURSES + "." + COL_COURSES_TIME_DEADLINE + ", " +
            TABLE_COURSES + "." + COL_COURSES_PICTURE + ", " +
            TABLE_COURSES + "." + COL_COURSES_IS_AT_MORNING + ", " +
            TABLE_COURSES + "." + COL_COURSES_IS_ONLINE + ", " +
            TABLE_COURSES + "." + COL_COURSES_LATITUDE + ", " +
            TABLE_COURSES + "." + COL_COURSES_LONGITUDE + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_ID + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_NAME + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_DESCRIPTION + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_ARABIC_NAME + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_ARABIC_DESCRIPTION + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_PICTURE + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_PHONE + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_LATITUDE + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_LONGITUDE + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_ADDRESS + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE__ARABIC_ADDRESS + ", " +
            TABLE_INSTITUTE + "." + COL_INSTITUTE_FIRST_CLASS + ", " +
            TABLE_COURSE_CATEGORY + "." + COL_CATEGORY + ", " +
            TABLE_COURSE_CATEGORY + "." + COL_CATEGORY_ARABIC + ", " +
            TABLE_ENROLMENT + "." + COL_ENROLMENT_REGISTERED_NAME + ", " +
            TABLE_ENROLMENT + "." + COL_ENROLMENT_REGISTERED_PHONE + ", " +
            "(SELECT AVG(" + TABLE_FEEDBACK + "." + COL_FEEDBACK_RATING +
            ") FROM " + TABLE_FEEDBACK + " WHERE " + TABLE_FEEDBACK + "." +
            COL_FEEDBACK_INSTITUTE_ID + " = " + TABLE_INSTITUTE + "." +
            COL_INSTITUTE_ID + ") AS " + COL_AVG_RATING_ALIAS +
            " FROM " + TABLE_COURSES +
            " INNER JOIN " + TABLE_INSTITUTE_COURSE_RELATIONSHIP +
            " ON " + TABLE_INSTITUTE_COURSE_RELATIONSHIP + "." + COL_ICR_COURSE_ID + " = " +
            TABLE_COURSES + "." + COL_COURSES_COURSE_ID +
            " INNER JOIN " + TABLE_INSTITUTE + " ON " + TABLE_INSTITUTE + "." + COL_INSTITUTE_ID + " = " +
            TABLE_INSTITUTE_COURSE_RELATIONSHIP + "." + COL_ICR_INSTITUTE_ID +
            " LEFT JOIN " + TABLE_COURSE_CATEGORY + " ON " + TABLE_COURSES + "." +
            COL_COURSES_CATEGORY_ID + "=" + TABLE_COURSE_CATEGORY + "." + COL_CATEGORY_ID;
}

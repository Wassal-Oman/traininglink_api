package om.metamorph.trainingLinks.database;

/**
 * Created by Sabri K on 23/07/2017.
 */
public class TableColumns {
    //users table
    final static String TABLE_USERS = "users";
    final static String COL_USERS_EMAIL = "email";
    final static String COL_USERS_NAME = "name";
    final static String COL_USERS_PASSWORD_HASH = "password_hash";
    final static String COL_USERS_PASSWORD_SALT = "password_salt";
    final static String COL_USERS_PHONE = "user_phone";
    final static String COL_USERS_AUTHENTICATION_TYPE = "authentication_type";

    //course table
    final static String TABLE_COURSES = "courses";
    final static String COL_COURSES_COURSE_ID = "course_id";
    final static String COL_COURSES_TITLE = "title";
    final static String COL_COURSES_DESCRIPTION = "course_description";
    final static String COL_COURSES_ACTIVITIES = "activities";
    final static String COL_COURSES_MILESTONES = "milestones";
    final static String COL_COURSES_ADDRESS = "course_address";
    final static String COL_COURSES_ARABIC_TITLE = "arabic_title";
    final static String COL_COURSES_ARABIC_DESCRIPTION = "arabic_course_description";
    final static String COL_COURSES_ARABIC_ACTIVITIES = "arabic_activities";
    final static String COL_COURSES_ARABIC_ADDRESS = "arabic_course_address";
    final static String COL_COURSES_ARABIC_MILESTONES = "arabic_milestones";
    final static String COL_COURSES_CATEGORY_ID = "category_id";
    final static String COL_COURSES_PRICE = "price";
    final static String COL_COURSES_TIME_POST = "time_post";
    final static String COL_COURSES_TIME_START = "time_course_start";
    final static String COL_COURSES_TIME_DEADLINE = "time_registration_deadline";
    final static String COL_COURSES_PICTURE = "course_picture";
    final static String COL_COURSES_IS_AT_MORNING = "is_at_morning";
    final static String COL_COURSES_IS_ONLINE = "is_online";
    final static String COL_COURSES_LATITUDE = "course_location_latitude";
    final static String COL_COURSES_LONGITUDE = "course_location_longitude";
    final static String COL_AVG_RATING_ALIAS = "average_rating";
    final static String COL_COURSE_DELETED = "deleted";

    //Institute Table
    final static String TABLE_INSTITUTE = "institutes";
    final static String COL_INSTITUTE_ID = "institute_id";
    final static String COL_INSTITUTE_NAME = "name";
    final static String COL_INSTITUTE_DESCRIPTION = "institute_description";
    final static String COL_INSTITUTE_ADDRESS = "institute_address";
    final static String COL_INSTITUTE_ARABIC_NAME = "arabic_name";
    final static String COL_INSTITUTE_ARABIC_DESCRIPTION = "arabic_institute_description";
    final static String COL_INSTITUTE__ARABIC_ADDRESS = "arabic_institute_address";
    final static String COL_INSTITUTE_PICTURE = "institute_picture";
    final static String COL_INSTITUTE_PHONE = "institute_phone";
    final static String COL_INSTITUTE_LATITUDE = "institute_location_latitude";
    final static String COL_INSTITUTE_LONGITUDE = "institute_location_longitude";
    final static String COL_INSTITUTE_FIRST_CLASS = "is_first_class";

    //favourites table
    final static String TABLE_FAVOURITES = "favourites";
    final static String COL_FAVOURITES_ID = "favourite_id";
    final static String COL_FAVOURITES_COURSE_ID = "course_id";
    final static String COL_FAVOURITES_EMAIL = "email";

    //enrolment table
    final static String TABLE_ENROLMENT = "student_course_enrolment";
    final static String COL_ENROLMENT_ID = "student_course_enrolment";
    final static String COL_ENROLMENT_REGISTERED_NAME = "registered_name";
    final static String COL_ENROLMENT_REGISTERED_PHONE = "registered_phone";
    final static String COL_ENROLMENT_EMAIL = "email";
    final static String COL_ENROLMENT_COURSE_ID = "course_id";

    //access-token-lookup table
    final static String TABLE_TOKEN_LOOKUP = "access_token_lookup";
    final static String COL_TOKEN_LOOKUP_ACCESS_TOKEN = "access_token";
    final static String COL_TOKEN_LOOKUP_EMAIL = "email";
    final static String COL_TOKEN_LOOKUP_LAST_ACTIVITY = "last_activity_datetime";

    //Feedback table
    final static String TABLE_FEEDBACK = "feedback";
    final static String COL_FEEDBACK_ID = "feedback_id";
    final static String COL_FEEDBACK_TEXT = "feedback_text";
    final static String COL_FEEDBACK_RATING = "rating";
    final static String COL_FEEDBACK_IS_ANONYMOUS = "is_anonymous";
    final static String COL_FEEDBACK_DATETIME = "datetime";
    final static String COL_FEEDBACK_EMAIL = "email";
    final static String COL_FEEDBACK_INSTITUTE_ID = "institute_id";

    //table institute relationship table
    final static String TABLE_INSTITUTE_COURSE_RELATIONSHIP = "institute_course_relationship";
    final static String COL_ICR_COURSE_ID = "course_id";
    final static String COL_ICR_INSTITUTE_ID = "institute_id";

    //Advertisement Table
    final static String TABLE_ADVERTISEMENT = "advertisement";
    final static String COL_ADVERTISEMENT_ID = "advertisement_id";
    final static String COL_ADVERTISEMENT_URL = "url";
    final static String COL_ADVERTISEMENT_TARGET_URL = "target_url";
    final static String COL_ADVERTISEMENT_CLICKS = "clicks";
    final static String COL_ADVERTISEMENT_VIEWS = "views";
    final static String COL_ADVERTISEMENT_INSTITUTE_ID = "institute_id";

    //Course Category table
    final static String TABLE_COURSE_CATEGORY = "course_categories";
    final static String COL_CATEGORY_ID = "category_id";
    final static String COL_CATEGORY = "category";
    final static String COL_CATEGORY_ARABIC = "arabic_category";
}

package om.metamorph.trainingLinks.database;

import om.metamorph.trainingLinks.*;
import om.metamorph.trainingLinks.model.*;

import javax.security.auth.login.CredentialException;
import javax.security.auth.login.CredentialNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

import static om.metamorph.trainingLinks.database.Queries.BASE_COURSE_QUERY;
import static om.metamorph.trainingLinks.database.Queries.BASE_ENROLMENT_COURSE_QUERY;
import static om.metamorph.trainingLinks.database.TableColumns.*;

/**
 * Created by OJP on 11/05/2017.
 */
public class DatabaseManager {

    private static DatabaseManager ourInstance = null;

    public static DatabaseManager getInstance() throws SQLException {
        if(ourInstance == null){
            ourInstance = new DatabaseManager();
        }
        return ourInstance;
    }

    private Connection getConnection() throws SQLException {
        try{
            Class.forName(DatabaseConfig.DB_DRIVER);
        }
        catch (ClassNotFoundException e){
            System.out.println(e);
        }
        return DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_USERNAME, DatabaseConfig.DB_PASSWORD);
    }

    public void createNewUser(NewUser user) throws Exception {
        String query = "INSERT INTO `training_links`.`users` " +
                "(`email`, `name`, `password_hash`, `password_salt`, " + COL_USERS_PHONE + ", `authentication_type`) " +
                "VALUES (?, ?, ?, ?, ?,?);";

        Connection connection = getConnection();


        String passwordHash = null;
        byte[] passwordSalt = null;
        if(user.getAuthenticationType().equals(Validation.AUTHENTICATION_TYPE_EMAIL)) {
            AuthenticationManager authenticationManager = new AuthenticationManager(user.getPassword());
            passwordHash = authenticationManager.getHash();
            passwordSalt = authenticationManager.getSalt();
        }

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, user.getEmail());
        preparedStatement.setString(2, user.getName());
        preparedStatement.setString(3, passwordHash);
        preparedStatement.setBytes(4, passwordSalt);
        preparedStatement.setString(5, user.getPhone());
        preparedStatement.setString(6, user.getAuthenticationType());

        preparedStatement.execute();

        connection.close();
    }
    public void createFacebookUser(NewUser user) throws SQLException {
        String query = "INSERT INTO " + TABLE_USERS +
                "("+ COL_USERS_EMAIL +", " + COL_USERS_NAME + ", " + COL_USERS_PHONE + ", " +
                COL_USERS_AUTHENTICATION_TYPE + ") " +
                "VALUES (?, ?, ?, ?);";

        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, user.getEmail());
        preparedStatement.setString(2, user.getName());
        preparedStatement.setString(3, user.getPhone());
        preparedStatement.setString(4, Validation.AUTHENTICATION_TYPE_FACEBOOK);

        preparedStatement.execute();

        connection.close();
    }

    public String insertTokenIntoDb(User user, String token) throws SQLException {
        String query = "INSERT INTO `training_links`.`access_token_lookup` " +
                "(access_token, last_activity_datetime, email) " +
                "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE " +
                "access_token = ?, last_activity_datetime = ?;";

        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, token);
        preparedStatement.setLong(2, Calendar.getInstance().getTimeInMillis());
        preparedStatement.setString(3, user.getEmail());
        preparedStatement.setString(4, token);
        preparedStatement.setLong(5, Calendar.getInstance().getTimeInMillis());

        preparedStatement.execute();

        connection.close();

        return token;
    }

    /**This returns user data if the user is credentials; otherwise, it throws an Exception*/
    public UserRow getAuthenticatedUserData(User user) throws CredentialException, SQLException {
        String query = "SELECT * FROM training_links.users WHERE " + COL_USERS_EMAIL + " = ?;";
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,user.getEmail());
        ResultSet resultSet = preparedStatement.executeQuery();
        UserRow userRow = new UserRow();
        boolean isEmpty = true;
        while (resultSet.next()){
            userRow.setEmail(resultSet.getString(COL_USERS_EMAIL));
            userRow.setName(resultSet.getString(COL_USERS_NAME));
            userRow.setPhone(resultSet.getString(COL_USERS_PHONE));
            userRow.setAuthenticationType(resultSet.getString(COL_USERS_AUTHENTICATION_TYPE));
            userRow.setPasswordHash(resultSet.getString(COL_USERS_PASSWORD_HASH));
            userRow.setPasswordSalt(resultSet.getBytes(COL_USERS_PASSWORD_SALT));
            isEmpty = false;
        }
        connection.close();
        if(isEmpty){
            throw new CredentialException();
        }
        return userRow;
    }

    public void deleteAccessToken(String accessToken) throws SQLException, CredentialNotFoundException {
        String query = "DELETE FROM training_links.access_token_lookup WHERE access_token = ?;";
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,accessToken);
        int rowsAffected = preparedStatement.executeUpdate();
        connection.close();
        if(rowsAffected == 0){
            throw new CredentialNotFoundException();
        }
        if(rowsAffected > 1){
            throw new SQLException();
        }
    }

    public ArrayList<Course> getFeaturedCourses(long offset, long limit) throws SQLException, NoCoursesException {
        StringBuilder query = new StringBuilder(BASE_COURSE_QUERY);

        //append WHERE courses are not deleted
        query.append(" WHERE " + TABLE_COURSES + "." + COL_COURSE_DELETED + "=0");

        //append AND courses are first class
        query.append(" AND " + TABLE_INSTITUTE + "." + COL_INSTITUTE_FIRST_CLASS + "=1");

        //append LIMIT and OFFSET to query
        query .append(" LIMIT " + limit + " OFFSET " + offset + ";");

        return getCourses(query.toString());
    }

    public ArrayList<Course> getUrgentCourses(long offset, long limit) throws SQLException, NoCoursesException {
        long now = Calendar.getInstance().getTimeInMillis()/1000;

        StringBuilder query = new StringBuilder(BASE_COURSE_QUERY);

        //append WHERE courses are not deleted
        query.append(" WHERE " + TABLE_COURSES + "." + COL_COURSE_DELETED + "=0");

        //append AND courses are urgent
        query.append(" AND (" + TABLE_COURSES + "." + COL_COURSES_TIME_START  + " - " + now + ") < " + Validation.URGENT_TIME);

        //append LIMIT and OFFSET to query
        query .append(" LIMIT " + limit + " OFFSET " + offset + ";");

        System.out.println(query.toString());

        return getCourses(query.toString());
    }

    public ArrayList<Course> getEndingCourses(long offset, long limit) throws SQLException, NoCoursesException {
        long now = Calendar.getInstance().getTimeInMillis()/1000;

        StringBuilder query = new StringBuilder(BASE_COURSE_QUERY);

        //append WHERE courses are not deleted
        query.append(" WHERE " + TABLE_COURSES + "." + COL_COURSE_DELETED + "=0");

        //append AND courses start soon
        query.append(" AND (" + TABLE_COURSES + "." + COL_COURSES_TIME_START  + " - " + now + ") < " + Validation.SOON_TIME);

        //append LIMIT and OFFSET to query
        query .append(" LIMIT " + limit + " OFFSET " + offset + ";");

        return getCourses(query.toString());
    }

    public ArrayList<Course> getSearchCourses(String searchQuery, long offset, long limit) throws SQLException,
            NoCoursesException {
        StringBuilder query = new StringBuilder(BASE_COURSE_QUERY);

        //append WHERE courses are not deleted
        query.append(" WHERE " + TABLE_COURSES + "." + COL_COURSE_DELETED + "=0 AND(");

        //append search key with OR
        query.append(TABLE_COURSES + "." + COL_COURSES_TITLE + " LIKE ?" +
                " OR " + TABLE_COURSES + "." + COL_COURSES_ARABIC_TITLE + " LIKE ?)");

        //append LIMIT and OFFSET to query
        query .append(" LIMIT " + limit + " OFFSET " + offset + ";");

        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
        preparedStatement.setString(1,"%" + searchQuery + "%");
        preparedStatement.setString(2,"%" + searchQuery + "%");
        ResultSet resultSet = preparedStatement.executeQuery();

        connection.close();

        ArrayList<Course> courseArrayList = new ArrayList<Course>();
        while (resultSet.next()){
            Course course = resultSetToCourse(resultSet);
            courseArrayList.add(course);
        }
        if(courseArrayList.isEmpty()){
            throw new NoCoursesException();
        }
        return courseArrayList;
    }

    public ArrayList<Course> getCourses(long offset, long limit) throws SQLException, NoCoursesException {
        StringBuilder query = new StringBuilder(BASE_COURSE_QUERY);

        //append WHERE courses are not deleted
        query.append(" WHERE " + TABLE_COURSES + "." + COL_COURSE_DELETED + "=0");

        //append LIMIT and OFFSET to query
        query .append(" LIMIT " + limit + " OFFSET " + offset + ";");

        return getCourses(query.toString());
    }

    public ArrayList<Course> getCoursesByInstitute(ArrayList<String> instituteId, long offset, long limit) throws SQLException, NoCoursesException {
        StringBuilder query = new StringBuilder(BASE_COURSE_QUERY);

        //append WHERE courses are not deleted
        query.append(" WHERE " + TABLE_COURSES + "." + COL_COURSE_DELETED + "=0 AND (");

        //append first WHERE institute = institute
        query.append(TABLE_INSTITUTE + "." + COL_INSTITUTE_ID + " = \"" + instituteId.get(0) + "\"");
        //append all subsequent institute = institute
        for (int i = 1 ; i < instituteId.size() ; i++) {
            query.append(" OR " + TABLE_INSTITUTE + "." + COL_INSTITUTE_ID + " = \"" + instituteId.get(i) + "\"");
        }
        //close bracket that encloses all the Category OR
        query.append(")");

        //append LIMIT and OFFSET to query
        query .append(" LIMIT " + limit + " OFFSET " + offset + ";");

        return getCourses(query.toString());
    }

    public ArrayList<Course> getCoursesByCategory(ArrayList<String> category, long offset, long limit) throws SQLException, NoCoursesException {
        StringBuilder query = new StringBuilder(BASE_COURSE_QUERY);

        //append WHERE courses are not deleted
        query.append(" WHERE " + TABLE_COURSES + "." + COL_COURSE_DELETED + "=0 AND (");

        //append first where category = category_id
        query.append(TABLE_COURSE_CATEGORY + "." + COL_CATEGORY_ID + " = " + category.get(0));

        //append all subsequent category = category_id
        for (int i = 1 ; i < category.size() ; i++) {
            query.append(" OR " + TABLE_COURSE_CATEGORY + "." + COL_CATEGORY_ID + " = " + category.get(i));
        }
        //close bracket that encloses all the Category OR
        query.append(")");

        //append LIMIT and OFFSET to query
        query .append(" LIMIT " + limit + " OFFSET " + offset + ";");

        return getCourses(query.toString());
    }

    public ArrayList<Course> getCoursesByLocation(ArrayList<String> courseLocations, long offset, long limit) throws SQLException, NoCoursesException {
        StringBuilder query = new StringBuilder(BASE_COURSE_QUERY);

        //append WHERE courses are not deleted
        query.append(" WHERE " + TABLE_COURSES + "." + COL_COURSE_DELETED + "=0 AND (");

        //English addresses!
        //append first where location = location
        query.append(TABLE_COURSES + "." + COL_COURSES_ADDRESS + " = ? ");

        //append all subsequent category = category_id
        for (int i = 1 ; i < courseLocations.size() ; i++) {
            query.append(" OR " + TABLE_COURSES + "." + COL_COURSES_ADDRESS + " = ?");
        }

        //append OR course.arabic_address = address
        for (int i = 0 ; i < courseLocations.size() ; i++) {
            query.append(" OR " + TABLE_COURSES + "." + COL_COURSES_ARABIC_ADDRESS + " = ?");
        }

        //close bracket that encloses all the Category OR
        query.append(")");

        //append LIMIT and OFFSET to query
        query .append(" LIMIT " + limit + " OFFSET " + offset + ";");

        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

        //plug in user input for english addresses
        int parameterIndexCounter =1;
        for (int i = 0 ; i < courseLocations.size() ; i++) {
            preparedStatement.setString(parameterIndexCounter, courseLocations.get(i));
            parameterIndexCounter++;
        }

        //plug in user input for arabic addresses
        for (int i = 0 ; i < courseLocations.size() ; i++) {
            preparedStatement.setString(parameterIndexCounter, courseLocations.get(i));
            parameterIndexCounter++;
        }

        ResultSet resultSet = preparedStatement.executeQuery();

        connection.close();

        ArrayList<Course> courseArrayList = new ArrayList<Course>();
        while (resultSet.next()){
            Course course = resultSetToCourse(resultSet);
            courseArrayList.add(course);
        }
        if(courseArrayList.isEmpty()){
            throw new NoCoursesException();
        }
        return courseArrayList;
    }

    public ArrayList<Course> getCourses(String query) throws SQLException, NoCoursesException {
        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<Course> courseArrayList = new ArrayList<Course>();
        while (resultSet.next()){
            Course course = resultSetToCourse(resultSet);
            courseArrayList.add(course);
        }

        connection.close();

        if(courseArrayList.isEmpty()){
            throw new NoCoursesException();
        }
        return courseArrayList;
    }

    public Course getCourseByIdFromDb(long courseId) throws SQLException, CourseNotFoundException {

        Connection connection = getConnection();

        StringBuilder query = new StringBuilder(BASE_COURSE_QUERY);

        //append WHERE course_id = id
        query.append(" WHERE " + TABLE_COURSES + "." + COL_COURSES_COURSE_ID + " = ?");

        PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
        preparedStatement.setString(1,String.valueOf(courseId));
        ResultSet resultSet = preparedStatement.executeQuery();

        connection.close();

        if(resultSet.next()){
            return resultSetToCourse(resultSet);
        }
        else{
            throw new CourseNotFoundException();
        }
    }

    private Course resultSetToCourse(ResultSet resultSet) throws SQLException {
        //course details
        Course course = new Course();
        course.setCourseId(resultSet.getLong(COL_COURSES_COURSE_ID));
        course.setTitle(resultSet.getString(COL_COURSES_TITLE));
        course.setDescription(resultSet.getString(COL_COURSES_DESCRIPTION));
        course.setActivities(resultSet.getString(COL_COURSES_ACTIVITIES));
        course.setMilestones(resultSet.getString(COL_COURSES_MILESTONES));
        course.setAddress(resultSet.getString(COL_COURSES_ADDRESS));
        course.setCategory(resultSet.getString(COL_CATEGORY));
        course.setArabicTitle(resultSet.getString(COL_COURSES_ARABIC_TITLE));
        course.setArabicDescription(resultSet.getString(COL_COURSES_ARABIC_DESCRIPTION));
        course.setArabicActivities(resultSet.getString(COL_COURSES_ARABIC_ACTIVITIES));
        course.setArabicMilestones(resultSet.getString(COL_COURSES_ARABIC_MILESTONES));
        course.setArabicCategory(resultSet.getString(COL_CATEGORY_ARABIC));
        course.setArabicCourseAddress(resultSet.getString(COL_COURSES_ARABIC_ADDRESS));
        course.setPrice(resultSet.getFloat(COL_COURSES_PRICE));
        course.setTimePost(resultSet.getLong(COL_COURSES_TIME_POST));
        course.setTimeCourseStart(resultSet.getLong(COL_COURSES_TIME_START));
        course.setTimeRegistrationDeadline(resultSet.getLong(COL_COURSES_TIME_DEADLINE));
        course.setPicture(resultSet.getString(COL_COURSES_PICTURE));
        course.setAtMorning(resultSet.getBoolean(COL_COURSES_IS_AT_MORNING));
        course.setOnline(resultSet.getBoolean(COL_COURSES_IS_ONLINE));
        course.getLocation().setLatitude(resultSet.getFloat(COL_COURSES_LATITUDE));
        course.getLocation().setLongitude(resultSet.getFloat(COL_COURSES_LONGITUDE));

        //institute details
        Institute institute = resultSetToInstitute(resultSet);
        institute.setRating(resultSet.getFloat(COL_AVG_RATING_ALIAS));
        course.setInstitute(institute);
        return course;
    }

    public ArrayList<Institute> getInstitutesWithDetails() throws SQLException {
        Connection connection = getConnection();
        String query = "SELECT * FROM " + TABLE_INSTITUTE + ";";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        System.out.println("arrayList created");
        ArrayList<Institute> instituteArrayList = new ArrayList<Institute>();
        while (resultSet.next()){
            instituteArrayList.add(resultSetToInstitute(resultSet));
        }
        return instituteArrayList;
    }

    public ArrayList<BaseInstitute> getInstituteNames() throws SQLException{
        Connection connection = getConnection();
        String query = "SELECT institute_id, name, arabic_name FROM " + TABLE_INSTITUTE + ";";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        ArrayList<BaseInstitute> baseInstituteArrayList = new ArrayList<BaseInstitute>();
        while (resultSet.next()){
            BaseInstitute baseInstitute = new BaseInstitute();
            baseInstitute.setInstituteId(resultSet.getLong(COL_INSTITUTE_ID));
            baseInstitute.setName(resultSet.getString(COL_INSTITUTE_NAME));
            baseInstitute.setArabicName(resultSet.getString(COL_INSTITUTE_ARABIC_NAME));
            baseInstituteArrayList.add(baseInstitute);
        }
        connection.close();
        return baseInstituteArrayList;
    }

    public Institute getInstituteByIdFromDb(long instituteId) throws SQLException, InstituteNotFoundException {
        Connection connection = getConnection();

        String query = "SELECT * FROM " + TABLE_INSTITUTE + " WHERE " + COL_INSTITUTE_ID + " = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,String.valueOf(instituteId));
        ResultSet resultSet = preparedStatement.executeQuery();
        connection.close();
        if(resultSet.next()){
            return resultSetToInstitute(resultSet);
        }
        else{
            throw new InstituteNotFoundException();
        }
    }

    private Institute resultSetToInstitute(ResultSet resultSet) throws SQLException {
        Institute institute = new Institute();
        institute.setInstituteId(resultSet.getLong(COL_INSTITUTE_ID));

        institute.setName(resultSet.getString(COL_INSTITUTE_NAME));
        institute.setDescription(resultSet.getString(COL_INSTITUTE_DESCRIPTION));
        institute.setAddress(resultSet.getString(COL_INSTITUTE_ADDRESS));

        institute.setArabicName(resultSet.getString(COL_INSTITUTE_ARABIC_NAME));
        institute.setArabicDescription(resultSet.getString(COL_INSTITUTE_ARABIC_DESCRIPTION));
        institute.setArabicAddress(resultSet.getString(COL_INSTITUTE__ARABIC_ADDRESS));

        institute.setPicture(resultSet.getString(COL_INSTITUTE_PICTURE));
        institute.setPhone(resultSet.getString(COL_INSTITUTE_PHONE));
        institute.getLocation().setLatitude(resultSet.getFloat(COL_INSTITUTE_LATITUDE));
        institute.getLocation().setLongitude(resultSet.getFloat(COL_INSTITUTE_LONGITUDE));
        institute.setFirstClass(resultSet.getBoolean(COL_INSTITUTE_FIRST_CLASS));
        return institute;
    }

    public void favouriteCourseInDb(long courseId, String email) throws SQLException {
        String query = "INSERT INTO " + TABLE_FAVOURITES + " (" + COL_FAVOURITES_COURSE_ID +
                ", " + COL_FAVOURITES_EMAIL + ")" +
                "SELECT * FROM (SELECT ?,?) AS tmp\n" +
                "WHERE NOT EXISTS (" +
                "    SELECT " + COL_FAVOURITES_COURSE_ID + ", " + COL_FAVOURITES_EMAIL +  " FROM " +
                TABLE_FAVOURITES + " WHERE " + COL_FAVOURITES_COURSE_ID + " = ?" +
                " AND " + COL_FAVOURITES_EMAIL + " = ?" +
                ") LIMIT 1;";

        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong(1, courseId);
        preparedStatement.setString(2, email);
        preparedStatement.setLong(3, courseId);
        preparedStatement.setString(4, email);
        preparedStatement.execute();
        connection.close();
    }

    public String getEmailFromToken(String token) throws SQLException, UserNotLoggedInException {
        Connection connection = getConnection();
        String query = "Select * from " + TABLE_TOKEN_LOOKUP + " WHERE " + COL_TOKEN_LOOKUP_ACCESS_TOKEN + " = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,String.valueOf(token));
        ResultSet resultSet = preparedStatement.executeQuery();
        connection.close();
        String email = null;
        if(resultSet.next()){
            email = resultSet.getString(COL_TOKEN_LOOKUP_EMAIL);
            updateLastActivity(email);
        }
        else{
            throw new UserNotLoggedInException();
        }
        return email;
    }

    private void updateLastActivity(String email) throws SQLException {
        String query = "UPDATE " + TABLE_TOKEN_LOOKUP + " SET " + COL_TOKEN_LOOKUP_LAST_ACTIVITY + "= ? " +
                "WHERE " + COL_TOKEN_LOOKUP_EMAIL + "= ?;";

        Connection connection = getConnection();

        long dateTime = Calendar.getInstance().getTimeInMillis();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong(1, dateTime);
        preparedStatement.setString(2, email);

        preparedStatement.execute();

        connection.close();
    }

    public void removeFavouriteFromDb(long courseId, String email) throws SQLException, CourseNotFoundException {
        String query = "DELETE FROM " + TABLE_FAVOURITES + " WHERE " + COL_FAVOURITES_COURSE_ID + "=? AND " +
                COL_FAVOURITES_EMAIL + " =?;";
        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong(1, courseId);
        preparedStatement.setString(2, email);
        int rowsAffected = preparedStatement.executeUpdate();
        if(rowsAffected < 1){
            throw new CourseNotFoundException();
        }
        connection.close();
    }

    public ArrayList<Course> getFavouritesByEmail(String email) throws SQLException, CourseNotFoundException {
        Connection connection = getConnection();

        StringBuilder query = new StringBuilder(BASE_COURSE_QUERY);

        //append favourites inner join
        query.append(" INNER JOIN " + TABLE_FAVOURITES +
                " ON " + TABLE_FAVOURITES + "." + COL_FAVOURITES_COURSE_ID + " = " +
                TABLE_COURSES + "." + COL_COURSES_COURSE_ID);

        //append WHERE courses are not deleted
        query.append(" WHERE " + TABLE_COURSES + "." + COL_COURSE_DELETED + "=0");

        //append where favourites is for this email
        query.append(" AND " + TABLE_FAVOURITES + "." + COL_FAVOURITES_EMAIL + " = ?");

        PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
        preparedStatement.setString(1,email);
        ResultSet resultSet = preparedStatement.executeQuery();
        connection.close();
        ArrayList<Course> courseArrayList = new ArrayList<Course>();
        while (resultSet.next()){
            Course course = resultSetToCourse(resultSet);
            courseArrayList.add(course);
        }
        if(courseArrayList.isEmpty()){
            throw new CourseNotFoundException();
        }
        return courseArrayList;
    }

    public void enrolCourseInDb(long courseId, String email, String name, String phone) throws SQLException {
        String query = "INSERT INTO " + TABLE_ENROLMENT + " (" + COL_ENROLMENT_REGISTERED_NAME +
                ", " + COL_ENROLMENT_REGISTERED_PHONE +
                ", " + COL_ENROLMENT_EMAIL +
                ", " + COL_ENROLMENT_COURSE_ID +
                ")" +
                "SELECT * FROM (SELECT ?,?,?,?) AS tmp\n" +
                "WHERE NOT EXISTS (" +
                "    SELECT * FROM " +
                TABLE_ENROLMENT + " WHERE " + COL_ENROLMENT_REGISTERED_NAME + " = ?" +
                " AND " + COL_COURSES_COURSE_ID + " = ?" + " AND " + COL_ENROLMENT_EMAIL + " = ?" +
                ") LIMIT 1;";

        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, phone);
        preparedStatement.setString(3, email);
        preparedStatement.setLong(4, courseId);
        preparedStatement.setString(5, name);
        preparedStatement.setLong(6, courseId);
        preparedStatement.setString(7, email);
        preparedStatement.execute();
        connection.close();
    }

    public ArrayList<EnrolledCourse> getEnrolmentsByEmail(String email) throws SQLException, CourseNotFoundException {

        StringBuilder query = new StringBuilder(BASE_ENROLMENT_COURSE_QUERY);

        //append favourites inner join
        query.append(" INNER JOIN " + TABLE_ENROLMENT + " ON " +
                TABLE_ENROLMENT + "." + COL_ENROLMENT_COURSE_ID + " = " +
                TABLE_COURSES + "." + COL_COURSES_COURSE_ID);

        //append where enrolment is for this email
        query.append(" WHERE " + TABLE_ENROLMENT + "." + COL_ENROLMENT_EMAIL + " = ?");

        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
        preparedStatement.setString(1,email);
        ResultSet resultSet = preparedStatement.executeQuery();
        connection.close();
        ArrayList<EnrolledCourse> courseArrayList = new ArrayList<EnrolledCourse>();
        while (resultSet.next()){
            EnrolledCourse course = resultSetToEnrolledCourse(resultSet);
            courseArrayList.add(course);
        }
        if(courseArrayList.isEmpty()){
            throw new CourseNotFoundException();
        }
        return courseArrayList;
    }

    private EnrolledCourse resultSetToEnrolledCourse(ResultSet resultSet) throws SQLException {
        EnrolledCourse course = new EnrolledCourse();
        course.setCourseId(resultSet.getLong(COL_COURSES_COURSE_ID));
        course.setTitle(resultSet.getString(COL_COURSES_TITLE));
        course.setDescription(resultSet.getString(COL_COURSES_DESCRIPTION));
        course.setActivities(resultSet.getString(COL_COURSES_ACTIVITIES));
        course.setMilestones(resultSet.getString(COL_COURSES_MILESTONES));
        course.setCategory(resultSet.getString(COL_COURSES_CATEGORY_ID));
        course.setArabicTitle(resultSet.getString(COL_COURSES_ARABIC_TITLE));
        course.setArabicDescription(resultSet.getString(COL_COURSES_ARABIC_DESCRIPTION));
        course.setArabicActivities(resultSet.getString(COL_COURSES_ARABIC_ACTIVITIES));
        course.setArabicMilestones(resultSet.getString(COL_COURSES_ARABIC_MILESTONES));
        course.setArabicCategory(resultSet.getString(COL_CATEGORY_ARABIC));
        course.setPrice(resultSet.getFloat(COL_COURSES_PRICE));
        course.setTimePost(resultSet.getLong(COL_COURSES_TIME_POST));
        course.setTimeRegistrationDeadline(resultSet.getLong(COL_COURSES_TIME_DEADLINE));
        course.setPicture(resultSet.getString(COL_COURSES_PICTURE));
        course.setAtMorning(resultSet.getBoolean(COL_COURSES_IS_AT_MORNING));
        course.setOnline(resultSet.getBoolean(COL_COURSES_IS_ONLINE));
        course.getLocation().setLatitude(resultSet.getFloat(COL_COURSES_LATITUDE));
        course.getLocation().setLongitude(resultSet.getFloat(COL_COURSES_LONGITUDE));
        course.getEnrolledStudent().setName(resultSet.getString(COL_ENROLMENT_REGISTERED_NAME));
        course.getEnrolledStudent().setPhone(resultSet.getString(COL_ENROLMENT_REGISTERED_PHONE));

        //institute details
        Institute institute = resultSetToInstitute(resultSet);
        institute.setRating(resultSet.getFloat(COL_AVG_RATING_ALIAS));
        course.setInstitute(institute);

        return course;
    }

    public FeedbackModel postFeedback(long instituteId, String email, FeedbackModel feedback) throws SQLException,
            InstituteNotFoundException, FeedbackNotFoundException {
        String query = "INSERT INTO " + TABLE_FEEDBACK + " (" + COL_FEEDBACK_TEXT + ", " + COL_FEEDBACK_RATING +
                ", " + COL_FEEDBACK_IS_ANONYMOUS + ", " + COL_FEEDBACK_DATETIME + ", " + COL_FEEDBACK_EMAIL +
                ", " + COL_FEEDBACK_INSTITUTE_ID + ") VALUES (?, ?, ?, ?, ?, ?);";
        Connection connection = getConnection();
        long datetime = Calendar.getInstance().getTimeInMillis();
        PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1,feedback.getFeedbackText());
        preparedStatement.setShort(2,feedback.getRating());
        preparedStatement.setBoolean(3,feedback.isAnonymous());
        preparedStatement.setLong(4,datetime);
        preparedStatement.setString(5,email);
        preparedStatement.setLong(6,instituteId);
        preparedStatement.executeUpdate();

        ResultSet keySet = preparedStatement.getGeneratedKeys();
        int newId = -1;
        if (keySet.next()) {
            newId = keySet.getInt(1);
        }

        connection.close();
        return getFeedbackById(newId);
    }

    public FeedbackModel getFeedbackById(long id) throws SQLException, FeedbackNotFoundException {
        String query = "select " + TABLE_FEEDBACK + "." + COL_FEEDBACK_ID + ", " +
                TABLE_FEEDBACK + "." + COL_FEEDBACK_TEXT + ", " +
                TABLE_FEEDBACK + "." + COL_FEEDBACK_RATING + ", " +
                TABLE_FEEDBACK + "." + COL_FEEDBACK_IS_ANONYMOUS + ", " +
                TABLE_FEEDBACK + "." + COL_FEEDBACK_DATETIME + ", " +
                TABLE_FEEDBACK + "." + COL_FEEDBACK_INSTITUTE_ID + ", " +
                TABLE_USERS + "." + COL_USERS_EMAIL + ", " +
                TABLE_USERS + "." + COL_USERS_NAME +
                " from " + TABLE_FEEDBACK +
                " inner join " + TABLE_USERS + " on " + TABLE_FEEDBACK + "." + COL_FEEDBACK_EMAIL +
                " = " + TABLE_USERS + "." + COL_USERS_EMAIL +
                " where " + TABLE_FEEDBACK + "." + COL_FEEDBACK_ID + " = ?;";
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong(1,id);
        ResultSet resultSet = preparedStatement.executeQuery();

        FeedbackModel feedbackModel;
        if (resultSet.next()){
            feedbackModel = resultSetToFeedback(resultSet);
        }
        else{
            throw new FeedbackNotFoundException();
        }
        connection.close();
        return feedbackModel;
    }

    private FeedbackModel resultSetToFeedback(ResultSet resultSet) throws SQLException {
        FeedbackModel feedbackModel = new FeedbackModel();
        feedbackModel.setFeedbackId(resultSet.getLong(COL_FEEDBACK_ID));
        feedbackModel.setInstituteId(resultSet.getLong(COL_FEEDBACK_INSTITUTE_ID));
        feedbackModel.setUserEmail(resultSet.getString(COL_USERS_EMAIL));
        feedbackModel.setName(resultSet.getString(COL_USERS_NAME));
        feedbackModel.setFeedbackText(resultSet.getString(COL_FEEDBACK_TEXT));
        feedbackModel.setRating(resultSet.getShort(COL_FEEDBACK_RATING));
        feedbackModel.setAnonymous(resultSet.getBoolean(COL_FEEDBACK_IS_ANONYMOUS));
        feedbackModel.setDatetime(resultSet.getLong(COL_FEEDBACK_DATETIME));
        return feedbackModel;
    }

    public ArrayList<FeedbackModel> getFeedbackByInstituteId(long instituteId) throws SQLException, FeedbackNotFoundException {
        String query = "select " + TABLE_FEEDBACK + "." + COL_FEEDBACK_ID + ", " +
                TABLE_FEEDBACK + "." + COL_FEEDBACK_TEXT + ", " +
                TABLE_FEEDBACK + "." + COL_FEEDBACK_RATING + ", " +
                TABLE_FEEDBACK + "." + COL_FEEDBACK_IS_ANONYMOUS + ", " +
                TABLE_FEEDBACK + "." + COL_FEEDBACK_DATETIME + ", " +
                TABLE_FEEDBACK + "." + COL_FEEDBACK_INSTITUTE_ID + ", " +
                TABLE_USERS + "." + COL_USERS_EMAIL + ", " +
                TABLE_USERS + "." + COL_USERS_NAME +
                " from " + TABLE_FEEDBACK +
                " inner join " + TABLE_USERS + " on " + TABLE_FEEDBACK + "." + COL_FEEDBACK_EMAIL +
                " = " + TABLE_USERS + "." + COL_USERS_EMAIL +
                " where " + TABLE_FEEDBACK + "." + COL_FEEDBACK_INSTITUTE_ID + " = ?;";
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong(1,instituteId);
        ResultSet resultSet = preparedStatement.executeQuery();
        connection.close();

        ArrayList<FeedbackModel> feedbackModels = new ArrayList<FeedbackModel>();
        boolean isEmpty = true;
        while (resultSet.next()){
            feedbackModels.add(resultSetToFeedback(resultSet));
            isEmpty = false;
        }

        if(isEmpty){
            throw new FeedbackNotFoundException();
        }

        return feedbackModels;
    }

    public ArrayList<Advertisement> getAllAdvertisementsFromDb() throws SQLException {
        Connection connection = getConnection();
        String query = "SELECT * FROM " + TABLE_ADVERTISEMENT + ";";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        connection.close();

        ArrayList<Advertisement> advertisements = new ArrayList<Advertisement>();
        while (resultSet.next()){
            Advertisement advertisement = resultSetToAdvertisement(resultSet);
            advertisements.add(advertisement);
        }
        return advertisements;
    }

    private Advertisement resultSetToAdvertisement(ResultSet resultSet) throws SQLException {
        Advertisement advertisement = new Advertisement();
        advertisement.setAdvertisementId(resultSet.getLong(COL_ADVERTISEMENT_ID));
        advertisement.setUrl(resultSet.getString(COL_ADVERTISEMENT_URL));
        advertisement.setTargetUrl(resultSet.getString(COL_ADVERTISEMENT_TARGET_URL));
        advertisement.setInstituteId(resultSet.getLong(COL_ADVERTISEMENT_INSTITUTE_ID));
        return advertisement;
    }

    public void updatePhone(String email, String phone) throws SQLException {
        String query = "UPDATE " + TABLE_USERS + " SET " + COL_USERS_PHONE + "= ? " +
                "WHERE " + COL_USERS_EMAIL + "= ?;";

        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, phone);
        preparedStatement.setString(2, email);

        preparedStatement.execute();

        connection.close();
    }

    public void updatePassword(String email, String newPassword) throws Exception {
        String query = "UPDATE " + TABLE_USERS +
                " SET " + COL_USERS_PASSWORD_HASH + " = ?, " + COL_USERS_PASSWORD_SALT + " = ? " +
                "WHERE " + COL_USERS_EMAIL + " = ?";

        Connection connection = getConnection();
        AuthenticationManager authenticationManager = new AuthenticationManager(newPassword);
        String passwordHash = authenticationManager.getHash();
        byte[] passwordSalt = authenticationManager.getSalt();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,passwordHash);
        preparedStatement.setBytes(2, passwordSalt);
        preparedStatement.setString(3, email);

        preparedStatement.execute();
        connection.close();
    }

    public CourseLocations getDistinctLocations() throws SQLException {
        String query = "SELECT DISTINCT " + COL_COURSES_ADDRESS + ", " +
                COL_COURSES_ARABIC_ADDRESS +
                " FROM " + TABLE_COURSES + ";";

        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        CourseLocations courseLocations = new CourseLocations();

        while(resultSet.next()){
            courseLocations.getLocations().add(resultSet.getString(COL_COURSES_ADDRESS));
            courseLocations.getArabicLocations().add(resultSet.getString(COL_COURSES_ARABIC_ADDRESS));
        }

        connection.close();
        return courseLocations;
    }

    public ArrayList<CourseCategory> getCourseCategories() throws SQLException {
        String query = "SELECT * FROM " + TABLE_COURSE_CATEGORY + ";";

        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<CourseCategory> courseCategories = new ArrayList<CourseCategory>();

        while(resultSet.next()){
            CourseCategory courseCategory = new CourseCategory();
            courseCategory.setCategoryId(resultSet.getLong(COL_COURSES_CATEGORY_ID));
            courseCategory.setCourseCategory(resultSet.getString(COL_CATEGORY));
            courseCategory.setCourseArabicCategory(resultSet.getString(COL_CATEGORY_ARABIC));
            courseCategories.add(courseCategory);
        }

        connection.close();
        return courseCategories;
    }

    public boolean emailExists(String email) throws SQLException {
        String query = "Select " + COL_USERS_EMAIL + " FROM " + TABLE_USERS + " WHERE " + COL_USERS_EMAIL + " = ?";
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,email);
        ResultSet resultSet = preparedStatement.executeQuery();
        connection.close();
        return resultSet.next();
    }

    public void createTwitterUser(NewUser user) throws SQLException {
        String query = "INSERT INTO " + TABLE_USERS +
                "("+ COL_USERS_EMAIL +", " + COL_USERS_NAME + ", " + COL_USERS_PHONE + ", " +
                COL_USERS_AUTHENTICATION_TYPE + ") " +
                "VALUES (?, ?, ?, ?);";

        Connection connection = getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, user.getEmail());
        preparedStatement.setString(2, user.getName());
        preparedStatement.setString(3, user.getPhone());
        preparedStatement.setString(4, Validation.AUTHENTICATION_TYPE_TWITTER);

        preparedStatement.execute();

        connection.close();
    }
}
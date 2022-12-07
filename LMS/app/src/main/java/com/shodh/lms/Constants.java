package com.shodh.lms;

public class Constants {

    public static final String HOST = "http://192.168.43.202:8000/api";
    public static final String IMAGE_BASE_PATH = "http://192.168.43.202:8000/storage/upload/profile_pic/";
    public static final String FILE_BASE_PATH = "http://192.168.43.202:8000/storage/upload/ebooks/";
    public static final String STUDENT_LOGIN = HOST+"/student/login";
    public static final String STUDENT_LOGOUT = HOST+"/student/logout";
    public static final String STUDENT_PROFILE_UPDATE = HOST+"/student/edit-profile";
    public static final String GET_DASHBOARD_COUNT = HOST+"/student/dashboard";
    public static final String GET_NEW_NOTIFICATION = HOST+"/student/new-notifications";
    public static final String GET_ALL_NOTIFICATION = HOST+"/student/all-notifications";
    public static final String MARK_ALL_NOTIFICATION = HOST+"/student/notifications/mark-all";
    public static final String MARK_SINGLE_NOTIFICATION = HOST+"/student/notifications/mark/";
    public static final String CLEAR_ALL_NOTIFICATION = HOST+"/student/notifications/delete-all";

    public static final String GET_ALL_BOOK = HOST+"/student/books";
    public static final String GET_SINGLE_BOOK = HOST+"/student/books/view/";

    public static final String GET_ALL_EBOOK = HOST+"/student/ebooks";
    public static final String GET_SINGLE_EBOOK = HOST+"/student/ebooks/view/";

    public static final String NOTIFY_ME = HOST+"/student/books/notify/";

}

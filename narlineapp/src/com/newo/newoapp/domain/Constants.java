package com.newo.newoapp.domain;

import android.R.string;

public class Constants {

	// These codes match the ones defined in TargetFinder.h for Cloud Reco
	// service
	public final static class CloudReco {
		public static final int INIT_SUCCESS = 2;
		public static final int INIT_ERROR_NO_NETWORK_CONNECTION = -1;
		public static final int INIT_ERROR_SERVICE_NOT_AVAILABLE = -2;
		public static final int UPDATE_ERROR_AUTHORIZATION_FAILED = -1;
		public static final int UPDATE_ERROR_PROJECT_SUSPENDED = -2;
		public static final int UPDATE_ERROR_NO_NETWORK_CONNECTION = -3;
		public static final int UPDATE_ERROR_SERVICE_NOT_AVAILABLE = -4;
		public static final int UPDATE_ERROR_BAD_FRAME_QUALITY = -5;
		public static final int UPDATE_ERROR_UPDATE_SDK = -6;
		public static final int UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE = -7;
		public static final int UPDATE_ERROR_REQUEST_TIMEOUT = -8;
	}

	// Menu item string constants:
	public final static class MenuItem {
		public static final String MENU_ITEM_ACTIVATE_CONT_AUTO_FOCUS = "Activate Cont. Auto Focus";
		public static final String MENU_ITEM_DEACTIVATE_CONT_AUTO_FOCUS = "Deactivate Cont. Auto Focus";
		public static final String MENU_ITEM_TRIGGER_AUTO_FOCUS = "Trigger Auto Focus";
	}

	// Focus mode constants:
	public final static class FocusMode {
		public static final int FOCUS_MODE_NORMAL = 0;
		public static final int FOCUS_MODE_CONTINUOUS_AUTO = 1;
	}

	// Application status constants:
	public final static class AppStatus {
		public static final int APPSTATUS_UNINITED = -1;
		public static final int APPSTATUS_INIT_APP = 0;
		public static final int APPSTATUS_INIT_QCAR = 1;
		public static final int APPSTATUS_INIT_TRACKER = 2;
		public static final int APPSTATUS_INIT_APP_AR = 3;
		public static final int APPSTATUS_LOAD_TRACKER = 4;
		public static final int APPSTATUS_INIT_CLOUDRECO = 5; // status for
																// CloudReco
		public static final int APPSTATUS_INITED = 6;
		public static final int APPSTATUS_CAMERA_STOPPED = 7;
		public static final int APPSTATUS_CAMERA_RUNNING = 8;
	}

	// Name of the native dynamic libraries to load:
	public final static class NativeLibNames {
		public static final String NATIVE_LIB_SAMPLE = "VideoPlayback";
		public static final String NATIVE_LIB_QCAR = "Vuforia";
	}

	public static final class PrefNames {
		public static final String APP_STATE = "appState";
		public static final String USER_INFO = "userInfo";
		public static final String SOCIAL_STATUS = "socialStatus";
		public static final String NOTIFICATION= "notification";
	}

	public static final class PrefKeys {
		public static final String IS_FIRST_TIME_OPEN = "isFirstTimeOpen";
		public static final String IS_SCANING = "isScaning";

		public static final String USER_ID = "userId";
		public static final String USER_NAME = "userName";
		public static final String EMAIL_ADDRESS = "emailAddress";
		public static final String PHONE_NUMBER = "phoneNumber";
		public static final String PASSWORD = "password";
		public static final String FIRST_NAME = "firstName";
		public static final String LAST_NAME = "lastName";
		public static final String GENDER = "gender";
		public static final String DATE_OF_BIRTH = "birthday";
		public static final String USER_TOTAL_POINTS = "userTotalPoints";
		public static final String USER_CURRENT_POINTS = "userCurrentPoints";

		public static final String SOCIAL_BUTTON_SCAN_SHARE = "socialButtonScanShare";
		public static final String NOTIFICATION_BUTTON_STATUS = "notificationButtonStatus";
	}

	public static final class AugmentedButtonCodes {
		public static final int WEBSITE = 1;
		public static final int FACEBOOK = 2;
		public static final int TWITTER = 3;
	}

	public final static class UserRegistration {
		public static final int USER_SUCCESSFULLY_REGISTERED = 1;
		public static final int USERNAME_EXIST_ERROR = 2;
		public static final int EMAIL_EXIST_ERROR = 3;
	}

	public final static class UserLogin {
		public static final int USER_SUCCESSFULLY_LOGIN = 1;
		public static final int USERNAME_NOT_EXIST_ERROR = 2;
		public static final int PASSWORD_INCORRECT = 3;
	}

	public final static class UserPasswordChange {
		public static final int USER_PASSWORD_CHANGED = 1;
		public static final int USER_PASSWORD_INCORRECT = 2;
		public static final int USER_PASSWORD_ERROR = 3;
	}

	public final static class UserProfileDataChange {
		public static final int USER_PROFILE_DATA_CHANGED = 1;
		public static final int USER_PROFILE_DATA_ERROR = 2;
	}

	public final static class UserForgotPassword {
		public static final int PASSWORD_SUCCESSFULLY_SENDED = 1;
		public static final int EMAIL_ADDRESS_INCORRECT = 2;
	}

	public static final class fileNames {
		public static final String PROFILE_PHOTO = "profile_photo.jpg";
		public static final String MAIN_FOLDER = "/sdcard/NeWo/";
	}

	public static final class targetLikeOperation {
		public static final int LIKE = 1;
		public static final int DISLIKE = 0;
	}

	public static final class ConstStrings {
		public static final String serverPath = "http://vmnewoapp.cloudapp.net/NarLineMobileApp/";
	}

}

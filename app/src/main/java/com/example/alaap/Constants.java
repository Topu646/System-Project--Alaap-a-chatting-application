package com.example.alaap;

import java.util.HashMap;

public class Constants {
    public final static String KEY_IMAGE = "image";
    public final static String KEY_NAME = "name";
    public final static String KEY_PREFERENCE_NAME = "chatAppPreference";
    public final static String KEY_EMAIL = "email";
    public final static String KEY_FCM_TOKEN = "fcmToken";
    public final static String KEY_COLLECTION_USERS = "users";
    public final static String KEY_USER_ID = "userId";
    public final static String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static HashMap<String,String>remoteMsgHeaders = null;
    public static HashMap<String ,String>getRemoteMsgHeaders()
    {
        if (remoteMsgHeaders == null)
        {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAQYuCFdU:APA91bEfV7Jruyei_7gjR-_FfHnSej6SPDVcMDkLR_X6un7GKJodXctJnkk5vIU8UEc5FDBrrihdk9wn_0kuojnaLcxlFDdCSL1ONJcydKvwZExrDevwfJUC2UKrsNNGlOKRd9AnN4RM"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }
}

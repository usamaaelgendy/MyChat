package com.usama.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.usama.mychat.Models.NotificationModel;
import com.usama.mychat.Models.StaticInfo;
import com.usama.mychat.Models.User;
import com.usama.mychat.Services.LocalUserService;
import com.usama.mychat.Services.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityNotifications extends AppCompatActivity {
    ListView lv_NotificationList;
    User user;
    List<NotificationModel> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Firebase.setAndroidContext(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        lv_NotificationList = findViewById(R.id.lv_NoticicationList);
        notificationList = new ArrayList<>();
        user = LocalUserService.getLocalUserFromPreferences(this);
        Firebase reqRef = new Firebase(StaticInfo.EndPoint + "/friendrequests/" + user.Email);
        reqRef.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Map map = dataSnapshot.getValue(Map.class);
                        String firstName = map.get("FirstName").toString();
                        String lastName = map.get("LastName").toString();
                        final String key = dataSnapshot.getKey();
                        NotificationModel not = new NotificationModel();
                        not.FirstName = firstName;
                        not.LastName = lastName;
                        not.NotificationType = 1; // friend request
                        notificationList.add(not);
                        not.EmailFrom = key;
                        not.FriendRequestFireBaseKey = dataSnapshot.getKey();
                        not.NotificationMessage = Tools.toProperName(firstName) + " " + Tools.toProperName(lastName);
                        ListAdapter adp = new NotficationListAdapter(ActivityNotifications.this, notificationList);
                        lv_NotificationList.setAdapter(adp);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        String friendEmail = dataSnapshot.getKey();
                        int index = -1;
                        for (int i = 0; i < notificationList.size(); i++) {
                            NotificationModel item = notificationList.get(i);
                            if (item.EmailFrom.equals(friendEmail))
                                index = i;
                        }
                        notificationList.remove(index);
                        ListAdapter adp = new NotficationListAdapter(ActivityNotifications.this, notificationList);
                        lv_NotificationList.setAdapter(adp);

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );


    }

}

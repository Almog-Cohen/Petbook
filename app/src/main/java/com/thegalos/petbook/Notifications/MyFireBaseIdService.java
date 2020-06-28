package com.thegalos.petbook.Notifications;

        import android.app.Activity;
        import android.util.Log;

        import androidx.annotation.NonNull;

        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.iid.FirebaseInstanceId;
        import com.google.firebase.iid.InstanceIdResult;
        import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFireBaseIdService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        if (firebaseUser!=null){

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener((Activity) getApplicationContext(), new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String refreshToken = instanceIdResult.getToken();
                    updateToken(refreshToken);
                }
            });


        }


    }

    private void updateToken(String refreshToken){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(refreshToken);
        reference.child(firebaseUser.getUid()).setValue(token);

    }
}

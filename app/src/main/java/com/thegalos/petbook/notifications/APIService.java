package com.thegalos.petbook.notifications;

import com.thegalos.petbook.objects.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAS80yvbM:APA91bHBBiZXLoWdefF3al29zCS1fqNP6tpV5DGH89ZihkLgUDRYpMSDoZXF_K26LLQPVplEFwZtLf7z8yhP_qAmLOlPrXZO7myHrNVX2Pwkg7AlbU17PE1hgHbd0ofFqeBfPfvZGpQQ"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}

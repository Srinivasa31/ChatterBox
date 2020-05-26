package Fragments;

import Notifications.MyResponse;
import Notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA3oj_JMk:APA91bHRHrZ7RJTKvBq9JJmtA-rv6IyLM21eLyp3P6nH-GNbJ8w7syutQgNJbKg-ns3gICs0w2B1WF6p3kI0gDuXqyDMHm937oAcTNFt0tlDdjAy8bHwMRjEz4jBNREZcMXuDTgHMzMZ"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}

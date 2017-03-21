package ru.user32338.fbandvkauth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfoActivity extends AppCompatActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, UserInfoActivity.class);
    }

    private TextView body;
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        body = (TextView) findViewById(R.id.body);
        avatar = (ImageView) findViewById(R.id.avatar);

        if (AccessToken.getCurrentAccessToken() != null) {

            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                            if (object != null) {
                                Log.d("tag", "json = " + object + "\n\n а далее уже https://developers.facebook.com/docs/graph-api/using-graph-api"  );

                                try {
                                    body.setText(object.toString());

                                    if (!object.isNull("picture")) {

                                        JSONObject picture = object.getJSONObject("picture");
                                        picture = picture.getJSONObject("data");

                                        Picasso.with(UserInfoActivity.this)
                                                .load(picture.getString("url"))
                                                .into(avatar);
                                    }

                                    toolbar.setTitle(object.getString("name"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }


                        }
                    });
            Bundle parameters = new Bundle();
            // параметры можно найти уже в самом api facenbook
            // https://developers.facebook.com/docs/graph-api/reference/user/
            parameters.putString("fields", "id,name,link,picture,email,birthday,devices,gender");
            request.setParameters(parameters);
            request.executeAsync();

        } else if (VKAccessToken.currentToken() != null) {
            // параметры можно брать воот отсюда https://vk.com/dev/users.get
            final VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS,
                    "id,first_name,last_name,sex,bdate,city,country,photo_50,photo_100," +
                            "photo_200_orig,photo_200,photo_400_orig,photo_max,photo_max_orig,online," +
                            "online_mobile,lists,domain,has_mobile,contacts,connections,site,education," +
                            "universities,schools,can_post,can_see_all_posts,can_see_audio,can_write_private_message," +
                            "status,last_seen,common_count,relation,relatives,counters"));
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {

                    Log.d("tag", response.responseString);
                    body.setText(response.responseString);

                    try {
                        JSONObject userInfo = response.json.getJSONArray("response").getJSONObject(0);
                        if (!TextUtils.isEmpty(userInfo.getString("photo_200_orig"))){

                            Picasso.with(UserInfoActivity.this)
                                    .load(userInfo.getString("photo_200_orig"))
                                    .into(avatar);


                            toolbar.setTitle(userInfo.getString("first_name") + " " + userInfo.getString("last_name"));

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }



                }
            });

        } else {
            finish();
        }

    }

    @Override
    public void onBackPressed() {

        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        } else if (VKAccessToken.currentToken() != null) {
            VKSdk.logout();
        }

        super.onBackPressed();
    }
}

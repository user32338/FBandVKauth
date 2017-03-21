package ru.user32338.fbandvkauth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.util.VKUtil;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;

    private Button customFButton;
    private Button customVKButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Facebook
         */
        callbackManager = CallbackManager.Factory.create();

        customFButton = (Button) findViewById(R.id.customFButton);

        if (AccessToken.getCurrentAccessToken() != null) {
            customFButton.setText("Выход");
            Toast.makeText(this, "мы уже входили ранее в FB", Toast.LENGTH_LONG).show();
            startActivity(UserInfoActivity.getIntent(MainActivity.this));
        }

        customFButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AccessToken.getCurrentAccessToken() != null){
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Да ты куда пошоль да!")
                            .setPositiveButton("ну и иди!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    customFButton.setText("он ушель");
                                    LoginManager.getInstance().logOut();
                                }
                            })
                            .setNegativeButton("вот молодец!", null)
                            .show();
                }else {
                    LoginManager.getInstance().logInWithReadPermissions(MainActivity.this,
                            Arrays.asList("public_profile"));
                }
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        customFButton.setText("Выход");
                        startActivity(UserInfoActivity.getIntent(MainActivity.this));
                        Toast.makeText(MainActivity.this, "мы вошли " + loginResult.getAccessToken(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "а мы и не входили", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this, "мы НЕ вошли " + error, Toast.LENGTH_LONG).show();
                    }
                });


        /**
         * VK
         */

        customVKButton = (Button) findViewById(R.id.customVKButton);

        //VKUtil.getCertificateFingerprint(this, getPackageName());

        if (VKAccessToken.currentToken() != null){
            customVKButton.setText("Выход");
            Toast.makeText(this, "мы уже входили ранее в VK", Toast.LENGTH_LONG).show();
            startActivity(UserInfoActivity.getIntent(MainActivity.this));
        }


        customVKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VKAccessToken.currentToken() != null){
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Да ты куда пошоль да!")
                            .setPositiveButton("ну и иди!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    customVKButton.setText("он ушель");
                                    VKSdk.logout();
                                }
                            })
                            .setNegativeButton("вот молодец!", null)
                            .show();
                }else {
                    VKSdk.login(MainActivity.this, "");
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AccessToken.getCurrentAccessToken() == null){
            customFButton.setText("Вход через FB");
        }

        if (VKAccessToken.currentToken() == null){
            customVKButton.setText("Вход через VK");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                startActivity(UserInfoActivity.getIntent(MainActivity.this));
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(MainActivity.this, "что-то пошло не так!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

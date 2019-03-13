package com.example.mxc_wallet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.wxample.mxcwallet.AuthGrpc;
import io.grpc.wxample.mxcwallet.LoginReply;
import io.grpc.wxample.mxcwallet.LoginRequest;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    public static final int FUNC_SINGUP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        //首先抓到 帳號用email當帳號
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        //監聽打了甚麼東西
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                //如果登入資訊正確
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//            startActivities(new Intent[]{intent});
                startActivityForResult(intent, FUNC_SINGUP);
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FUNC_SINGUP){
            if (resultCode == RESULT_OK){
                //logon = true;
                String email = data.getStringExtra("SIGNUP_EMAIL");
                String password = data.getStringExtra("SIGNUP_PWD");
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);
                //Log.d("User name is: ", name);
            }else {
                finish();
            }
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        //小於6.0版本就不會問了
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        //如果是讀取聯絡人資料他會問你說要不要讀取
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            // TODO: alert the user with a Snackbar/AlertDialog giving them the permission rationale
            // To use the Snackbar from the design support library, ensure that the activity extends
            // AppCompatActivity and uses the Theme.AppCompat theme.
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     * 用來如果可使用者允許讀取就可以使用自動完成功能
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     * 登入時的判斷方法
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors 用來重製錯誤.
        //设置输入框的错误提示为空
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt 儲存值在登入的時候.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one 判斷使用著的密碼是空值或是有問題或是無效的.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address 確定帳號是有效的.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        /*if (email.equals("123@") && password.equals("12345")){
            getIntent().putExtra("LOGIN_EMAIL", email);
            setResult(RESULT_OK, getIntent());
//            Toast.makeText(this, "Login Success!", Toast.LENGTH_LONG).show();
//            finish();
        }else {
            new AlertDialog.Builder(LoginActivity.this).setTitle("Error")
                    .setMessage("Wrong Email or Password!")
                    .setPositiveButton("OK", null)
                    .show();
            mPasswordView.setText("");
            return;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            //如果格式错误，输入框重新获得输入焦点

            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //如果输入的格式正确，显示验证等待对话框，并启动验证线程
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    /**
     * Shows the progress UI and hides the login form 動畫特效.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //這邊是用來讀取聯絡人資料, 基本上就是去讀取去查詢聯絡人資料然後自動完成的功能
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
    //這邊是用來讀取聯絡人資料, 基本上就是去讀取去查詢聯絡人資料然後自動完成的功能

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     * 異步存取了 AsynsTask的應用
     * 這個ASYNSTASK絕對不能  doinBackground 和UI有任何互動
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;
        private ManagedChannel mChannel;
        private String cusName;
        private String cusETHadd;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            mChannel = ManagedChannelBuilder.forAddress("45.76.94.136", 8089).usePlaintext(true).build();
            AuthGrpc.AuthBlockingStub blockingStub = AuthGrpc.newBlockingStub(mChannel);
//            AuthGrpc.AuthStub asyncStub = AuthGrpc.newStub(mChannel);
//            AuthGrpc.AuthFutureStub futureStub = AuthGrpc.newFutureStub(mChannel);

            LoginRequest loginRequest = LoginRequest.newBuilder()
                    .setEmail(mEmail)
                    .setPassword(mPassword)
                    .build();

            /*ListenableFuture<LoginReply> future = futureStub.login(loginRequest);
            Futures.addCallback(future, new FutureCallback<LoginReply>() {

                @Override
                public void onSuccess(LoginReply result) {}

                @Override
                public void onFailure(Throwable t) {}

            });*/

            /*asyncStub.login(loginRequest, new StreamObserver<LoginReply>() {

                @Override
                public void onNext(LoginReply value) {}

                @Override
                public void onError(Throwable t) {}

                @Override
                public void onCompleted() {}

            });*/

            //LoginReply loginReply;
            try {
                LoginReply loginReply = blockingStub.login(loginRequest);
                if (loginReply.getReply().equals("0")) {
                    String[] reply = loginReply.getMessage().split(":");
                    cusETHadd = reply[0];
                    cusName = reply[1];
                    return true;
                } else if (loginReply.getReply().equals("2")) {
                    /*
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                    startActivityForResult(intent, FUNC_SINGUP);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("You haven't registered, do you want to join MXC?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                    */
                    return false;
                } else {
                    return false;
                }

            } catch (Exception e) {
                System.out.println("This is err: " + e);
                throw e;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            try {
                mChannel.shutdown().awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (success) {
                Toast.makeText(LoginActivity.this, "MXC - your best choice!", Toast.LENGTH_LONG).show();
                //finish();
                Intent intent = new Intent();//数据是使用Intent返回
                intent.putExtra("LOGIN_NAME",cusName);//把返回数据存入Intent
                intent.putExtra("LOGIN_ETH", cusETHadd);
                LoginActivity.this.setResult(RESULT_OK, intent);
                LoginActivity.this.finish();
            } else {
                //Toast.makeText(LoginActivity.this, "This email has registered already!", Toast.LENGTH_LONG).show();
                //System.out.println("Nooooooooo!");
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
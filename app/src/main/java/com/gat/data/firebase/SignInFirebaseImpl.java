package com.gat.data.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.gat.data.user.EmailLoginData;
import com.gat.data.user.SocialLoginData;
import com.gat.domain.SchedulerFactory;
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.LoginData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.Executor;

import dagger.Lazy;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 4/1/17.
 */

public class SignInFirebaseImpl implements SignInFirebase {
    private static final String TAG = SignInFirebaseImpl.class.getSimpleName();
    private static final int TYPE_LOGIN = 1;
    private static final int TYPE_REGISTER = 0;

    private final Lazy<UserDataSource> localUserDataSourceLazy;
    private final Lazy<UserDataSource> networkUserDataSourceLazy;
    private final FirebaseService firebaseService;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private final SchedulerFactory scheduler;

    private Subject<Boolean> result;
    private Subject<Integer> loginSubject;
    private FirebaseAuth.AuthStateListener authStateListener;
    private CompositeDisposable disposable;

    public SignInFirebaseImpl(Lazy<UserDataSource> localUserDataSourceLazy, Lazy<UserDataSource> networkUserDataSourceLazy, FirebaseService firebaseService, SchedulerFactory scheduler) {
        this.localUserDataSourceLazy = localUserDataSourceLazy;
        this.networkUserDataSourceLazy = networkUserDataSourceLazy;
        this.scheduler = scheduler;
        this.firebaseService = firebaseService;

        firebaseAuth = FirebaseAuth.getInstance();
        result = PublishSubject.create();
        loginSubject = BehaviorSubject.create();

        authStateListener = firebaseAuth12 -> {
            Log.d(TAG, "StateChanged");
            firebaseUser = firebaseAuth12.getCurrentUser();
            if (firebaseUser != null) {
                result.onNext(true);
                String fireBaseToken = FirebaseInstanceId.getInstance().getToken();
                Log.d("FireBaseToken:", fireBaseToken);
            } else {
                result.onNext(false);
            }
        };

        firebaseAuth.addAuthStateListener(authStateListener);

        disposable = new CompositeDisposable(
                loginSubject.subscribeOn(scheduler.main()).subscribe(type -> {
                    if (type == TYPE_LOGIN) {
                        localUserDataSourceLazy.get().loadLoginData().subscribe(loginData -> {
                            if (loginData.type() == LoginData.Type.EMAIL)
                                loginWithEmail(loginData);
                            else if (loginData.type() == LoginData.Type.FACE)
                                loginWithFacebook(loginData);
                            else if (loginData.type() == LoginData.Type.TWITTER)
                                loginWithTwitter(loginData);
                            else if (loginData.type() == LoginData.Type.GOOGLE)
                                loginWithGoogle(loginData);
                            else
                                return;
                        });
                    }  else {
                        localUserDataSourceLazy.get().loadLoginData().subscribe(loginData -> {
                            if (loginData.type() == LoginData.Type.EMAIL)
                                registerWithEmail(loginData);
                            else
                                login();
                        });
                    }
                }),
                result.observeOn(scheduler.io()).subscribe(isLogged -> {
                    Log.d(TAG, "IsLogged:" + isLogged);
                    if (isLogged) {
                        firebaseService.Init();
                        String fireBaseToken = FirebaseInstanceId.getInstance().getToken();
                        networkUserDataSourceLazy.get().registerFirebaseToken(fireBaseToken).subscribeOn(scheduler.io()).subscribe(result -> {
                            Log.d(TAG, "RegisterFirebaseToken:" + result);
                        }, throwable -> {
                            throwable.printStackTrace();
                        });
                    }
                })
        );
    }

    private void loginWithFacebook(LoginData loginData) {
        String token = ((SocialLoginData)loginData).token();
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            firebaseUser = task.getResult().getUser();
            result.onNext(firebaseUser != null);
        });
    }

    private void loginWithTwitter(LoginData loginData) {
        String token = ((SocialLoginData)loginData).token();
        String secret = ((SocialLoginData)loginData).secret();
        AuthCredential credential = TwitterAuthProvider.getCredential(token, secret);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            firebaseUser = task.getResult().getUser();
            result.onNext(firebaseUser != null);
        });
    }

    private void loginWithGoogle(LoginData loginData) {
        String token = ((SocialLoginData)loginData).token();
        AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            firebaseUser = task.getResult().getUser();
            result.onNext(firebaseUser != null);
        });
    }

    private void loginWithEmail(LoginData loginData) {
        String email = ((EmailLoginData)loginData).email();
        String password = ((EmailLoginData)loginData).password();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            firebaseUser = task.getResult().getUser();
            result.onNext(firebaseUser != null);
        });
    }

    private void registerWithEmail(LoginData loginData) {
        String email = ((EmailLoginData)loginData).email();
        String password = ((EmailLoginData)loginData).password();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                firebaseUser = task.getResult().getUser();
                result.onNext(firebaseUser != null);
        });
    }

    @Override
    public void login() {
        loginSubject.onNext(TYPE_LOGIN);
    }

    @Override
    public void signOut() {

    }

    @Override
    public void register() {
        loginSubject.onNext(TYPE_REGISTER);
    }

    @Override
    public Observable<Boolean> getLoginResult() {
        return result.observeOn(scheduler.io());
    }

    @Override
    public void destroy() {
        firebaseAuth.removeAuthStateListener(authStateListener);
        disposable.dispose();
    }
}

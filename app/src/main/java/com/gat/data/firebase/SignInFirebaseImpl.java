package com.gat.data.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.gat.data.user.EmailLoginData;
import com.gat.data.user.SocialLoginData;
import com.gat.domain.SchedulerFactory;
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.LoginData;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private final Lazy<UserDataSource> userDataSourceLazy;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private final SchedulerFactory scheduler;

    private Subject<FirebaseUser> result;
    private Subject<Integer> loginSubject;
    private FirebaseAuth.AuthStateListener authStateListener;
    private CompositeDisposable disposable;

    public SignInFirebaseImpl(Lazy<UserDataSource> userDataSourceLazy, SchedulerFactory scheduler) {
        this.userDataSourceLazy = userDataSourceLazy;
        this.scheduler = scheduler;

        firebaseAuth = FirebaseAuth.getInstance();
        result = PublishSubject.create();
        loginSubject = BehaviorSubject.create();

        authStateListener = firebaseAuth12 -> {
            firebaseUser = firebaseAuth12.getCurrentUser();
            if (firebaseUser != null) {
                result.onNext(firebaseUser);
                String firebaseToken = FirebaseInstanceId.getInstance().getToken();
                Log.d("FirebaseToken:", firebaseToken);
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);

        disposable = new CompositeDisposable(
                loginSubject.subscribeOn(scheduler.main()).subscribe(type -> {
                    if (type == TYPE_LOGIN) {
                        userDataSourceLazy.get().loadLoginData().subscribe(loginData -> {
                            if (loginData.type() == LoginData.Type.EMAIL)
                                loginWithEmail(loginData);
                            else if (loginData.type() == LoginData.Type.FACE)
                                loginWithFacebook(loginData);
                            else
                                throw new UnsupportedOperationException();
                        });
                    }  else {
                        userDataSourceLazy.get().loadLoginData().subscribe(loginData -> {
                            if (loginData.type() == LoginData.Type.EMAIL)
                                registerWithEmail(loginData);
                            else
                                login();
                        });
                    }
                })
        );
    }

    private void loginWithFacebook(LoginData loginData) {
        String token = ((SocialLoginData)loginData).token();
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        firebaseAuth.signInWithCredential(credential);
    }

    private void loginWithEmail(LoginData loginData) {
        String email = ((EmailLoginData)loginData).email();
        String password = ((EmailLoginData)loginData).password();
        firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    private void registerWithEmail(LoginData loginData) {
        String email = ((EmailLoginData)loginData).email();
        String password = ((EmailLoginData)loginData).password();
        firebaseAuth.createUserWithEmailAndPassword(email, password);
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
    public Observable<FirebaseUser> getLoginResult() {
        return result.observeOn(scheduler.io());
    }

    @Override
    public void destroy() {
        firebaseAuth.removeAuthStateListener(authStateListener);
        disposable.dispose();
    }
}

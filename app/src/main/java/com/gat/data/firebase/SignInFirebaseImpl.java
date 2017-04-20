package com.gat.data.firebase;

import android.util.Log;

import com.gat.data.user.EmailLoginData;
import com.gat.data.user.SocialLoginData;
import com.gat.domain.SchedulerFactory;
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.LoginData;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

import dagger.Lazy;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 4/1/17.
 */

public class SignInFirebaseImpl implements SignInFirebase {
    private static final String TAG = SignInFirebaseImpl.class.getSimpleName();
    private final Lazy<UserDataSource> userDataSourceLazy;
    private FirebaseAuth firebaseAuth;
    private final SchedulerFactory scheduler;

    private Subject<Boolean> result;
    private Subject<Integer> loginSubject;

    public SignInFirebaseImpl(Lazy<UserDataSource> userDataSourceLazy, SchedulerFactory scheduler) {
        this.userDataSourceLazy = userDataSourceLazy;
        this.scheduler = scheduler;

        firebaseAuth = FirebaseAuth.getInstance();
        result = BehaviorSubject.create();
        loginSubject = BehaviorSubject.create();
    }

    private Observable<Boolean> loginWithFacebook(LoginData loginData) {
        String token = ((SocialLoginData)loginData).token();
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    result.onNext(task.isSuccessful());
                });
        return result.observeOn(scheduler.io());
    }

    private void loginWithEmail(LoginData loginData) {
        String email = ((EmailLoginData)loginData).email();
        String password = ((EmailLoginData)loginData).password();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    result.onNext(task.isSuccessful());
                });
    }

    private void registerWithEmail(LoginData loginData) {
        String email = ((EmailLoginData)loginData).email();
        String password = ((EmailLoginData)loginData).password();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    result.onNext(task.isSuccessful());
                });
    }

    @Override
    public void login() {
        userDataSourceLazy.get().loadLoginData().subscribe(loginData -> {
            if (loginData.type() == LoginData.Type.EMAIL)
                loginWithEmail(loginData);
            else if (loginData.type() == LoginData.Type.FACE)
                loginWithFacebook(loginData);
            else
                throw new UnsupportedOperationException();
        });
    }

    @Override
    public void signOut() {

    }

    @Override
    public void register() {
        userDataSourceLazy.get().loadLoginData().subscribe(loginData -> {
           if (loginData.type() == LoginData.Type.EMAIL)
               registerWithEmail(loginData);
            else
                login();
        });
    }

    @Override
    public Observable<Boolean> getLoginResult() {
        return result.observeOn(scheduler.io());
    }

    @Override
    public void destroy() {

    }
}

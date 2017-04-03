package com.gat.data.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.gat.data.user.EmailLoginData;
import com.gat.data.user.SocialLoginData;
import com.gat.domain.SchedulerFactory;
import com.gat.repository.entity.LoginData;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 4/1/17.
 */

public class SignInFirebaseImpl implements SignInFirebase {
    private static final String TAG = SignInFirebaseImpl.class.getSimpleName();
    private final LoginData loginData;
    private FirebaseAuth firebaseAuth;
    private final SchedulerFactory scheduler;

    Subject<Boolean> result;
    public SignInFirebaseImpl(LoginData loginData, SchedulerFactory scheduler) {
        this.loginData = loginData;
        this.scheduler = scheduler;

        firebaseAuth = FirebaseAuth.getInstance();
        result = BehaviorSubject.create();
    }

    private Observable<Boolean> loginWithFacebook() {
        String token = ((SocialLoginData)loginData).token();
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    result.onNext(task.isSuccessful());
                });
        return result.observeOn(scheduler.io());
    }

    private Observable<Boolean> loginWithEmail() {
        String email = ((EmailLoginData)loginData).email();
        String password = ((EmailLoginData)loginData).password();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    result.onNext(task.isSuccessful());
                });
        return result.observeOn(scheduler.io());
    }

    private Observable<Boolean> registerWithEmail() {
        String email = ((EmailLoginData)loginData).email();
        String password = ((EmailLoginData)loginData).password();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    result.onNext(task.isSuccessful());
                });
        return result.observeOn(scheduler.io());
    }

    @Override
    public Observable<Boolean> login() {
        if (loginData.type() == LoginData.Type.FACE)
            return loginWithFacebook();
        else if (loginData.type() == LoginData.Type.EMAIL)
            return loginWithEmail();
        else
            throw new UnsupportedOperationException();
    }

    @Override
    public Observable<Boolean> signOut() {
        return null;
    }

    @Override
    public Observable<Boolean> register() {
        if (loginData.type() == LoginData.Type.EMAIL)
            return registerWithEmail();
        else
            return login();
    }

    @Override
    public void destroy() {

    }
}

package com.gat.data.firebase;

import android.util.Log;

import com.gat.common.util.Strings;
import com.gat.data.exception.LoginException;
import com.gat.data.response.ServerResponse;
import com.gat.data.user.EmailLoginData;
import com.gat.data.user.SocialLoginData;
import com.gat.domain.SchedulerFactory;
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.LoginData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

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
    private OnCompleteListener<AuthResult> onLoginCompleteListener;
    private CompositeDisposable disposable;

    private Subject<Boolean> linkCredentialResult;
    private OnCompleteListener<AuthResult> onLinkCompleteListener;
    private Subject<LoginData> linkSubject;
    private Subject<Integer> unlinkSubject;

    public SignInFirebaseImpl(Lazy<UserDataSource> localUserDataSourceLazy, Lazy<UserDataSource> networkUserDataSourceLazy, FirebaseService firebaseService, SchedulerFactory scheduler) {
        this.localUserDataSourceLazy = localUserDataSourceLazy;
        this.networkUserDataSourceLazy = networkUserDataSourceLazy;
        this.scheduler = scheduler;
        this.firebaseService = firebaseService;

        firebaseAuth = FirebaseAuth.getInstance();
        result = PublishSubject.create();
        loginSubject = PublishSubject.create();

        linkCredentialResult = PublishSubject.create();
        linkSubject = BehaviorSubject.create();
        unlinkSubject = BehaviorSubject.create();

        authStateListener = firebaseAuth12 -> {
            Log.d(TAG, "StateChanged");
            firebaseUser = firebaseAuth12.getCurrentUser();
            if (firebaseUser != null && localUserDataSourceLazy.get().loadUser().blockingFirst().isValid()) {
                result.onNext(true);
                String fireBaseToken = FirebaseInstanceId.getInstance().getToken();
                Log.d("FireBaseToken:", fireBaseToken);
            } else {
                result.onNext(false);
            }
        };

        firebaseAuth.addAuthStateListener(authStateListener);

        onLoginCompleteListener = task -> {
            if (task.isSuccessful() && task.isComplete()) {
                firebaseUser = task.getResult().getUser();
                result.onNext(true);
            } else {
                Log.d(TAG, task.getException().getMessage());
                result.onNext(false);
            }

        };

        onLinkCompleteListener = task -> {
            if (task.isSuccessful() && task.isComplete()) {
                firebaseUser = task.getResult().getUser();
                linkCredentialResult.onNext(true);
            } else {
                Log.d(TAG, task.getException().getMessage());
                linkCredentialResult.onNext(false);
            }
        };

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
                result.distinctUntilChanged().observeOn(scheduler.io()).subscribe(isLogged -> {
                    Log.d(TAG, "IsLogged:" + isLogged);
                    if (isLogged) {
                        firebaseService.Init();
                        String fireBaseToken = FirebaseInstanceId.getInstance().getToken();
                        networkUserDataSourceLazy.get().registerFirebaseToken(fireBaseToken).subscribeOn(scheduler.io()).subscribe(result -> {
                            Log.d(TAG, "RegisterFirebaseToken:" + result);
                        }, throwable -> {
                            throwable.printStackTrace();
                        });
                    } else {
                        firebaseAuth.signOut();
                        firebaseService.destroy();
                    }
                }),
                linkSubject.observeOn(scheduler.io()).delay(100, TimeUnit.MILLISECONDS).subscribe(loginData -> linkCredential(loginData)),
                unlinkSubject.observeOn(scheduler.io()).delay(100, TimeUnit.MILLISECONDS).subscribe(type -> unlinkWithCredential(type))
        );
    }

    private void loginWithFacebook(LoginData loginData) {
        String token = ((SocialLoginData)loginData).token();
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(onLoginCompleteListener);
    }

    private void loginWithTwitter(LoginData loginData) {
        String token = ((SocialLoginData)loginData).token();
        String secret = ((SocialLoginData)loginData).secret();
        AuthCredential credential = TwitterAuthProvider.getCredential(token, secret);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(onLoginCompleteListener);
    }

    private void loginWithGoogle(LoginData loginData) {
        String token = ((SocialLoginData)loginData).token();
        AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(onLoginCompleteListener);
    }

    private void loginWithEmail(LoginData loginData) {
        String email = ((EmailLoginData)loginData).email();
        String password = ((EmailLoginData)loginData).firebasePassword();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(onLoginCompleteListener);
    }

    private void registerWithEmail(LoginData loginData) {
        String email = ((EmailLoginData)loginData).email();
        String password = ((EmailLoginData)loginData).firebasePassword();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(onLoginCompleteListener);
    }

    @Override
    public void login() {
        loginSubject.onNext(TYPE_LOGIN);
    }

    @Override
    public void signOut() {
        firebaseAuth.signOut();
        firebaseService.destroy();
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
    public Observable<Boolean> linkWithCredential(LoginData loginData) {
        linkSubject.onNext(loginData);
        return linkCredentialResult.observeOn(scheduler.io());
    }

    private void linkCredential(LoginData loginData) {
        AuthCredential authCredential;
        switch (loginData.type()) {
            case LoginData.Type.EMAIL:
                EmailLoginData emailLoginData = (EmailLoginData) loginData;
                authCredential = EmailAuthProvider.getCredential(emailLoginData.email(), emailLoginData.password());
                break;
            case LoginData.Type.FACE:
            {
                SocialLoginData socialLoginData = (SocialLoginData) loginData;
                authCredential = FacebookAuthProvider.getCredential(socialLoginData.token());
            }
            break;
            case LoginData.Type.TWITTER:
            {
                SocialLoginData socialLoginData = (SocialLoginData) loginData;
                authCredential = TwitterAuthProvider.getCredential(socialLoginData.token(), socialLoginData.secret());
            }
            break;
            case LoginData.Type.GOOGLE:
            {
                SocialLoginData socialLoginData = (SocialLoginData) loginData;
                authCredential = GoogleAuthProvider.getCredential(socialLoginData.token(), null);
            }
            break;
            default:
                throw new UnsupportedOperationException();
        }
        if (firebaseUser != null) {
            firebaseUser.linkWithCredential(authCredential).addOnCompleteListener(onLinkCompleteListener);
        } else {
            throw new LoginException(ServerResponse.NO_LOGIN);
        }
    }

    private void unlinkCredential(int type) {
        String providerId;
        switch (type) {
            case LoginData.Type.EMAIL:
                providerId = EmailAuthProvider.PROVIDER_ID;
                break;
            case LoginData.Type.FACE:
                providerId = FacebookAuthProvider.PROVIDER_ID;
                break;
            case LoginData.Type.TWITTER:
                providerId = TwitterAuthProvider.PROVIDER_ID;
                break;
            case LoginData.Type.GOOGLE:
                providerId = GoogleAuthProvider.PROVIDER_ID;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        if (firebaseUser != null) {
            boolean hasFound = false;
            for (Iterator<String> iterator = firebaseUser.getProviders().iterator(); iterator.hasNext();) {
                if (providerId.equals(iterator.next())) {
                    firebaseUser.unlink(providerId).addOnCompleteListener(onLinkCompleteListener);
                    hasFound = true;
                    break;
                }
            }
            if (!hasFound) {
                linkCredentialResult.onNext(false);
            }
        } else {
            throw new LoginException(ServerResponse.NO_LOGIN);
        }
    }

    @Override
    public Observable<Boolean> unlinkWithCredential(int type) {
        unlinkSubject.onNext(type);
        return linkCredentialResult.observeOn(scheduler.io());
    }

    @Override
    public void destroy() {
        firebaseAuth.removeAuthStateListener(authStateListener);
        disposable.dispose();
    }
}

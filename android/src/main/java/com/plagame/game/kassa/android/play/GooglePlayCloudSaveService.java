package com.plagame.game.kassa.android.play;

import android.app.Activity;

import com.badlogic.gdx.Gdx;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.SnapshotsClient;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.platform.service.api.BaseCloudSaveService;
import com.plagame.game.kassa.platform.service.api.model.PlatformCallback;

import java.io.IOException;

/**
 * Created by Дмитрий Малышев on 23.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class GooglePlayCloudSaveService extends BaseCloudSaveService {

    private final Activity activity;
    private User cachedUser;

    public GooglePlayCloudSaveService(Activity activity) {
        this.activity = activity;
    }

    // =========================================================
    // INIT
    // =========================================================

    @Override
    public void init() {
        // ничего не нужно — Play Games сам управляет сессией
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @Override
    public void doLogin(PlatformCallback<String> callback) {
        PlayGames.getGamesSignInClient(activity)
            .signIn()
            .addOnCompleteListener(task -> {
                System.out.println("----------task.getResult().isAuthenticated() = " + task.getResult().isAuthenticated());
                if (task.isSuccessful() && task.getResult().isAuthenticated()) {
                    Gdx.app.postRunnable(() -> {
                        callback.onSuccess("login_success");
                    });
                } else {
                    Gdx.app.postRunnable(() -> {
                        callback.onError("Not Authenticated");
                    });
                }
            });
    }

    @Override
    public String getLogin() {
        return "";
    }

    // =========================================================
    // AUTH
    // =========================================================

    @Override
    public boolean isAuthorized() {
        try {
            return PlayGames.getGamesSignInClient(activity)
                .isAuthenticated()
                .getResult()
                .isAuthenticated();
        } catch (Exception e) {
            return false;
        }
    }

    // =========================================================
    // SAVE
    // =========================================================

    @Override
    public void saveUser(User user) {
        saveUserLocal(user);
        if (isAuthorized()) {
            saveUserToCloud(user);
        }
    }

    public void saveUserToCloud(User user) {

        if (!isAuthorized()) return;

        // 1. сразу обновляем локальную копию
        cachedUser = user;

        // 2. сериализация
        byte[] data = user.serialize();
//        String json = user.toJson();
//        byte[] data = json.getBytes(StandardCharsets.UTF_8);

        SnapshotsClient client =
            PlayGames.getSnapshotsClient(activity);

        client.open("savegame", true)
            .addOnSuccessListener(task -> {

                Snapshot snapshot = task.getData();

                snapshot.getSnapshotContents().writeBytes(data);

                SnapshotMetadataChange metadataChange =
                    new SnapshotMetadataChange.Builder().build();

                client.commitAndClose(snapshot, metadataChange);
            });
    }

    // =========================================================
    // LOAD
    // =========================================================

    @Override
    public void loadUser(PlatformCallback<User> callback) {

        // 1. если уже есть в памяти → мгновенный ответ
        if (cachedUser != null) {
            callback.onSuccess(cachedUser);
            return;
        }

        if (!isAuthorized()) {
            callback.onError("not_logged_in");
            return;
        }

        SnapshotsClient client =
            PlayGames.getSnapshotsClient(activity);

        client.open("savegame", false)
            .addOnSuccessListener(task -> {

                Snapshot snapshot = task.getData();

                byte[] data = null;
                try {
                    data = snapshot.getSnapshotContents().readFully();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                User user = User.deserialize(data);
//                String json = new String(data, StandardCharsets.UTF_8);
//                User user = User.fromJson(json);

                // 2. сохраняем в cache
                cachedUser = user;

                callback.onSuccess(user);
            })
            .addOnFailureListener(e ->
                callback.onError(e.getMessage())
            );
    }
}

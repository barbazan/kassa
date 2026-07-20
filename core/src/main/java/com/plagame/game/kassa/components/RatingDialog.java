package com.plagame.game.kassa.components;

import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_HEADER;
import static com.plagame.game.kassa.GameApplication.FONT_RATING;
import static com.plagame.game.kassa.GameConfig.LEADERBOARD_MAX_DOLLARS_NAME;
import static com.plagame.game.kassa.Resources.ATLAS_1;
import static com.plagame.game.kassa.screens.BaseScreen.PAD;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.integration.platform.service.api.model.LeaderboardEntry;
import com.plagame.game.integration.platform.service.api.model.PlatformCallback;
import com.plagame.game.integration.platform.service.api.model.TargetPlatform;
import com.plagame.game.kassa.utils.NumberFormat;
import com.plagame.game.kassa.utils.SoundUtil;

import java.util.List;

/**
 * Created by Дмитрий Малышев on 25.11.2025.
 * Email: dmitry.malyshev@gmail.com
 */
public class RatingDialog extends Table {

    private final Table headerTable;
    private final Table bodyTable;
    private float dialogWidth, dialogHeight, imageSize, pad;
    private Table scrolledTable;

    public RatingDialog() {
        super();
        this.dialogWidth = GameApplication.get().screenWidth * 0.96f;
        this.dialogHeight = Math.max(dialogWidth, GameApplication.get().screenHeight * 0.75f);
        this.imageSize = dialogWidth / 10;
        this.pad = imageSize / 5;
        setSize(dialogWidth, dialogHeight);
        setPosition((GameApplication.get().screenWidth - dialogWidth) / 2, (GameApplication.get().screenHeight - dialogHeight) / 2);
//        setDebug(true);

        headerTable = new Table();
//        headerTable.setDebug(true);
        headerTable.setWidth(dialogWidth);
        headerTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("dialog_header")));
        Image headerImage = new Image(ATLAS_1.findRegion("icon_trends"));
        headerImage.setSize(imageSize * 1.5f, imageSize * 1.5f);
        headerTable.add(headerImage).size(headerImage.getWidth(), headerImage.getHeight()).align(Align.left).pad(pad);

        Table t = new Table();
        Label headerLabel = new Label("РЕЙТИНГ", new Label.LabelStyle(FONT_DIALOG_HEADER, Color.GOLD));
        headerLabel.setAlignment(Align.center);
        t.add(headerLabel).align(Align.center).expandX().fill();
        headerTable.add(t).align(Align.center).expandX().fill();

        Image closeImage = new Image(ATLAS_1.findRegion("icon_close"));
        closeImage.addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SoundUtil.playClickSound();
                GameApplication.get().getGameScreen().uiStage.hideRatingDialog();
                GameApplication.get().getGameScreen().uiStage.refresh();
                GameApplication.get().getGameScreen().uiStage.checkFullscreenAdv();
            }
        });
        closeImage.setSize(imageSize, imageSize);
        headerTable.add(closeImage).size(closeImage.getWidth(), closeImage.getHeight()).align(Align.topRight).pad(pad);

        add(headerTable).align(Align.top).expandX().fill();
        row();

        bodyTable = new Table();
        bodyTable.setSize(dialogWidth, dialogHeight - headerTable.getHeight());
        bodyTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("dialog_body")));
        scrolledTable = new Table();
        scrolledTable.setSize(bodyTable.getWidth(), bodyTable.getHeight());

        ScrollPane scrollPane = new ScrollPane(scrolledTable);
        scrollPane.setScrollingDisabled(true, false);
        bodyTable.add(scrollPane).pad(pad / 2).padBottom(pad * 2).expand().fill();
        add(bodyTable).expandX().fill();

        if(GameApplication.get().platform.leaderboard().getLeaderboard().isEmpty()) {
            GameApplication.get().platform.leaderboard().loadLeaderboard(LEADERBOARD_MAX_DOLLARS_NAME, 100, 20, new PlatformCallback<List<LeaderboardEntry>>() {
                @Override
                public void onSuccess(List<LeaderboardEntry> value) {
                    addRatingTable(value);
                }

                @Override
                public void onError(String error) {
                    System.out.println("error = " + error);
                }
            });
        } else {
            addRatingTable(GameApplication.get().platform.leaderboard().getLeaderboard());
        }

    }

    public void addRatingTable(List<LeaderboardEntry> leaderboard) {
        for (int i = 0; i < leaderboard.size(); i++) {
            LeaderboardEntry leaderboardEntry = leaderboard.get(i);
            if(leaderboardEntry.login == null) {
                continue;
            }
            int rank = leaderboardEntry.rank;
            String playerName = leaderboardEntry.login;
            int maxLength = 14;
            if(playerName.length() > maxLength) {
                playerName = playerName.substring(0, maxLength) + "..." + playerName.substring(playerName.length() - 3);
            }

            Color nameColor = Color.WHITE;
            if(User.get().isAuthorized()) {
                String login = GameApplication.get().platform.cloud().getLogin();
                if(login != null && login.equals(playerName)) { // значит это я
                    nameColor = Color.GREEN;
                }
            }
            Label nickLabel = new Label(playerName, new Label.LabelStyle(FONT_RATING, nameColor));
            Table rowTable = new Table();
            rowTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("rating_row")));
            float rowHeight = nickLabel.getHeight() * 1.9f;
            rowTable.setSize(GameApplication.get().minScreenSize, rowHeight);

            rowTable.add(new Label(String.valueOf(rank), new Label.LabelStyle(FONT_RATING, nameColor))).align(Align.left).padLeft(PAD * 2);

//            boolean isUp = GameConfig.random.nextBoolean();
            boolean isUp = false; //todo rank
            String uid = String.valueOf(leaderboardEntry.id);
            Integer curRank = User.get().rankMap.get(uid);
            if(curRank == null || curRank >= leaderboardEntry.rank) {
                User.get().rankMap.put(uid, leaderboardEntry.rank);
                isUp = true;
            }
            Image triangleImage = new Image(isUp ? ATLAS_1.findRegion("triangle_green") : ATLAS_1.findRegion("triangle_red"));
            triangleImage.setSize(PAD * 1.8f, PAD * 1.8f);
            rowTable.add(triangleImage).size(PAD * 1.8f, PAD * 1.8f).align(Align.left).padLeft(PAD * 4);

            rowTable.add(nickLabel).align(Align.center).padLeft(PAD * 2).expandX().fill();
            if(GameConfig.TARGET_PLATFORM == TargetPlatform.HTML_YANDEX) {
                rowTable.add(new Label(leaderboardEntry.formattedScore, new Label.LabelStyle(FONT_RATING, nameColor))).padRight(PAD * 2).align(Align.right);
            } else {
                rowTable.add(new Label(NumberFormat.format(leaderboardEntry.value), new Label.LabelStyle(FONT_RATING, nameColor))).padRight(PAD * 2).align(Align.right);
            }

            scrolledTable.add(rowTable).height(rowHeight).align(Align.top).padBottom(PAD / 2).align(Align.top).expandX();
            scrolledTable.row();
        }
        scrolledTable.add().expand();
        scrolledTable.row();
    }

}

package com.plagame.game.kassa.serialize;

import com.plagame.game.kassa.beans.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Дмитрий Малышев on 17.05.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class ByteArrayUserSerializer {

    private static final int SAVE_VERSION = 2;

    public static byte[] serialize(User user) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);

            // VERSION
            out.writeInt(SAVE_VERSION);

            out.writeInt(user.id);
            out.writeUTF(user.secret);
            out.writeUTF(user.login);

            out.writeInt(user.location);

            out.writeInt(user.dollars);
            out.writeLong(user.lastSaveTime);
            out.writeLong(user.lastLoginTime);

            out.writeBoolean(user.soundOn);
            out.writeBoolean(user.musicOn);

            writeStringSet(out, user.purchasedProducts);

            out.writeLong(user.loginDayCount);
            out.writeBoolean(user.isAdHide);

            out.flush();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static User deserialize(byte[] data) {
        try {
            User user = new User();

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));

            int version = in.readInt();

            user.id = in.readInt();
            user.secret = in.readUTF();
            user.login = in.readUTF();

            user.location = in.readInt();

            user.dollars = in.readInt();

            user.lastSaveTime = in.readLong();
            user.lastLoginTime = in.readLong();

            user.soundOn = in.readBoolean();
            user.musicOn = in.readBoolean();

            user.purchasedProducts = readStringSet(in);

            user.loginDayCount = in.readLong();
            user.isAdHide = in.readBoolean();

            return user;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // =========================================================
    // MAP
    // =========================================================
    private static void writeIntMap(DataOutputStream out, Map<String, Integer> map) throws IOException {
        out.writeInt(map.size());
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeInt(entry.getValue());
        }
    }

    private static ConcurrentHashMap<String, Integer> readIntMap(DataInputStream in) throws IOException {
        int size = in.readInt();
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        for (int i = 0; i < size; i++) {
            String key = in.readUTF();
            int value = in.readInt();
            map.put(key, value);
        }
        return map;
    }

    private static void writeLongMap(DataOutputStream out, Map<String, Long> map) throws IOException {
        out.writeInt(map.size());
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeLong(entry.getValue());
        }
    }

    private static ConcurrentHashMap<String, Long> readLongMap(DataInputStream in) throws IOException {
        int size = in.readInt();
        ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
        for (int i = 0; i < size; i++) {
            String key = in.readUTF();
            long value = in.readLong();
            map.put(key, value);
        }
        return map;
    }

    // =========================================================
    // SET
    // =========================================================
    private static void writeStringSet(DataOutputStream out, Set<String> set) throws IOException {
        out.writeInt(set.size());
        for (String value : set) {
            out.writeUTF(value);
        }
    }

    private static HashSet<String> readStringSet(DataInputStream in) throws IOException {
        int size = in.readInt();
        HashSet<String> set = new HashSet<>();
        for (int i = 0; i < size; i++) {
            set.add(in.readUTF());
        }
        return set;
    }
}

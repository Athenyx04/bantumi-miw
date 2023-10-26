package es.upm.miw.bantumi.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "score_table")
public class Score {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "player_name")
    private String playerName;

    @ColumnInfo(name = "date_time")
    private String dateTime;

    @ColumnInfo(name = "seeds_player")
    private int seedsPlayer;

    @ColumnInfo(name = "seeds_opponent")
    private int seedsOpponent;

    public Score(@NonNull String playerName, String dateTime, int seedsPlayer, int seedsOpponent) {
        this.playerName = playerName;
        this.dateTime = dateTime;
        this.seedsPlayer = seedsPlayer;
        this.seedsOpponent = seedsOpponent;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getPlayerName() {
        return playerName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public int getSeedsPlayer() {
        return seedsPlayer;
    }

    public int getSeedsOpponent() {
        return seedsOpponent;
    }
}

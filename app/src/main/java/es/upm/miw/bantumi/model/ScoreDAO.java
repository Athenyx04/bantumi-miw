package es.upm.miw.bantumi.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScoreDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Score score);

    @Query("DELETE FROM score_table")
    void deleteAll();

    @Query("SELECT * FROM score_table ORDER BY seeds_player DESC LIMIT 10")
    LiveData<List<Score>> getTopTenScores();
}

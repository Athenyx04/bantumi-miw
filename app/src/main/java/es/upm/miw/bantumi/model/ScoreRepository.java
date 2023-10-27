package es.upm.miw.bantumi.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ScoreRepository {
    private ScoreDAO mScoreDao;
    private LiveData<List<Score>> mTopTenScores;

    ScoreRepository(Application application) {
        ScoreRoomDatabase db = ScoreRoomDatabase.getDatabase(application);
        mScoreDao = db.scoreDAO();
        mTopTenScores = mScoreDao.getTopTenScores();
    }

    LiveData<List<Score>> getTopTenScores() {
        return mTopTenScores;
    }

    void insert(Score score) {
        ScoreRoomDatabase.databaseWriteExecutor.execute(() -> {
            mScoreDao.insert(score);
        });
    }

    void deleteAll() {
        ScoreRoomDatabase.databaseWriteExecutor.execute(() -> {
            mScoreDao.deleteAll();
        });
    }
}

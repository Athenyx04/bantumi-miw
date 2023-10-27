package es.upm.miw.bantumi.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ScoreViewModel extends AndroidViewModel {
    private ScoreRepository mRepository;
    private final LiveData<List<Score>> mTopTenScores;

    public ScoreViewModel(Application application) {
        super(application);
        mRepository = new ScoreRepository(application);
        mTopTenScores = mRepository.getTopTenScores();
    }

    public LiveData<List<Score>> getTopTenScores() { return mTopTenScores; }

    public void insert(Score score) { mRepository.insert(score); }

    public void deleteAll() { mRepository.deleteAll(); }
}

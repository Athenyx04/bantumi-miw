package es.upm.miw.bantumi;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import es.upm.miw.bantumi.model.ScoreViewModel;

public class TopTenActivity extends AppCompatActivity {
    private ScoreViewModel mScoreViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topten_activity);

        RecyclerView rvTopTen = findViewById(R.id.rv_topten);
        // @TODO Adapter
        rvTopTen.setLayoutManager(new LinearLayoutManager(this));

        mScoreViewModel = new ViewModelProvider(this).get(ScoreViewModel.class);

        mScoreViewModel.getTopTenScores().observe(this, scores -> {
            // @TODO adapter.submitList(scores);
        });

    }
}

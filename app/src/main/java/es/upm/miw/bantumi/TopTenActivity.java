package es.upm.miw.bantumi;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import es.upm.miw.bantumi.model.ScoreViewModel;
import es.upm.miw.bantumi.views.ScoreListAdapter;

public class TopTenActivity extends AppCompatActivity {
    private ScoreViewModel mScoreViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ThemePreferenceHelper.getThemeMode(this)) {
            setTheme(R.style.Theme_Bantumi_Purple);
        } else {
            setTheme(R.style.Theme_Bantumi);
        }
        setContentView(R.layout.topten_activity);

        RecyclerView rvTopTen = findViewById(R.id.rv_topten);
        final ScoreListAdapter adapter = new ScoreListAdapter(new ScoreListAdapter.ScoreDiff());
        rvTopTen.setAdapter(adapter);
        rvTopTen.setLayoutManager(new LinearLayoutManager(this));

        mScoreViewModel = new ViewModelProvider(this).get(ScoreViewModel.class);

        mScoreViewModel.getTopTenScores().observe(this, scores -> {
            adapter.submitList(scores);
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.topten_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.opcDeleteScores) {
            DeleteScoresAlertDialog alertDialog = new DeleteScoresAlertDialog();
            alertDialog.show(getSupportFragmentManager(), "ConfirmScoresDeleteDialog");
            return true;
        } else {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.txtSinImplementar),
                    Snackbar.LENGTH_LONG
            ).show();
        }
        return true;
    }

    public void deleteAllScores() {
        mScoreViewModel.deleteAll();
    }
}

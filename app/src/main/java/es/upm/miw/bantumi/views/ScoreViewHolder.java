package es.upm.miw.bantumi.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.model.Score;

public class ScoreViewHolder extends RecyclerView.ViewHolder {
    private final TextView scoreItemPlayerName;
    private final TextView scoreItemDateTime;
    private final TextView scoreItemFinalScore;

    private ScoreViewHolder(View itemView) {
        super(itemView);
        scoreItemPlayerName = itemView.findViewById(R.id.tv_playerName);
        scoreItemDateTime = itemView.findViewById(R.id.tv_datetime);
        scoreItemFinalScore = itemView.findViewById(R.id.tv_finalScore);
    }

    public void bind(Score score) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");
        Date parsedDate = dateFormat.parse(score.getDateTime());
        scoreItemPlayerName.setText(score.getPlayerName());
        scoreItemDateTime.setText(SimpleDateFormat.getDateTimeInstance().format(parsedDate));
        scoreItemFinalScore.setText(String.format(
                Locale.getDefault(),
                "%02d - %02d",
                score.getSeedsPlayer(),
                score.getSeedsOpponent())
        );
    }

    static ScoreViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topten_item, parent, false);
        return new ScoreViewHolder(view);
    }
}

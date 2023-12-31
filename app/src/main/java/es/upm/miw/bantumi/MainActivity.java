package es.upm.miw.bantumi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.upm.miw.bantumi.model.BantumiViewModel;
import es.upm.miw.bantumi.model.Score;
import es.upm.miw.bantumi.model.ScoreViewModel;

public class MainActivity extends AppCompatActivity {

    protected final String LOG_TAG = "MiW";
    JuegoBantumi juegoBantumi;
    BantumiViewModel bantumiVM;
    ScoreViewModel scoreVM;
    int numInicialSemillas;
    SharedPreferences preferences;
    private boolean isSecondaryTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(getString(R.string.preferencesFile),
                Context.MODE_PRIVATE);
        isSecondaryTheme = ThemePreferenceHelper.getThemeMode(this);
        if (isSecondaryTheme) {
            setTheme(R.style.Theme_Bantumi_Purple);
        } else {
            setTheme(R.style.Theme_Bantumi);
        }

        setContentView(R.layout.activity_main);
        // Instancia el ViewModel y el juego, y asigna observadores a los huecos
        numInicialSemillas = Integer.parseInt(preferences.getString(getString(R.string.key_SeedNumber),
                getString(R.string.default_SeedNumber)));
        bantumiVM = new ViewModelProvider(this).get(BantumiViewModel.class);
        scoreVM = new ViewModelProvider(this).get(ScoreViewModel.class);
        juegoBantumi = new JuegoBantumi(bantumiVM, getPreferredTurnStart(), numInicialSemillas);
        crearObservadores();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isSecondaryTheme != ThemePreferenceHelper.getThemeMode(this)) {
            recreate();
        }

        preferences = getSharedPreferences(getString(R.string.preferencesFile),
                Context.MODE_PRIVATE);

        String prefPlayerName = preferences.getString(
                getString(R.string.key_PlayerName),
                getString(R.string.default_PlayerName)
        );
        boolean prefUseExternalStorage = preferences.getBoolean(
                getString(R.string.key_UseExternalStorage),
                getResources().getBoolean(R.bool.default_UseExternalStorage)
        );

        TextView tvJugador1 = findViewById(R.id.tvPlayer1);
        tvJugador1.setText(prefPlayerName);
        juegoBantumi.numInicialSemillas = Integer.parseInt(preferences.getString(getString(R.string.key_SeedNumber),
                getString(R.string.default_SeedNumber)));

        Log.i(LOG_TAG, "onRESUME(): Player Name = " + prefPlayerName);
        Log.i(LOG_TAG, "Use External Storage = " + ((prefUseExternalStorage) ? "ON" : "OFF"));
    }

    /**
     * Crea y subscribe los observadores asignados a las posiciones del tablero.
     * Si se modifica el contenido del tablero -> se actualiza la vista.
     */
    private void crearObservadores() {
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            int finalI = i;
            bantumiVM.getNumSemillas(i).observe(    // Huecos y almacenes
                    this,
                    new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            mostrarValor(finalI, juegoBantumi.getSemillas(finalI));
                        }
                    });
        }
        bantumiVM.getTurno().observe(   // Turno
                this,
                new Observer<JuegoBantumi.Turno>() {
                    @Override
                    public void onChanged(JuegoBantumi.Turno turno) {
                        marcarTurno(juegoBantumi.turnoActual());
                    }
                }
        );
    }

    /**
     * Indica el turno actual cambiando el color del texto
     *
     * @param turnoActual turno actual
     */
    private void marcarTurno(@NonNull JuegoBantumi.Turno turnoActual) {
        TextView tvJugador1 = findViewById(R.id.tvPlayer1);
        TextView tvJugador2 = findViewById(R.id.tvPlayer2);
        switch (turnoActual) {
            case turnoJ1:
                tvJugador1.setTextColor(getColor(R.color.white));
                tvJugador1.setBackgroundColor(getColor(android.R.color.holo_blue_light));
                tvJugador2.setTextColor(getColor(R.color.black));
                tvJugador2.setBackgroundColor(getColor(R.color.white));
                break;
            case turnoJ2:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador1.setBackgroundColor(getColor(R.color.white));
                tvJugador2.setTextColor(getColor(R.color.white));
                tvJugador2.setBackgroundColor(getColor(android.R.color.holo_blue_light));
                break;
            default:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador2.setTextColor(getColor(R.color.black));
        }
    }

    /**
     * Muestra el valor <i>valor</i> en la posición <i>pos</i>
     *
     * @param pos posición a actualizar
     * @param valor valor a mostrar
     */
    private void mostrarValor(int pos, int valor) {
        String num2digitos = String.format(Locale.getDefault(), "%02d", pos);
        // Los identificadores de los huecos tienen el formato casilla_XX
        int idBoton = getResources().getIdentifier("casilla_" + num2digitos, "id", getPackageName());
        if (0 != idBoton) {
            TextView viewHueco = findViewById(idBoton);
            viewHueco.setText(String.valueOf(valor));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opcAcercaDe:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.aboutTitle)
                        .setMessage(R.string.aboutMessage)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;
            case R.id.opcReiniciarPartida:
                RestartAlertDialog alertDialog = new RestartAlertDialog();
                alertDialog.show(getSupportFragmentManager(), "ConfirmRestartDialog");
                return true;
            case R.id.opcGuardarPartida:
                saveGameAction();
                return true;
            case R.id.opcRecuperarPartida:
                loadGameAction();
                return true;
            case R.id.opcAjustes:
                editPreferences();
                return true;
            case R.id.opcMejoresResultados:
                showTopTenScores();
                return true;
            default:
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.txtSinImplementar),
                        Snackbar.LENGTH_LONG
                ).show();
        }
        return true;
    }

    /**
     * Acción que se ejecuta al pulsar sobre cualquier hueco
     *
     * @param v Vista pulsada (hueco)
     */
    public void huecoPulsado(@NonNull View v) {
        String resourceName = getResources().getResourceEntryName(v.getId()); // pXY
        int num = Integer.parseInt(resourceName.substring(resourceName.length() - 2));
        Log.i(LOG_TAG, "huecoPulsado(" + resourceName + ") num=" + num);
        switch (juegoBantumi.turnoActual()) {
            case turnoJ1:
                juegoBantumi.jugar(num);
                break;
            case turnoJ2:
                juegaComputador();
                break;
            default:    // JUEGO TERMINADO
                finJuego();
        }
        if (juegoBantumi.juegoTerminado()) {
            finJuego();
        }
    }

    /**
     * Elige una posición aleatoria del campo del jugador2 y realiza la siembra
     * Si mantiene turno -> vuelve a jugar
     */
    void juegaComputador() {
        while (juegoBantumi.turnoActual() == JuegoBantumi.Turno.turnoJ2) {
            int pos = 7 + (int) (Math.random() * 6);    // posición aleatoria [7..12]
            Log.i(LOG_TAG, "juegaComputador(), pos=" + pos);
            if (juegoBantumi.getSemillas(pos) != 0 && (pos < 13)) {
                juegoBantumi.jugar(pos);
            } else {
                Log.i(LOG_TAG, "\t posición vacía");
            }
        }
    }

    /**
     * El juego ha terminado. Volver a jugar?
     */
    private void finJuego() {
        String texto = (juegoBantumi.getSemillas(6) > 6 * numInicialSemillas)
                ? "Gana Jugador 1"
                : "Gana Jugador 2";
        if (juegoBantumi.getSemillas(6) == 6 * numInicialSemillas) {
            texto = "¡¡¡ EMPATE !!!";
        }
        Snackbar.make(
                findViewById(android.R.id.content),
                texto,
                Snackbar.LENGTH_LONG
        )
        .show();

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");
        String iso8601String = dateFormat.format(date);

        scoreVM.insert(new Score(
                preferences.getString(
                        getString(R.string.key_PlayerName),
                        getString(R.string.default_PlayerName)
                ),
                iso8601String,
                juegoBantumi.getSemillas(6),
                juegoBantumi.getSemillas(13)
        ));

        // terminar
        new FinalAlertDialog().show(getSupportFragmentManager(), "ALERT_DIALOG");
    }

    /**
     * Devuelve el nombre del fichero de partidas guardadas
     *
     * @return nombre del fichero
     */
    private String getSavedGamesFileName() {
        String fileName = getString(R.string.default_SavedGamesFileName);
        Log.i(LOG_TAG, "Nombre fichero partidas guardadas: " + fileName);

        return fileName;
    }

    /**
     * Determina si prefiere memoria interna o externa
     *
     * @return valor lógico
     */
    private boolean useInternalStorage() {
        return preferences.getBoolean(
                getString(R.string.key_UseExternalStorage),
                getResources().getBoolean(R.bool.default_UseExternalStorage)
        );
    }

    /**
     * Devuelve el stream de escritura para un fichero especificado
     *
     * @param fileName Nombre del fichero en el que escribir
     * @param append Booleano que sobreescribe el fichero si false, añade si true
     */
    private FileOutputStream getFileOutputStream(String fileName, boolean append) {
        try {
            if (useInternalStorage()) {
                return openFileOutput(fileName, append ? Context.MODE_APPEND : Context.MODE_PRIVATE); // Memoria interna
            } else {    // Comprobar estado SD card
                String sdCardStatus = Environment.getExternalStorageState();
                if (sdCardStatus.equals(Environment.MEDIA_MOUNTED)) {
                    String filePath = getExternalFilesDir(null) + "/" + fileName;
                    return new FileOutputStream(filePath, append);
                } else {
                    Toast.makeText(
                            this,
                            getString(R.string.txtExternalStorageError),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "FILE I/O ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Devuelve el stream de lectura para un fichero especificado
     *
     * @param fileName Nombre del fichero a leer
     */
    private BufferedReader getInputBufferedReader(String fileName) {
        try {
            if (useInternalStorage()) {
                return new BufferedReader(new InputStreamReader(openFileInput(fileName))); // Memoria interna
            } else {    // Comprobar estado SD card
                String sdCardStatus = Environment.getExternalStorageState();
                if (sdCardStatus.equals(Environment.MEDIA_MOUNTED)) {
                    String filePath = getExternalFilesDir(null) + "/" + fileName;
                    return new BufferedReader(new FileReader(filePath));
                } else {
                    Toast.makeText(
                            this,
                            getString(R.string.txtExternalStorageError),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "FILE I/O ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Al pulsar el botón Guardar → añadir al fichero.
     */
    private void saveGameAction() {
        try {  // Add to file
            FileOutputStream fos = getFileOutputStream(getSavedGamesFileName(), false);
            if (fos == null) {
                return;
            }

            // Escribir fichero
            fos.write(juegoBantumi.serializa().getBytes());
            fos.write('\n');
            fos.close();
            Log.i(LOG_TAG, "Click botón Guardar -> AÑADIR al fichero");

            // Toast éxito
            Toast.makeText(
                    this,
                    getString(R.string.txtSaveGameSuccess),
                    Toast.LENGTH_SHORT
            ).show();
        } catch (Exception e) {
            Log.e(LOG_TAG, "FILE I/O ERROR: " + e.getMessage());
            e.printStackTrace();

            // Toast error
            Toast.makeText(
                    this,
                    getString(R.string.txtSaveGameFailure),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    /**
     * Al pulsar el botón Cargar → leer el fichero.
     */
    private void loadGameAction() {
        try {
            BufferedReader fin = getInputBufferedReader(getSavedGamesFileName());
            if (fin == null) {
                // Toast fichero vacío
                Toast.makeText(
                        this,
                        getString(R.string.txtLoadGameEmpty),
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            // Leer fichero
            String saveData = fin.readLine();
            fin.close();
            Log.i(LOG_TAG, "Click botón Cargar -> CARGA los datos del fichero");

            if (saveData != null) {
                juegoBantumi.deserializa(saveData);

                // Toast éxito
                Toast.makeText(
                        this,
                        getString(R.string.txtLoadGameSuccess),
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                // Toast fichero vacío
                Toast.makeText(
                        this,
                        getString(R.string.txtLoadGameEmpty),
                        Toast.LENGTH_SHORT
                ).show();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "FILE I/O ERROR: " + e.getMessage());
            e.printStackTrace();

            // Toast error
            Toast.makeText(
                    this,
                    getString(R.string.txtLoadGameFailure),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void editPreferences() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void showTopTenScores() {
        Intent intent = new Intent(this, TopTenActivity.class);
        startActivity(intent);
    }

    public JuegoBantumi.Turno getPreferredTurnStart() {
        if (preferences == null) {
            return JuegoBantumi.Turno.turnoJ1;
        }

        if (preferences.getBoolean(getString(R.string.key_Player2Start), false)) {
            return JuegoBantumi.Turno.turnoJ2;
        }
        return JuegoBantumi.Turno.turnoJ1;
    }
}
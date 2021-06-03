package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPort, clientPort, clientAddr, pokemonName;
    private ImageView pokemonImage;
    private Button serverConnect, serverGetInfo;
    private TextView showPokemonAbilities, showPokemonTypes;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    private ButtonClickListener buttonClickListenerServer = new ButtonClickListener();
    private class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            String port = serverPort.getText().toString();
            if(port.isEmpty() || port == null) {
                Toast.makeText(getApplicationContext(), "Please fill server port", Toast.LENGTH_SHORT).show();
            }

            serverThread = new ServerThread(Integer.parseInt(port));

            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }

    private ButtonClickListenerGetInfo buttonClickListenergetInfo = new ButtonClickListenerGetInfo();
    private class ButtonClickListenerGetInfo implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            String port = clientPort.getText().toString();
            String addr = clientAddr.getText().toString();
            String name = pokemonName.getText().toString();

            if(port.isEmpty() || port == null) {
                Toast.makeText(getApplicationContext(), "Please fill client port", Toast.LENGTH_SHORT).show();
                return;
            }
            if(addr.isEmpty() || addr == null) {
                Toast.makeText(getApplicationContext(), "Please fill client address", Toast.LENGTH_SHORT).show();
                return;
            }
            if(name.isEmpty() || name == null) {
                Toast.makeText(getApplicationContext(), "Please fill pokemon name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            clientThread = new ClientThread(addr, Integer.parseInt(port), name, showPokemonAbilities, showPokemonTypes);
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPort = findViewById(R.id.serverPort);
        serverConnect = findViewById(R.id.serverConnect);
        clientAddr = findViewById(R.id.clientAddress);
        clientPort = findViewById(R.id.clientPort);
        pokemonName = findViewById(R.id.pokemonName);
        pokemonImage = findViewById(R.id.pokemonImage);
        serverGetInfo = findViewById(R.id.clientGetInfo);
        clientPort = findViewById(R.id.clientPort);

        showPokemonAbilities = findViewById(R.id.showAbilities);
        showPokemonTypes = findViewById(R.id.showPokemonTypes);

        serverConnect.setOnClickListener(buttonClickListenerServer);
        serverGetInfo.setOnClickListener(buttonClickListenergetInfo);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
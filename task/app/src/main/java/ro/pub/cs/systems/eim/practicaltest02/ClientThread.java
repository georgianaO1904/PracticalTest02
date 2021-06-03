package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private String address;
    private int port;
    private String pokemonName;
    private TextView showAbilitiesTextView;
    private TextView showTypeTextView;

    private Socket socket;

    public ClientThread(String address, int port, String pokemonName, TextView showAbilitiesTextView, TextView showTypeTextView)
    {
        this.address = address;
        this.port = port;
        this.pokemonName = pokemonName;
        this.showAbilitiesTextView = showAbilitiesTextView;
        this.showTypeTextView = showTypeTextView;
    }

    public void run(){
        try{
            socket = new Socket(address, port);
            if(socket ==  null){
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            if(bufferedReader == null || printWriter == null)
            {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(pokemonName);
            printWriter.flush();

            String pokemonInformation;

            while ((pokemonInformation = bufferedReader.readLine()) != null){
                final String finalizedPokemonInformation = pokemonInformation;
                String pokemonTypes = "", pokemonAbilities = "";
                // TODO de primit raspuns type si abilities

                showTypeTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        showTypeTextView.setText(pokemonTypes);
                    }
                });

                showAbilitiesTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        showAbilitiesTextView.setText(pokemonAbilities);
                    }
                });
            }

        }catch (IOException ioException)
        {
            if(Constants.DEBUG)
                ioException.printStackTrace();
        }
        finally {
            if(socket != null)
            {
                try {
                    socket.close();
                } catch (IOException ioException)
                {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if(Constants.DEBUG)
                    {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}

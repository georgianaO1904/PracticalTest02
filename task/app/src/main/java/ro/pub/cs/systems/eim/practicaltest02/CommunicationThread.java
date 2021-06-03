package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread  extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            String pokemonName = bufferedReader.readLine();
            String pokemonInformation = bufferedReader.readLine();
            if (pokemonName == null || pokemonName.isEmpty() || pokemonInformation == null || pokemonInformation.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client(pokemonName)!");
                return;
            }
            HttpClient httpClient = new DefaultHttpClient();
            String pageSourceCode = "";

            if (false) {
                HttpPost httpPost = new HttpPost(Constants.WEB_SERVICE_ADDRESS);
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("name", pokemonName));
                params.add(new BasicNameValuePair("type", Constants.TYPE));
                params.add(new BasicNameValuePair("abilities", Constants.ABILITIES));
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                httpPost.setEntity(urlEncodedFormEntity);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                pageSourceCode = httpClient.execute(httpPost, responseHandler);
            } else {
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + Constants.SLASH + pokemonName);
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }
            }

            if (pageSourceCode == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                return;
            }else
                Log.i(Constants.TAG, pageSourceCode );

            //Update for pokemon API
//            if(false){
//                Document document = Jsoup.parse(pageSourceCode);
//                Element element = document.child(0);
//                Elements elements = element.getElementsByTag(Constants.NAME);
//                for (Element script : elements){
//                    String scriptData = script.data();
//                    if (scriptData.contains(Constants.WEB_SERVICE_POKEMON)){
//                        int position = scriptData.indexOf(Constants.WEB_SERVICE_POKEMON);
//                        scriptData = scriptData.substring(position);
//                        JSONObject content = new JSONObject(scriptData);
//                        JSONObject currentPokemon = content.getJSONObject(Constants.CURRENT_POKEMON);
//                        String name = currentPokemon.getString(Constants.NAME);
//                        String abilities = currentPokemon.getString(Constants.ABILITIES);
//                        String type = currentPokemon.getString(Constants.TYPES);
//                    }
//                }
//            }
            JSONObject content = new JSONObject(pageSourceCode);
            JSONArray pokemonArray = content.getJSONArray(Constants.POKEMON);
            JSONObject pokemon;
            String info = "";
            for(int i = 0; i < pokemonArray.length(); i++){
                pokemon = pokemonArray.getJSONObject(i);
                info += pokemon.getString(Constants.POKEMON) + "," + "" + pokemon.getString(Constants.ABILITIES);
                if(i<pokemonArray.length() - 1)
                {
                    info +=";";
                }
                JSONObject pokemonJSON = content.getJSONObject(Constants.POKEMON);
                String name = pokemonJSON.getString(Constants.NAME);
                String abilities = pokemonJSON.getString(Constants.ABILITIES);
                String type = pokemonJSON.getString(Constants.TYPES);

            }

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }catch (JSONException jsonException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
            if (Constants.DEBUG) {
                jsonException.printStackTrace();
            }
        }
    }
}


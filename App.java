package com.diogo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.gson.Gson;

/**
 * Using assembly ai API to transcript an audio file into text
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //my API key: xxxxxx
        //audio url: https://github.com/diogo-dev/assemblyAPI/raw/refs/heads/main/Grava%C3%A7%C3%A3o%20(45).m4a
        
        Transcript transcript = new Transcript();
        transcript.setAudio_url("https://github.com/diogo-dev/assemblyAPI/raw/refs/heads/main/Grava%C3%A7%C3%A3o%20(45).m4a");
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(transcript);
        
        try {
            HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                .header("Authorization", "put your API key here!")
                .POST(BodyPublishers.ofString(jsonRequest))
                .build();
            
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> postResponse = httpClient.send(postRequest, BodyHandlers.ofString());

            System.out.println(postResponse.body());
            transcript = gson.fromJson(postResponse.body(), Transcript.class);

            // GET() method is the default value for HttpResquest → I can omit it if I want
            HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript/" + transcript.getId()))
                .header("Authorization", "61b349b9957e44cb9db2be8f7e4faa9b")
                .GET()
                .build();

            while (true) {
                HttpResponse<String> getResponse = httpClient.send(getRequest, BodyHandlers.ofString());
                transcript = gson.fromJson(getResponse.body(), Transcript.class);

                System.out.println(transcript.getStatus());

                if ("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus())) {
                    break;
                }
                Thread.sleep(1000);
            }

            System.out.println("Transcription completed!");
            System.out.println(transcript.getText());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        

    }
}

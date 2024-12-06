package com.lovense;

import com.google.gson.Gson;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import lombok.extern.slf4j.Slf4j;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import net.runelite.api.GameState;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.SwingUtil;
import okhttp3.*;

import static net.runelite.http.api.RuneLiteAPI.JSON;

@Slf4j
@Singleton
public class LovensePanel extends PluginPanel {
    private final LovensePlugin lovensePlugin;
    private final LovenseConfig lovenseConfig;
    private final OkHttpClient client;

    private final Gson gson;

    private JPanel lovenseFrame;
    private LovenseToy selectedToy;
    private JButton testButton;

    @Inject
    public LovensePanel (
            LovensePlugin lovensePlugin,
            LovenseConfig lovenseConfig,
            OkHttpClient client
    ){
        super(false);

        this.lovensePlugin = lovensePlugin;
        this.lovenseConfig = lovenseConfig;
        this.client = client;

        gson = new GsonBuilder().create();
    }

    void init()
    {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel lovenseContainer = new JPanel();
        lovenseContainer.setLayout(new BorderLayout());
        lovenseContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JButton getToysButton = createGetToysButton("Get toys on your LAN");

        lovenseFrame = new JPanel();
        lovenseFrame.add(getToysButton);
        lovenseContainer.add(lovenseFrame, BorderLayout.CENTER);
        add(lovenseContainer, BorderLayout.CENTER);
    }

    private JButton createGetToysButton(String btnText)
    {
        JButton btn = new JButton(btnText);
        btn.setToolTipText("Loads Toys using the given config");
        btn.setBackground(ColorScheme.DARK_GRAY_COLOR);
        btn.setUI(new BasicButtonUI());
        btn.addActionListener(e -> {
            try {
                getToys();
            }
            catch(IOException ex){
                // do something maybe
            }
        });
        return btn;
    }

    private JButton createTestButton(String btnText)
    {
        JButton btn = new JButton(btnText);
        btn.setToolTipText("Send a test command to the selected toy with Duration: 5s, Intensity: 10");
        btn.setBackground(ColorScheme.DARK_GRAY_COLOR);
        btn.setUI(new BasicButtonUI());
        btn.addActionListener(e -> {
            try {
                sendToyCommand(selectedToy.id, 5, 10);
            }
            catch(IOException ex){
                // do something maybe
            }
        });
        return btn;
    }

    private void getToys() throws IOException {
        String toysUrl = String.format("http://%s:%s/command", lovenseConfig.localIp(), lovenseConfig.httpPort());
        HttpUrl url = HttpUrl.parse(toysUrl);

        String getToysRequest = "{\"command\":\"getToys\"}";

        Request request = new Request.Builder()
                .post(RequestBody.create(JSON, getToysRequest))
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute())
        {
            String responseBody = response.body().string();
            GetToysResponse toyResponse = gson.fromJson(responseBody, new TypeToken<GetToysResponse>(){}.getType());
            handleGetToysResponse(toyResponse);
        }
        catch (JsonParseException ex)
        {
            throw new IOException(ex);
        }
    }

    private void handleGetToysResponse(GetToysResponse getToysResponse){
        final JComboBox<LovenseToy> toys = new JComboBox<LovenseToy>();

        if(getToysResponse.data.toys.equals("{}")){
            // No toys, maybe add message?
            return;
        }

        HashMap<String, LovenseToy> toyData =  gson.fromJson(getToysResponse.data.toys, new TypeToken<HashMap<String, LovenseToy>>(){}.getType());

        // Create button for each available toy, with callback to store in current session
        for (LovenseToy toy : toyData.values()) {
            toys.addItem(toy);
        }

        toys.addItemListener(e ->
        {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                final LovenseToy source = (LovenseToy) e.getItem();
                this.selectedToy = source;

                if(testButton == null){
                    testButton = createTestButton("Test");
                    lovenseFrame.add(testButton);
                }
            }
        });

        toys.setSelectedIndex(-1);
        lovenseFrame.add(toys);
    }

    private void sendToyCommand(String toyId, int timeSec, int intensity) throws IOException {
        if(intensity < 1) {
            intensity = 1;
        }
        if(intensity > 20){
            intensity = 20;
        }

        if(timeSec < 1){
            timeSec = 1;
        }

        ToyCommand command = new ToyCommand(toyId, timeSec, intensity);
        String jsonPayload = gson.toJson(command);

        String toysUrl = String.format("http://%s:%s/command", lovenseConfig.localIp(), lovenseConfig.httpPort());
        HttpUrl url = HttpUrl.parse(toysUrl);
        Request request = new Request.Builder()
                .post(RequestBody.create(JSON, jsonPayload))
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute())
        {
            // Don't care about the response
            String responseBody = response.body().string();
            int x = 3;
        }
        catch (JsonParseException ex)
        {
            throw new IOException(ex);
        }
    }
}

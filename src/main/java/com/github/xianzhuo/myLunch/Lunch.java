package com.github.xianzhuo.myLunch;

import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Lunch
 * Created by shangjie
 * 16-10-25
 */
public class Lunch {
    private static final String configPath = System.getProperty("user.dir") + "/data/config.json";
    private static String dateStoredPath = "";
    private static final Logger LOG = LoggerFactory.getLogger(Lunch.class);
    private static int period;
    private static final int DEFAULT_PERIOD = 5;
    private List<String> foods = new ArrayList<>();
    private List<String> menu = new ArrayList<>();
    private Map configMap;

    public static void main(String... args) {
        Lunch lunch = new Lunch();
//        lunch.generateWeekMenu();
        lunch.getTodayFood();
    }

    public Lunch() {
        initConfig();
        initFoods();
        initPeriod();
        initDataStoredPath();

        LOG.info("Init Lunch - foods: [" + toStringList(foods) + "]");
        LOG.info("Init Lunch - menu: [" + toStringList(menu) + "]");
        LOG.info("Init Lunch - period: [" + period + "]");
        LOG.info("Init Lunch finish.");
    }

    private void initConfig() {
        File configFile = new File(configPath);
        if (!configFile.exists()) {
            LOG.error("can't find config.json file");
            return;
        }

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(configFile);
            bufferedReader = new BufferedReader(fileReader);
            String line;
            String config = "";
            while ((line = bufferedReader.readLine()) != null) {
                config += line;
            }
            configMap = GsonHelper.DEFAULT_GSON.fromJson(config, Map.class);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                fileReader.close();
                bufferedReader.close();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private void initFoods() {
        if (configMap != null && configMap.containsKey("foods")) {
            foods = (List<String>) configMap.get("foods");
        }
    }

    private void initPeriod() {
        if (configMap != null && configMap.containsKey("period")) {
            period = new Double((Double) configMap.get("period")).intValue();
        } else {
            period = DEFAULT_PERIOD;
        }
    }

    private void initDataStoredPath() {
        if (configMap != null && configMap.containsKey("storedPath") &&
                !((String) configMap.get("storedPath")).isEmpty()) {
            dateStoredPath = (String) configMap.get("storedPath");
        } else {
            dateStoredPath = System.getProperty("user.dir") + "/data/data";
        }
    }

    public void generateWeekMenu() {
        if (menu.isEmpty() && new File(dateStoredPath).exists()) {
            readMenuFromDataStoredFile();
        } else {
            menu.clear();
            for (int index = 0; index < period; index++) {
                int random = (int) (1 + Math.random() * 10) % period;
                menu.add(foods.get(random));
            }
        }
        writeMenuToDataStoredFile();
        LOG.info("{menu: [" + toStringList(menu) + "]}");
    }

    private void writeMenuToDataStoredFile() {
        File data = new File(dateStoredPath);
        OutputStream fileOutputStream = null;
        try {
            if (!data.exists()) {
                data.createNewFile();
                fileOutputStream = new FileOutputStream(data);
                fileOutputStream.write(GsonHelper.DEFAULT_GSON.toJson(menu).getBytes());
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private void readMenuFromDataStoredFile() {
        File data = new File(dateStoredPath);
        BufferedReader bufferedReader = null;
        try {
            if (data.isFile() && data.exists()) {
                bufferedReader = new BufferedReader(new FileReader(data));
                String menuJson = "";
                String tmp = "";
                while ((tmp = bufferedReader.readLine()) != null) {
                    menuJson += tmp;
                }
                if (menuJson != null && !menuJson.isEmpty()) {
                    menu = GsonHelper.DEFAULT_GSON.fromJson(menuJson, new TypeToken<List<String>>() {
                    }.getType());
                }
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public String getTodayFood() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekDay > period) {
            menu.clear();
            File data = new File(dateStoredPath);
            if (data.exists()) {
                calendar.add(Calendar.DATE, (weekDay - 1) * -1);
                data.renameTo(new File(data.getAbsolutePath() + "." + new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime())));
            }
            LOG.info("Now [" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()) + "] is weekend, i have to clear my menu.");
            return "";
        } else if (menu.isEmpty()) {
            generateWeekMenu();
        }
        LOG.info("Today's food is [" + menu.get(weekDay - 1) + "], and today is the [" + weekDay + "]th day of week.");
        return menu.get(weekDay - 1);
    }

    public void promptFood() {
        String todayFood = getTodayFood();
        // TODO: 16-10-25 system to prompt me what food to eat
    }

    private String toStringList(List<String> input) {
        String msg = "";
        for (String item : input) {
            msg += "," + item;
        }
        if (msg.length() > 1) {
            return msg.substring(1);
        } else {
            return msg;
        }
    }
}

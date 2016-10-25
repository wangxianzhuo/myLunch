package com.github.xianzhuo.myLunch;

import org.apache.log4j.PropertyConfigurator;
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
    private static final String configPath = System.getProperty("user.dir") + "/config";
    private static final Logger LOG = LoggerFactory.getLogger(Lunch.class);
    private static final int WEEK_DAY = 5;
    private List<String> foods = new ArrayList<>();
    private List<String> menu = new ArrayList<>();

    public static void main(String... args) {
        Lunch lunch = new Lunch();
        lunch.generateWeekMenu();
        lunch.getTodayFood();
    }

    public Lunch() {
        initConfig();
        LOG.info("Init Lunch - foods: [" + toStringList(foods) + "]");
        LOG.info("Init Lunch - menu: [" + toStringList(menu) + "]");
        LOG.info("Init Lunch - week day: [" + WEEK_DAY + "]");
        LOG.info("Init Lunch finish.");
    }

    private void initConfig() {
        File config = new File(configPath);
        if (!config.exists()) {
            LOG.error("can't find config file");
            return;
        }

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(config);
            bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                foods.add(line);
            }
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
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

    public void generateWeekMenu() {
        if (!menu.isEmpty()) {
            menu.clear();
        }
        for (int index = 0; index < WEEK_DAY; index++) {
            int random = (int) (1 + Math.random() * 10) % WEEK_DAY;
            menu.add(foods.get(random));
        }
        LOG.info("{menu: [" + toStringList(menu) + "]}");
    }

    public String getTodayFood() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekDay > 5) {
            menu.clear();
            LOG.info("Now [" + new SimpleDateFormat("yyyy-MM-dd HH:mm") + "] is weekend, i have to clear my menu.");
            return "";
        }
        if (menu.isEmpty()) {
            generateWeekMenu();
        }
        LOG.info("Today's food is [" + menu.get(weekDay) + "]");
        return menu.get(weekDay);
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

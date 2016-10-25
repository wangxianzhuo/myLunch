package com.github.xianzhuo.myLunch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * PeriodTask
 * Created by shangjie
 * 16-10-25
 */
public class PeriodTask {
    private static final Logger LOG = LoggerFactory.getLogger(PeriodTask.class);
    private static final int MILLISECOND_PER_DAY = 1000 * 60 * 60 * 24;
    private static final int MILLISECOND_PER_MINUTES = 1000 * 60;
    private static String periodTime = "11:00:00";

    public static void main(String... args) {
        LOG.info("Init PeriodTask - period time: [" + periodTime + "]");

        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        long initDelay = getTimeMillis(periodTime) - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : MILLISECOND_PER_DAY + initDelay;

        service.scheduleAtFixedRate(new FoodTask(), initDelay, MILLISECOND_PER_DAY, TimeUnit.MILLISECONDS);
    }

    private static long getTimeMillis(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
class FoodTask implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(FoodTask.class);
    private Lunch lunch = new Lunch();
    @Override
    public void run() {
        try {
            lunch.promptFood();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}

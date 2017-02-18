package net.homelinux.mickey.dia.jaxrs;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.ThreadManager;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.DeadlineExceededException;

import net.htmlparser.jericho.Source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.homelinux.mickey.dia.Ekikara2OuDia;
import net.homelinux.mickey.dia.Formatter;
import net.homelinux.mickey.dia.OuDiaFormatter;

public class Ekikara2OuDiaBeanImpl implements Ekikara2OuDiaBean {
    protected static Logger log =
        LoggerFactory.getLogger(Ekikara2OuDiaBeanImpl.class);
    private static final String RESOURCE_NAME = "ekikara",
        DEFAULT_URL_STRING = "defaultUrl", DOWN_STRING = "down",
        UP_STRING = "up", EXT_STRING = ".htm",
        PROCESS_TABLES_PATTERN_STRING = "[1-9,]+",
        LINE_NUMBER_PATTERN_STRING = "\\d{7}",
        START_TIME_PATTERN_STRING = "\\d{3,4}",
        DAY_PATTERN_STRING = "(|_sat|_holi)",
        PROCESS_TABLES_STRING = "processTables",
        KITEN_JIKOKU_STRING = "KitenJikoku";
    private static final Pattern OPTION_END_TAG_PATTERN =
        Pattern.compile("</option>");
    private ResourceBundle resources = ResourceBundle.getBundle(RESOURCE_NAME);
    private Ekikara2OuDia ekikara2OuDia;

    @Override
    public Response getOuDia(String processTables, String lineNumber,
                             String startTime, String day,
                             final boolean reverse, String referer) {
        log.info("url: {} processTables: {}\nreferer: {}",
                 resources.getString(DEFAULT_URL_STRING) + lineNumber
                 + resources.getString(DOWN_STRING) + "1" + day
                 + EXT_STRING, processTables, referer);
        log.debug("processTables: {}\nlineNumber: {}\nstartTime: {}\nday: {}"
                  + "\nreverse: {}", processTables, lineNumber, startTime, day,
                  reverse);
        checkParams(processTables, lineNumber, startTime, day);

        System.setProperty(PROCESS_TABLES_STRING, processTables);
        System.setProperty(KITEN_JIKOKU_STRING, startTime);

        List<String> urlArgs = createArgumentURL(lineNumber, day, reverse);

        log.debug("urlArgs: {}", urlArgs);

        ekikara2OuDia = new Ekikara2OuDia();
        final TreeMap<String, Source> sourceMap =
            new TreeMap<>(new Comparator<String>() {
                    public int compare(String url0, String url1) {
                        if (!reverse
                            || (url0.contains("/" + DOWN_STRING)
                                && url1.contains("/" + DOWN_STRING))
                            || (url0.contains("/" + UP_STRING)
                                && url1.contains("/" + UP_STRING))) {
                            return url0.compareTo(url1);
                        }
                        return url1.compareTo(url0);
                    }
                });
        
        ExecutorService pool = Executors.newCachedThreadPool();
        try {
            pool = Executors.newFixedThreadPool
                (40, ThreadManager.currentRequestThreadFactory());
        } catch (NullPointerException e) {}
        final List<WebApplicationException> exceptions = new ArrayList<>();
        // Thread.currentThread().setUncaughtExceptionHandler
        //     (new Thread.UncaughtExceptionHandler() {
        //             public void uncaughtException(Thread t, Throwable e) {
        //                 exceptions.add(e);
        //             }
        //         });
        for (final String url : urlArgs) {
            pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runProcess(sourceMap, ekikara2OuDia, url);
                        } catch (WebApplicationException e) {
                            exceptions.add(e);
                        }
                    }
                });
        }
        pool.shutdown();
        try {
            pool.awaitTermination(3600, SECONDS);
        } catch (InterruptedException e) {
            log.error("interrupted exception", e);
            String message
                = "Excection time was over 60 seconds.\n"
                + "Retry to reload (F5/Ctrl-R) a few times.\n" + e;
            Response response =
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                type(MediaType.TEXT_PLAIN).entity(message).build();
            throw new WebApplicationException(e, response);
        }
        log.debug("exceptions:{}", exceptions);
        if (!exceptions.isEmpty()) {
            throw exceptions.get(0);
        }
        for (String url : sourceMap.keySet()) {
            ekikara2OuDia.process(sourceMap.get(url));
        }
        if (ekikara2OuDia.rule != null) {
            ekikara2OuDia.adjust();
        }
        log.info("title: {}\nupdateDate: {}", ekikara2OuDia.getTitle(),
                 ekikara2OuDia.getUpdateDate());
        Formatter formatter =
            new OuDiaFormatter(ekikara2OuDia.getTitle() + " "
                               + ekikara2OuDia.getUpdateDate(),
                               ekikara2OuDia.getAllStations(),
                               ekikara2OuDia.getDownTrains(),
                               ekikara2OuDia.getUpTrains(),
                               urlArgs.toString().replaceAll(",", "") + " "
                               + ekikara2OuDia.getUpdateDate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(out, "Shift_JIS");
            writer.write(formatter.format());
            writer.close();
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
            log.error("lineNumber: " + lineNumber, e);
            Response response =
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                type(MediaType.TEXT_PLAIN).entity(e.toString()).build();
            throw new WebApplicationException(e, response);
        }
        log.trace("sjis contents: {}", new String(out.toByteArray()));
        return Response.ok(out.toByteArray(),
                           MediaType.APPLICATION_OCTET_STREAM)
            .header("Content-disposition", "attachment; filename="
                    + lineNumber + day + ".oud")
            .header("Access-Control-Allow-Origin", "http://ekikara.jp")
            .header("Access-Control-Allow-Headers",
                    "origin, content-type, accept, authorization")
            .header("Access-Control-Allow-Credentials", "true")
            .header("Access-Control-Allow-Methods", "POST")
            .build();
    }

    private void checkParams(String processTables, String lineNumber,
                             String startTime, String day) {
        String message = null;
        if (!processTables.matches(PROCESS_TABLES_PATTERN_STRING)) {
            message = "Bad Shorisuru-Hyo.";
        } else if (!lineNumber.matches(LINE_NUMBER_PATTERN_STRING)) {
            message = "Bad Senku-Bango.";
        } else if (!startTime.matches(START_TIME_PATTERN_STRING)) {
            message = "Bad Kiten-Jikoku.";
        } else if (!day.matches(DAY_PATTERN_STRING)) {
            message = "Bad Youbi-no-Shitei.";
        }
        if (message != null) {
            log.error("checkParams error! {}\nprocessTables: {}\nlineNumber: "
                      + "{}\nstartTime: {}\nday: {}",
                      message, processTables, lineNumber, startTime, day);
            Response response =
                Response.status(Response.Status.BAD_REQUEST).
                type(MediaType.TEXT_PLAIN).entity(message).build();
            throw new WebApplicationException(response);
        }
    }

    private void runProcess(Map<String, Source> sourceMap,
                            Ekikara2OuDia ekikara2OuDia, String url) {
        try {
            sourceMap.put(url, ekikara2OuDia.fetchUrlAndParse(url));
        } catch (DeadlineExceededException e) {
            log.error("url: " + url, e);
            String message
                = "Execution time was over 60 seconds. "
                + "Retry to reload (F5/Ctrl-R) some times. "
                + "If you fail again and again, consult me by e-mail.\n" + e;
            Response response =
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                type(MediaType.TEXT_PLAIN).entity(message).build();
            throw new WebApplicationException(e, response);
        } catch (IndexOutOfBoundsException e) {
            log.error("url: " + url, e);
            String message
                = "Maybe your Shorisuru-Hyo is out of bounds or "
                + "some internal error(not yet resolved).\n" + e;
            Response response =
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                type(MediaType.TEXT_PLAIN).entity(message).build();
            throw new WebApplicationException(e, response);
        } catch (SocketTimeoutException e) {
            log.error("url: " + url, e);
            String message
                = "Could not get " + url
                + "\nRetry to reload this request.\n"
                + "If you fail again and again, consult me by e-mail.\n" + e;
            Response response =
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                type(MediaType.TEXT_PLAIN).entity(message).build();
            throw new WebApplicationException(e, response);
        } catch (Exception e) {
            log.error("url: " + url, e);
            Response response =
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                type(MediaType.TEXT_PLAIN).entity(e.toString()).build();
            throw new WebApplicationException(e, response);
        }
    }

    private int countPages(String lineNumber, String day, String direction) {
        int pages = 0;
        URL defaultURL = null;
        try {
            defaultURL = new URL(resources.getString(DEFAULT_URL_STRING)
                                     + lineNumber + direction + "1" + day
                                     + EXT_STRING);
            log.debug("defaultURL: {}", defaultURL);
            BufferedReader reader = new BufferedReader
                (new InputStreamReader(defaultURL.openStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = OPTION_END_TAG_PATTERN.matcher(line);
                if (matcher.find()) {
                    pages++;
                }
            }
            reader.close();
        } catch (MalformedURLException e) {
            log.error("defaultURL: " + defaultURL, e);
            Response response =
                Response.status(Response.Status.NOT_FOUND).
                type(MediaType.TEXT_PLAIN).
                entity("Could not find Senku-Bango you specified.\n" + e).build();
            throw new WebApplicationException(e, response);
        } catch (SocketTimeoutException e) {
            log.error("defaultURL: " + defaultURL, e);
            Response response =
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                type(MediaType.TEXT_PLAIN).
                entity("Could not get " + defaultURL + "\n" + e).build();
            throw new WebApplicationException(e, response);
        } catch (IOException e) {
            log.error("defaultURL: " + defaultURL, e);
            Response response =
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                type(MediaType.TEXT_PLAIN).
                entity("IOError by server.\n" + e).build();
            throw new WebApplicationException(e, response);
        } catch (ApiProxy.OverQuotaException e) {
            log.error("defaultURL: " + defaultURL, e);
            Response response =
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                type(MediaType.TEXT_PLAIN).
                entity("Exceeded GAE Quota.\n" + e).build();
            throw new WebApplicationException(e, response);
        }

        if (pages == 0) {
            log.error("page is 0. lineNumber: {}, direction: {}, day: {}",
                      lineNumber, direction, day);
            Response response =
                Response.status(Response.Status.BAD_REQUEST).
                type(MediaType.TEXT_PLAIN).
                entity("Maybe bad Senku-Bango.").build();
            throw new WebApplicationException(response);
        }
        return pages;
    }

    private List<String> createArgumentURL(String lineNumber, String day,
                                           boolean reverse) {
        List<String> urlArgs = new ArrayList<String>();
        if (reverse) {
            urlArgs.addAll(createURLList(lineNumber, day,
                                         resources.getString(UP_STRING)));
            urlArgs.addAll(createURLList(lineNumber, day,
                                         resources.getString(DOWN_STRING)));
        } else {
            urlArgs.addAll(createURLList(lineNumber, day,
                                         resources.getString(DOWN_STRING)));
            urlArgs.addAll(createURLList(lineNumber, day,
                                         resources.getString(UP_STRING)));
        }
        return urlArgs;
    }

    private List<String> createURLList(String lineNumber, String day,
                                       String direction) {
        List<String> urls = new ArrayList<String>();
        int pages = countPages(lineNumber, day, direction);
        for (int i = 1; i <= pages; i++) {
            urls.add(resources.getString(DEFAULT_URL_STRING) + lineNumber
                     + direction + i + day + EXT_STRING);
        }
        return urls;
    }
}

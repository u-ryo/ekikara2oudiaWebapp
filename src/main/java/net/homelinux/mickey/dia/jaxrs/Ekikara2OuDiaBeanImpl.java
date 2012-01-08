package net.homelinux.mickey.dia.jaxrs;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
                             String startTime, String day, boolean reverse,
                             String referer) {
        log.info("url: {} processTables: {}\nreferer: {}",
                 new String[] {
                     resources.getString(DEFAULT_URL_STRING) + lineNumber
                     + resources.getString(DOWN_STRING) + "1" + day
                     + EXT_STRING, processTables, referer });
        log.debug("processTables: {}\nlineNumber: {}\nstartTime: {}\nday: {}"
                  + "\nreverse: {}", new Object[] { processTables, lineNumber,
                                                    startTime, day, reverse });
        checkParams(processTables, lineNumber, startTime, day);

        System.setProperty(PROCESS_TABLES_STRING, processTables);
        System.setProperty(KITEN_JIKOKU_STRING, startTime);

        List<String> urlArgs = createArgumentURL(lineNumber, day, reverse);

        log.debug("urlArgs: {}", urlArgs);

        ekikara2OuDia = new Ekikara2OuDia();
        for (String url : urlArgs) {
            try {
                ekikara2OuDia.process(url);
            } catch (Exception e) {
                log.error("lineNumber: " + lineNumber + ", processTables: "
                          + processTables + ", day: " + day, e);
                Response response =
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    type(MediaType.TEXT_PLAIN).entity(e.toString()).build();
                throw new WebApplicationException(e, response);
            }
        }
        log.info("title: {}\nupdateDate: {}", ekikara2OuDia.getTitle(),
                 ekikara2OuDia.getUpdateDate());
        Formatter formatter =
            new OuDiaFormatter(ekikara2OuDia.getTitle() + " "
                               + ekikara2OuDia.getUpdateDate(),
                               ekikara2OuDia.getAllStations(),
                               ekikara2OuDia.getDownTrains(),
                               ekikara2OuDia.getUpTrains(),
                               urlArgs + " " + ekikara2OuDia.getUpdateDate());
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
            .build();
    }

    private void checkParams(String processTables, String lineNumber,
                             String startTime, String day) {
        String message = null;
        if (!processTables.matches(PROCESS_TABLES_PATTERN_STRING)) {
            message = "「処理する表」の指定がまずい.";
        } else if (!lineNumber.matches(LINE_NUMBER_PATTERN_STRING)) {
            message = "線区番号が悪い.";
        } else if (!startTime.matches(START_TIME_PATTERN_STRING)) {
            message = "起点時刻の指定が悪い.";
        } else if (!day.matches(DAY_PATTERN_STRING)) {
            message = "曜日の指定が悪い.";
        }
        if (message != null) {
            log.error("checkParams error! {}\nprocessTables: {}\nlineNumber: "
                      + "{}\nstartTime: {}\nday: {}",
                      new String[] { message, processTables, lineNumber,
                                     startTime, day});
            Response response =
                Response.status(Response.Status.BAD_REQUEST).
                type(MediaType.TEXT_PLAIN).entity(message).build();
            throw new WebApplicationException(response);
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
                entity("指定された線区番号は見付からなかった.").build();
            throw new WebApplicationException(e, response);
        } catch (IOException e) {
            log.error("defaultURL: " + defaultURL, e);
            Response response =
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                type(MediaType.TEXT_PLAIN).
                entity("server側のIO error.").build();
            throw new WebApplicationException(e, response);
        }

        if (pages == 0) {
            log.error("page is 0. lineNumber: {}, direction: {}, day: {}",
                      new String[] { lineNumber, direction, day });
            Response response =
                Response.status(Response.Status.BAD_REQUEST).
                type(MediaType.TEXT_PLAIN).
                entity("指定の線区番号がおかしいと思われ.").build();
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

package arie.kal.covidtracker.services;

import arie.kal.covidtracker.models.LocationData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
/**
 *
 * @author ariep
 */
public class CovidDataService {

    private static String COVID_GIT_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationData> allData = new ArrayList<>();

    public List<LocationData> getAllData() {
        return allData;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *") //Execute Method on the First Hr of every day
    public void pullVirusData() throws IOException, InterruptedException {
        List<LocationData> newData = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(COVID_GIT_URL)).build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        //System.out.println(httpResponse.body()); //Data is a String. We want to convert it to StringReader for parsing.

        StringReader csvBodyReader = new StringReader(httpResponse.body()); //Parse csv
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationData locData = new LocationData();
            locData.setState(record.get("Province/State"));
            locData.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
            locData.setLatestTotalCases(latestCases);
            locData.setDiffFromPrevDay(latestCases - prevDayCases);
            newData.add(locData);



        }
        this.allData = newData;
    }

}

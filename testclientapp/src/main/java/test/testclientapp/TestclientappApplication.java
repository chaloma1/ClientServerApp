package test.testclientapp;

import com.sun.management.OperatingSystemMXBean;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


@SpringBootApplication
@EnableScheduling
public class TestclientappApplication {

    OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    DecimalFormat df = new DecimalFormat("###.##");
    double CPUusage;
    double memoryUsage;
    double freeMemory;
    Date dateOfMeasurement;

    public static final String clientLocation = "Hradec";

    // String s = InetAddress.getLocalHost().getHostName(); Can be used to get device hostname
    public static final String clientHostName = "Desktop01-hradec";

    public static void main(String[] args) {
        SpringApplication.run(TestclientappApplication.class, args);
    }



    @Scheduled(fixedRate = 15000)
    public void reportUsages(){
        CPUusage = osBean.getSystemCpuLoad() * 100;
        CPUusage = Double.parseDouble(df.format(CPUusage).replace(",","."));
        memoryUsage = (double)(osBean.getTotalPhysicalMemorySize() - osBean.getFreePhysicalMemorySize()) / 1024 / 1024 / 1024;
        memoryUsage = Double.parseDouble(df.format(memoryUsage).replace(",","."));
        freeMemory = (double)osBean.getFreePhysicalMemorySize() / 1024 / 1024 / 1024;
        freeMemory = Double.parseDouble(df.format(freeMemory).replace(",","."));

        dateOfMeasurement = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatDate = sdf.format(dateOfMeasurement);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("hostName", clientHostName);
            jsonObject.put("location", clientLocation);
            jsonObject.put("processorUsage", CPUusage);
            jsonObject.put("usedMemory", memoryUsage);
            jsonObject.put("freeMemory", freeMemory);
            jsonObject.put("dateOfMeasurement", formatDate);
            // System.out.println(jsonObject);

            String jsonOutputString = jsonObject.toString();

           /* Functional in HTTP, not in HTTPS
           URL url = new URL("https://localhost:8443/usageReport");

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonOutputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            */

            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();

            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(csf)
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory();

            requestFactory.setHttpClient(httpClient);

            RestTemplate restTemplate = new RestTemplate(requestFactory);

            String uri = "https://localhost:8443/usageReport";

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> httpEntity = new HttpEntity<String>(jsonOutputString, httpHeaders);
            ResponseEntity<String> entity = restTemplate.postForEntity(uri,httpEntity,String.class);
            System.out.println(entity.getStatusCode() + " " + "status entity");

            int status = entity.getStatusCode().value();

            if (status > 299){
                System.out.println("HW usage report failed to save" + " " + status + " " + "code response" + " " + entity.getStatusCode().getReasonPhrase() );
            }else {
                System.out.println("Success in sending usage report" + " " + status + " " + "code response");
            }

        }catch (Exception e){
            System.out.println("Couldnt make http request from client to server " +
                    "while sending usageReport" + " "+ e.getMessage());
        }



    }

}

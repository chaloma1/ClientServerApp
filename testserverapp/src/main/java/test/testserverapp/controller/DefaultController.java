package test.testserverapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import test.testserverapp.Repository.SystemUsageDataRepository;
import test.testserverapp.model.SystemUsageData;

import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.util.*;

@Controller
public class DefaultController {

    @Autowired
    private SystemUsageDataRepository systemUsageDataRepository;

    private SystemUsageData systemUsageData;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String defaultPage(HttpSession session){

        String[] hostNames = systemUsageDataRepository.findAllHostNames();

        List<SystemUsageData> dangerousUsages = systemUsageDataRepository.findDangerousUsages();


        System.out.println(dangerousUsages.size() + " " + "dangerousUsages");


        session.setAttribute("hostNames", hostNames);
        session.setAttribute("dangerousUsages", dangerousUsages);


        return "defaultJSP";

    }

    @RequestMapping(value = "/showUsage", method = RequestMethod.GET)
    public String showUsage(HttpSession session, Model model, @RequestParam(required = false) String hostName){

        if (session.getAttribute("hostNames") == null){
            return "redirect:./";
        }

        if(hostName == null || hostName.isEmpty() ){
            return "redirect:./";
        }


        List<SystemUsageData> dataList = systemUsageDataRepository.findLatestUsages(hostName);
        dataList = sortListByDate(dataList);



        model.addAttribute("systemUsageData", dataList);

        return "defaultJSP";

    }

    @RequestMapping(value = "/testInit", method = RequestMethod.GET)
    public String testInit() throws InterruptedException {
        systemUsageDataRepository.deleteAll();
        systemUsageDataRepository.flush();
        Random cpu = new Random();
        Random memory = new Random();
        DecimalFormat df = new DecimalFormat("###.##");


        // critical usage examples
        systemUsageDataRepository.save(new SystemUsageData("testCriticalUsage", "hradec", 95.0, 2.1, 0.5, new Date()));
        systemUsageDataRepository.save(new SystemUsageData("testCriticalUsage", "hradec", 91.0, 6.8, 4.5, new Date()));
        systemUsageDataRepository.save(new SystemUsageData("testCriticalUsage", "hradec", 50.0, 4.3, 0.7, new Date()));

        for (int i = 0; i<= 10; i++) {

            Date date = new Date();

            systemUsageData =
                    new SystemUsageData(
                            "testHostName",
                            "hradec",
                            Double.parseDouble(df.format(cpu.nextDouble()*100).replace(",",".")),
                            Double.parseDouble(df.format(memory.nextDouble()*15).replace(",",".")),
                            Double.parseDouble(df.format(memory.nextDouble()*15).replace(",",".")),
                            date);

            systemUsageDataRepository.save(systemUsageData);
            Thread.sleep(1000);
        }



        return "redirect:./";

    }


    @RequestMapping(value = "/usageReport", method = RequestMethod.POST)
    public ResponseEntity usageReport(@RequestBody SystemUsageData systemUsageData){

        System.out.println(systemUsageData.getHostName() + " " + "Json accepted");

        try {
            //systemUsageData = new SystemUsageData(hostName, location, Double.parseDouble(CPUusage), Double.parseDouble(usedMemory), Double.parseDouble(freeMemory), dateOfMeasurement);
            systemUsageDataRepository.save(systemUsageData);
            System.out.println("usageReport is saved in DB");

            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            System.out.println("Something went wrong with saving usageReport in DB" + " " + e.getMessage());
            return ResponseEntity.ok(HttpStatus.CONFLICT);
        }

    }

    public List<SystemUsageData> sortListByDate(List<SystemUsageData> list){

        Collections.sort(list, new Comparator<SystemUsageData>() {
            @Override
            public int compare(SystemUsageData o1, SystemUsageData o2) {
                return o1.getDateOfMeasurement().compareTo(o2.getDateOfMeasurement());
            }
        });

        return list;

    }

}

package pfuchs.syt4.westbahn.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pfuchs.syt4.westbahn.model.Bahnhof;
import pfuchs.syt4.westbahn.model.Strecke;
import pfuchs.syt4.westbahn.model.Zug;
import pfuchs.syt4.westbahn.repositories.BahnhofRepository;
import pfuchs.syt4.westbahn.repositories.ZugRepository;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class SearchController {
    @Autowired
    private ZugRepository zugRepo;
    @Autowired
    private BahnhofRepository repo;
    @Autowired
    private pfuchs.syt4.westbahn.web.Controller controller;

    @GetMapping(value = {"/", "/search"})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        zugRepo.deleteAll();
        if (repo.findAll().size() == 0) {
            Bahnhof westbhf = new Bahnhof("Wien Westbhf", 0, 0, 0, true);
            repo.save(westbhf);
            Bahnhof huetteldorf = new Bahnhof("Wien Hütteldorf", 3, 30, 20, false);
            repo.save(huetteldorf);
            Bahnhof poelten = new Bahnhof("St. Pölten", 9, 100, 60, false);
            repo.save(poelten);
            Bahnhof amstetten = new Bahnhof("Amstetten", 12, 130, 80, false);
            repo.save(amstetten);
            Bahnhof linz = new Bahnhof("Linz", 17, 200, 110, false);
            repo.save(linz);
            Bahnhof wels = new Bahnhof("Wels", 20, 240, 130, false);
            repo.save(wels);
            Bahnhof attnang = new Bahnhof("Attnang-Puchheim", 23, 270, 150, false);
            repo.save(attnang);
            Bahnhof salzburg = new Bahnhof("Salzburg", 26, 300, 180, true);
            repo.save(salzburg);

        }
        Bahnhof westbhf = repo.findByName("Wien Westbhf");
        Bahnhof salzburg = repo.findByName("Salzburg");

        if (zugRepo.findAll().size()==0)
            for (int i = 0; i < 7; ++i) {
                Zug wien_salzburg = new Zug(westbhf, salzburg);
                Zug salzburg_wien = new Zug(salzburg, westbhf);
                Calendar today = Calendar.getInstance();
                today.set(Calendar.DATE, today.get(Calendar.DATE)+i);
                today.set(Calendar.HOUR_OF_DAY, 15);
                today.set(Calendar.MINUTE, 30);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);
                wien_salzburg.setStartZeit(today.getTime());
                today.set(Calendar.HOUR_OF_DAY, 19);
                today.set(Calendar.MINUTE, 0);
                salzburg_wien.setStartZeit(today.getTime());
                zugRepo.save(wien_salzburg);
                zugRepo.save(salzburg_wien);
            }

        if (controller.is_logged_in())
            modelAndView.addObject("logged_in", true);
        modelAndView.setViewName("search");
        modelAndView.addObject("bahnhoefe", repo.findAll());
        modelAndView.addObject("zuege", zugRepo.findAll());
        return modelAndView;
    }

    @GetMapping("/update/search")
    public String update(@RequestParam(name="from") String from,
                         @RequestParam(name="to") String to,
                         @RequestParam(name="date", required = false) String date,
                         Model model) {
        int indexFrom = -1;
        int indexTo = -1;
        for (int i = 0; i < repo.findAll().size(); ++i)
            if (repo.findAll().get(i).getName().equalsIgnoreCase(from))
                indexFrom = i;
            else if (repo.findAll().get(i).getName().equalsIgnoreCase(to))
                indexTo = i;
        Set<Zug> zuege;
        if (indexFrom < indexTo)
            zuege = zugRepo.findAllFromWien();
        else zuege = zugRepo.findAllFromSalzburg();

        List<Zug> zuegeAmTag = zugRepo.findByMatchMonthAndMatchDay(date);
        if (date != null && !date.equals("")) {
            Set<Zug> z2 = new HashSet<>();
            for (Zug z : zuege)
                if (zuegeAmTag.contains(z))
                    z2.add(z);
            zuege = z2;
        }


        Strecke strecke = new Strecke(repo.findAll().get(indexFrom), repo.findAll().get(indexTo));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        List<String> abfahrten = new LinkedList<>();
        List<String> dauern = new LinkedList<>();
        for (Zug z : zuege) {
            Date zeitpunkt = new Date(z.getStartZeit().getTime()+
                    Math.abs(z.getStart().getAbsZeitEntfernung()-strecke.getStart().getAbsZeitEntfernung())*60000);
            abfahrten.add(sdf.format(zeitpunkt));
            int dauer = Math.abs(strecke.getStart().getAbsZeitEntfernung()-strecke.getEnde().getAbsZeitEntfernung());
            dauern.add(dauer/60 + ":"+((dauer%60<10)?"0"+dauer%60:dauer%60));
        }
        model.addAttribute("abfahrten", abfahrten);
        model.addAttribute("dauern", dauern);
        model.addAttribute("zuege", zuege);
        model.addAttribute("strecke", strecke);
        return "update";
    }
}

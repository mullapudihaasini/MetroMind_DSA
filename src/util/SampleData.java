package util;

import model.ServiceAlert;
import model.Station;
import model.TrainSchedule;
import model.TransitEvent;
import service.MetroGraph;
import service.TrainService;

import java.util.ArrayList;
import java.util.List;

/**
 * Initializes realistic, fictional sample data for the Metro Mind platform.
 * 
 * NOTICE:
 * All stations, lines, distances, and train timetables created in this class are
 * strictly fictional sample representations intended for academic DSA algorithmic demonstration.
 */
public class SampleData {

    private SampleData() {
        // Static utility
    }

    /**
     * Populates the MetroGraph with 14 stations across 4 interconnecting metro lines.
     */
    public static void populateMetroNetwork(MetroGraph graph) {
        // 1. Instantiate Stations
        Station st01 = new Station("ST01", "Central", "Red Line");
        Station st02 = new Station("ST02", "City Hall", "Red Line");
        Station st03 = new Station("ST03", "Museum", "Red Line");
        Station st04 = new Station("ST04", "University", "Red Line");
        Station st05 = new Station("ST05", "Tech Park", "Red Line");
        Station st06 = new Station("ST06", "Airport", "Red Line");

        Station st07 = new Station("ST07", "Terminal", "Blue Line");
        Station st08 = new Station("ST08", "Market", "Blue Line");
        Station st09 = new Station("ST09", "Stadium", "Blue Line");
        Station st10 = new Station("ST10", "Lake View", "Blue Line");

        Station st11 = new Station("ST11", "Botanical Garden", "Green Line");
        Station st12 = new Station("ST12", "Financial District", "Green Line");
        Station st13 = new Station("ST13", "Harbor Point", "Green Line");

        Station st14 = new Station("ST14", "Innovation Hub", "Yellow Line");

        // Add all to graph
        graph.addStation(st01);
        graph.addStation(st02);
        graph.addStation(st03);
        graph.addStation(st04);
        graph.addStation(st05);
        graph.addStation(st06);
        graph.addStation(st07);
        graph.addStation(st08);
        graph.addStation(st09);
        graph.addStation(st10);
        graph.addStation(st11);
        graph.addStation(st12);
        graph.addStation(st13);
        graph.addStation(st14);

        // 2. Add Red Line Connections (North-South Corridor)
        graph.addConnection("ST01", "ST02", 2.10, "Red Line", 16000, true); // Central <-> City Hall
        graph.addConnection("ST02", "ST03", 1.80, "Red Line", 15000, true); // City Hall <-> Museum
        graph.addConnection("ST03", "ST04", 3.40, "Red Line", 15000, true); // Museum <-> University
        graph.addConnection("ST04", "ST05", 4.20, "Red Line", 18000, true); // University <-> Tech Park
        graph.addConnection("ST05", "ST06", 5.60, "Red Line", 18000, true); // Tech Park <-> Airport

        // 3. Add Blue Line Connections (East-West Corridor)
        graph.addConnection("ST07", "ST08", 2.80, "Blue Line", 14000, true); // Terminal <-> Market
        graph.addConnection("ST08", "ST01", 1.90, "Blue Line", 20000, true); // Market <-> Central
        graph.addConnection("ST01", "ST09", 3.10, "Blue Line", 19000, true); // Central <-> Stadium
        graph.addConnection("ST09", "ST10", 4.50, "Blue Line", 12000, true); // Stadium <-> Lake View

        // 4. Add Green Line Connections (Outer Orbital Corridor)
        graph.addConnection("ST11", "ST04", 2.50, "Green Line", 11000, true); // Botanical Garden <-> University
        graph.addConnection("ST04", "ST12", 3.70, "Green Line", 13000, true); // University <-> Financial District
        graph.addConnection("ST12", "ST09", 2.90, "Green Line", 13000, true); // Financial District <-> Stadium
        graph.addConnection("ST09", "ST13", 4.00, "Green Line", 10000, true); // Stadium <-> Harbor Point

        // 5. Add Yellow Line Connections (Express Airport Link)
        graph.addConnection("ST01", "ST14", 6.20, "Yellow Line", 22000, true); // Central <-> Innovation Hub
        graph.addConnection("ST14", "ST06", 5.10, "Yellow Line", 22000, true); // Innovation Hub <-> Airport
    }

    /**
     * Populates initial train timetable in the TrainService PriorityQueue.
     */
    public static void populateTrainSchedules(TrainService trainService) {
        trainService.addSchedule(new TrainSchedule("TR-101", "Red Line", "ST01", "Central", "ST06", "Airport", "06:15", "On Time"));
        trainService.addSchedule(new TrainSchedule("TR-102", "Red Line", "ST06", "Airport", "ST01", "Central", "06:30", "On Time"));
        trainService.addSchedule(new TrainSchedule("TR-201", "Blue Line", "ST07", "Terminal", "ST10", "Lake View", "07:00", "On Time"));
        trainService.addSchedule(new TrainSchedule("TR-202", "Blue Line", "ST10", "Lake View", "ST07", "Terminal", "07:15", "On Time"));
        trainService.addSchedule(new TrainSchedule("TR-301", "Green Line", "ST11", "Botanical Garden", "ST13", "Harbor Point", "07:30", "On Time"));
        trainService.addSchedule(new TrainSchedule("TR-302", "Green Line", "ST13", "Harbor Point", "ST11", "Botanical Garden", "07:45", "On Time"));
        trainService.addSchedule(new TrainSchedule("TR-401", "Yellow Line", "ST01", "Central", "ST06", "Airport", "08:00", "Boarding"));
        trainService.addSchedule(new TrainSchedule("TR-402", "Yellow Line", "ST06", "Airport", "ST01", "Central", "08:20", "On Time"));
        trainService.addSchedule(new TrainSchedule("TR-103", "Red Line", "ST02", "City Hall", "ST05", "Tech Park", "08:45", "On Time"));
        trainService.addSchedule(new TrainSchedule("TR-203", "Blue Line", "ST01", "Central", "ST09", "Stadium", "09:10", "Delayed"));
    }

    /**
     * Provides sample service alerts for string matching and multi-pattern automaton demonstrations.
     */
    public static List<ServiceAlert> getSampleServiceAlerts() {
        List<ServiceAlert> alerts = new ArrayList<>();
        alerts.add(new ServiceAlert("ALT-01", "Red Line", "WARNING",
                "Track maintenance scheduled between Museum and University. Minor delay expected.", "08:00"));
        alerts.add(new ServiceAlert("ALT-02", "Blue Line", "INFO",
                "Signal upgrade completed at Central. Normal train headway restored across all tracks.", "08:15"));
        alerts.add(new ServiceAlert("ALT-03", "Yellow Line", "CRITICAL",
                "Emergency signal failure near Innovation Hub. Express trains temporarily running at reduced speed.", "08:30"));
        alerts.add(new ServiceAlert("ALT-04", "Green Line", "INFO",
                "Special event shuttle operating between Stadium and Financial District.", "08:45"));
        alerts.add(new ServiceAlert("ALT-05", "Red Line", "WARNING",
                "Overcrowding alert at Tech Park during peak morning hours. Boarding queues active.", "09:00"));
        return alerts;
    }

    /**
     * Generates a sample stream of transit events for randomized and parallel algorithm testing.
     */
    public static List<TransitEvent> getSampleTransitEvents() {
        List<TransitEvent> events = new ArrayList<>();
        events.add(new TransitEvent(1001, "ST01", "PASSENGER_ENTRY", 450, 1697150000L));
        events.add(new TransitEvent(1002, "ST04", "PASSENGER_ENTRY", 320, 1697150060L));
        events.add(new TransitEvent(1003, "ST06", "PASSENGER_EXIT", 580, 1697150120L));
        events.add(new TransitEvent(1004, "ST01", "DISPATCH", 120, 1697150180L));
        events.add(new TransitEvent(1005, "ST09", "PASSENGER_ENTRY", 290, 1697150240L));
        events.add(new TransitEvent(1006, "ST02", "PASSENGER_ENTRY", 180, 1697150300L));
        events.add(new TransitEvent(1007, "ST14", "SENSOR_PING", 95, 1697150360L));
        events.add(new TransitEvent(1008, "ST05", "PASSENGER_ENTRY", 640, 1697150420L));
        events.add(new TransitEvent(1009, "ST08", "PASSENGER_EXIT", 210, 1697150480L));
        events.add(new TransitEvent(1010, "ST03", "PASSENGER_ENTRY", 140, 1697150540L));
        return events;
    }
}

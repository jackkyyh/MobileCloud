package com.urop.server;

//
//        import java.util.PriorityQueue;
//        import java.util.LinkedList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

// import static util.Config.serverListeningPort;

/**
 * The server of TowerDefence game.
 * Visit http://localhost:3111/hall to see hall of fame.
 */
@SpringBootApplication
public class Server {

    static String serverListeningPort = "9544";

    private int[] arrayToSort = {6, 2, 7, 1, 3, 5, 2, 7, 2, 6, 2, 1, 5, 8, 4, 2, 6, 8};

    /**
     * Entry point
     *
     * @param args useless.
     */
    public static void main(String[] args) {
        System.out.println("Server started...");
        SpringApplication app = new SpringApplication(Server.class);
        app.setDefaultProperties(Collections.<String, Object>singletonMap("server.port", serverListeningPort));
        app.run(args);
    }

    @RestController
    private class RecordController {
        @RequestMapping(value = "/submit", method = RequestMethod.POST)
        public Integer addRecord(@RequestBody int[] score, HttpServletRequest request) {
            // Record record = new Record(score, request.getRemoteAddr());
            //top10.add(record);
            //if(top10.size() > 10){
            //   Record rec = top10.poll();
            //  System.out.println(rec + " removed");
            //}
            System.out.println("submission recieved: " + score.length + " @ " + request.getRemoteAddr());
            return 200;
        }

        @RequestMapping(value = "/request", method = RequestMethod.GET)
        public int[] getAll() {
            //LinkedList top = new LinkedList<Int>(arr);
            return arrayToSort;
        }
    }
}
package dev.orderflow.common.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
@Profile("dev")
public class DevController {


    @GetMapping("/slow")
    public ResponseEntity<String> slow(@RequestParam(defaultValue = "ms")  int ms) {

        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.ok("bad");
        }

        return ResponseEntity.ok("Success after " + ms + "ms");
    }

}
